/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.DiagnosticCodes;

/**
 * Proposes completions to the input string in the globalFilter box, and locates
 * syntax errors if present
 *
 * @author David-Alexandre Beaupre and Remi Croteau
 *
 */
public class LspFilterParser {

    public static class FilterToken {
        public enum TokenType {
            SYNTACTIC, FIELD, VALUE
        }

        private String fInput;
        private Range fRange;
        private TokenType fType;

        public FilterToken(String input, Range range, TokenType type) {
            fInput = input;
            fRange = range;
            fType = type;
        }

        public String getInput() {
            return fInput;
        }

        public Range getRange() {
            return fRange;
        }

        public TokenType getType() {
            return fType;
        }

        public void setInput(String input) {
            fInput = input;
        }

        public void setRange(Range range) {
            fRange = range;
        }

        public void setTokenType(TokenType type) {
            fType = type;
        }
    }

    private static final int STATE_START_TEXT_1 = 0;
    private static final int STATE_START_TEXT_2 = 1;
    private static final int STATE_TEXT_1 = 2;
    private static final int STATE_TEXT_2 = 3;
    private static final int STATE_START_OP_SEP = 4;
    private static final int STATE_START_SEPARATOR = 5;
    private static final int STATE_OPERATOR = 6;
    private static final int STATE_SEPARATOR = 7;

    private static final char LEFT_PARENTHESIS = '(';
    private static final char RIGHT_PARENTHESIS = ')';
    private static final String MATCHES_OP = "matches";
    private static final String CONTAINS_OP = "contains";
    private static final String OR_OP = "||";
    private static final List<String> OPERATORS = Arrays.asList("!=", "==", "<", ">", MATCHES_OP, CONTAINS_OP);
    private static final List<String> SEPARATORS = Arrays.asList("&&", OR_OP);
    private static final List<Character> WHITESPACES = Arrays.asList(' ', '\t', '\r', '\n');
    private static final List<Character> SPECIAL_TEXT = Arrays.asList('-', '_', '[', ']', '.', '*', '$', '^', '|', '\\', '{', '}', '?', '+');
    private static final List<Character> OPERATOR_CUES = Arrays.asList('!', '=', '<', '>');
    private static final List<Character> SEPARATOR_CUES = Arrays.asList('&');
    private static final String source = "LSP Filter Parser";
    private Stack<Position> parenthesesPositions;
    List<Diagnostic> diagnostics;
    private int index;

    public LspFilterParser() {
        parenthesesPositions = new Stack();
        diagnostics = new ArrayList();
        index = 0;
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    private static boolean isValidTextChar(char character) {
        return SPECIAL_TEXT.contains(character) || Character.isLetterOrDigit(character);
    }

    private void logDiagnostic(int start, int end, String errorCode) {
        Range range = new Range(new Position(0, start), new Position(0, end));
        String message = DiagnosticCodes.getMessage(errorCode);
        DiagnosticSeverity severity = DiagnosticCodes.getSeverity(errorCode);
        diagnostics.add(new Diagnostic(range, message, severity, source, errorCode));
    }

    private void updateParenthesisState(char parenthesis) {
        if (parenthesis == LEFT_PARENTHESIS) {
            parenthesesPositions.push(new Position(0, index));
        } else if (parenthesis == RIGHT_PARENTHESIS) {
            try {
                parenthesesPositions.pop();
            } catch (EmptyStackException e) {
                logDiagnostic(index, index + 1, DiagnosticCodes.RIGHT_PARENTHESIS_UNBALANCED);
            }
        }
    }

    public void parseFilter(String input) {
        parenthesesPositions.clear();
        diagnostics.clear();
        index = 0;
        int state = STATE_START_TEXT_1;
        while (index < input.length()) {
            char character = input.charAt(index);
            switch (state) {
            case STATE_START_TEXT_1:
                if (isValidTextChar(character)) {
                    state = STATE_TEXT_1;
                } else if (WHITESPACES.contains(character)) {
                    index++;
                } else if (character == LEFT_PARENTHESIS) {
                    updateParenthesisState(character);
                    index++;
                } else {
                    if (character == RIGHT_PARENTHESIS) {
                        updateParenthesisState(character);
                        logDiagnostic(index, index + 1, DiagnosticCodes.RIGHT_PARENTHESIS_UNEXPECTED);
                    } else if (OPERATOR_CUES.contains(character) || SEPARATOR_CUES.contains(character)) {
                        logDiagnostic(index, index + 1, DiagnosticCodes.CHARACTER_UNEXPECTED);
                    } else {
                        logDiagnostic(index, index + 1, DiagnosticCodes.ILLEGAL_CHARACTER);
                    }
                    index++;
                }
                break;
            case STATE_TEXT_1:
                if (WHITESPACES.contains(character)) {
                    state = STATE_START_OP_SEP;
                    index++;
                } else if (OPERATOR_CUES.contains(character)) {
                    state = STATE_OPERATOR;
                } else if (SEPARATOR_CUES.contains(character)) {
                    state = STATE_SEPARATOR;
                } else if (isValidTextChar(character)) {
                    index++;
                } else if (character == RIGHT_PARENTHESIS) {
                    updateParenthesisState(character);
                    index++;
                    state = STATE_START_SEPARATOR;
                } else {
                    if (character == LEFT_PARENTHESIS) {
                        updateParenthesisState(character);
                        logDiagnostic(index, index + 1, DiagnosticCodes.LEFT_PARENTHESIS_UNEXPECTED);
                    } else {
                        logDiagnostic(index, index + 1, DiagnosticCodes.ILLEGAL_CHARACTER);
                    }
                    index++;
                }
                break;
            case STATE_START_TEXT_2:
                if (isValidTextChar(character)) {
                    state = STATE_TEXT_2;
                } else if (WHITESPACES.contains(character)) {
                    index++;
                } else {
                    if (character == RIGHT_PARENTHESIS) {
                        updateParenthesisState(character);
                        logDiagnostic(index, index + 1, DiagnosticCodes.RIGHT_PARENTHESIS_UNEXPECTED);
                    } else if (character == LEFT_PARENTHESIS) {
                        updateParenthesisState(character);
                        logDiagnostic(index, index + 1, DiagnosticCodes.LEFT_PARENTHESIS_UNEXPECTED);
                    } else {
                        logDiagnostic(index, index + 1, DiagnosticCodes.ILLEGAL_CHARACTER);
                    }
                    index++;
                }
                break;
            case STATE_TEXT_2:
                if (WHITESPACES.contains(character)) {
                    state = STATE_START_SEPARATOR;
                    index++;
                } else if (SEPARATOR_CUES.contains(character)) {
                    state = STATE_SEPARATOR;
                } else if (isValidTextChar(character)) {
                    index++;
                } else if (character == RIGHT_PARENTHESIS) {
                    updateParenthesisState(character);
                    index++;
                    state = STATE_START_SEPARATOR;
                } else {
                    if (character == LEFT_PARENTHESIS) {
                        updateParenthesisState(character);
                        logDiagnostic(index, index + 1, DiagnosticCodes.LEFT_PARENTHESIS_UNEXPECTED);
                    } else if (OPERATOR_CUES.contains(character) || SEPARATOR_CUES.contains(character)) {
                        logDiagnostic(index, index + 1, DiagnosticCodes.CHARACTER_UNEXPECTED);
                    } else {
                        logDiagnostic(index, index + 1, DiagnosticCodes.ILLEGAL_CHARACTER);
                    }
                    index++;
                }
                break;
            case STATE_START_OP_SEP:
                if (WHITESPACES.contains(character)) {
                    index++;
                } else if (OPERATOR_CUES.contains(character) || character == MATCHES_OP.charAt(0) || character == CONTAINS_OP.charAt(0)) {
                    state = STATE_OPERATOR;
                } else if (SEPARATOR_CUES.contains(character) || character == OR_OP.charAt(0)) {
                    state = STATE_SEPARATOR;
                } else if (character == RIGHT_PARENTHESIS) {
                    updateParenthesisState(character);
                    index++;
                    state = STATE_START_SEPARATOR;
                } else {
                    if (character == LEFT_PARENTHESIS) {
                        updateParenthesisState(character);
                        logDiagnostic(index, index + 1, DiagnosticCodes.OPERATOR_SEPARATOR_EXPECTED);
                    } else if (isValidTextChar(character)) {
                        logDiagnostic(index, index + 1, DiagnosticCodes.OPERATOR_SEPARATOR_EXPECTED);
                    } else {
                        logDiagnostic(index, index + 1, DiagnosticCodes.ILLEGAL_CHARACTER);
                    }
                    index++;
                }
                break;
            case STATE_OPERATOR: {
                boolean matches = false;
                for (int j = 0; j < OPERATORS.size(); j++) {
                    String op = OPERATORS.get(j);
                    matches = input.regionMatches(index, op, 0, op.length());
                    if (matches) {
                        index += op.length();
                        state = STATE_START_TEXT_2;
                        break;
                    }
                }
                if (!matches) {
                    logDiagnostic(index, index + 1, DiagnosticCodes.INVALID_OPERATOR);
                    index++;
                }
                break;
            }
            case STATE_START_SEPARATOR:
                if (WHITESPACES.contains(character)) {
                    index++;
                } else if (SEPARATOR_CUES.contains(character) || character == OR_OP.charAt(0)) {
                    state = STATE_SEPARATOR;
                } else if (character == RIGHT_PARENTHESIS) {
                    updateParenthesisState(character);
                    index++;
                } else {
                    if (character == LEFT_PARENTHESIS) {
                        updateParenthesisState(character);
                        logDiagnostic(index, index + 1, DiagnosticCodes.LEFT_PARENTHESIS_UNEXPECTED);
                    } else {
                        logDiagnostic(index, index + 1, DiagnosticCodes.SEPARATOR_EXPECTED);
                    }
                    index++;
                }
                break;
            case STATE_SEPARATOR: {
                boolean matches = false;
                for (int j = 0; j < SEPARATORS.size(); j++) {
                    String sep = SEPARATORS.get(j);
                    matches = input.regionMatches(index, sep, 0, sep.length());
                    if (matches) {
                        index += sep.length();
                        state = STATE_START_TEXT_1;
                        break;
                    }
                }
                if (!matches) {
                    logDiagnostic(index, index + 1, DiagnosticCodes.INVALID_SEPARATOR);
                    index++;
                }
                break;
            }
            default:
                break;
            }

        }
        if (!(state == STATE_TEXT_1 || state == STATE_TEXT_2 || state == STATE_START_SEPARATOR ||
                state == STATE_START_OP_SEP) || !parenthesesPositions.empty()) {
            logDiagnostic(index, index + 1, DiagnosticCodes.INVALID_ENDSTATE);
        }
    }
}
