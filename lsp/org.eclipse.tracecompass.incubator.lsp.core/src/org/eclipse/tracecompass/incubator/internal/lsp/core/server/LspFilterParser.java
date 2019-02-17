/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.eclipse.lsp4j.Position;

/**
 * Proposes completions to the input string in the globalFilter box, and locates
 * syntax errors if present
 *
 * @author David-Alexandre Beaupre and Remi Croteau
 *
 */
public class LspFilterParser {

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

    public static void runAllChecks(String input) {
        parseFilter(input);
    }

    private static boolean isValidTextChar(char character) {
        return SPECIAL_TEXT.contains(character) || Character.isLetterOrDigit(character);
    }

    private static void parseFilter(String input) {
        int state = STATE_START_TEXT_1;
        Stack<Position> parenthesesPositions = new Stack();
        int i = 0;
        System.out.println();
        System.out.println("Input = " + input);
        while (i < input.length()) {
            char character = input.charAt(i);
            System.out.println("State = " + state + ", index = " + i + ", character = " + character);
            switch (state) {
            case STATE_START_TEXT_1:
                if (isValidTextChar(character)) {
                    state = STATE_TEXT_1;
                } else if (WHITESPACES.contains(character)) {
                    i++;
                } else if (character == LEFT_PARENTHESIS) {
                    parenthesesPositions.push(new Position(0, i));
                    i++;
                } else {
                    // log-error + parentheses right
                    System.out.println("ERROR");
                }
                break;
            case STATE_TEXT_1:
                if (WHITESPACES.contains(character)) {
                    state = STATE_START_OP_SEP;
                    i++;
                } else if (OPERATOR_CUES.contains(character)) {
                    state = STATE_OPERATOR;
                } else if (SEPARATOR_CUES.contains(character)) {
                    state = STATE_SEPARATOR;
                } else if (isValidTextChar(character)) {
                    i++;
                } else if (character == RIGHT_PARENTHESIS) {
                    try {
                        parenthesesPositions.pop();
                    } catch (EmptyStackException e) {
                        // log-error
                        System.out.println("ERROR RIGHT PARENTHESIS");
                    }
                    i++;
                    state = STATE_START_SEPARATOR;
                } else {
                    // log-error + parentheses
                    System.out.println("ERROR");
                }
                break;
            case STATE_START_TEXT_2:
                if (isValidTextChar(character)) {
                    state = STATE_TEXT_2;
                } else if (WHITESPACES.contains(character)) {
                    i++;
                } else {
                    // log-error + parentheses
                    System.out.println("ERROR");
                }
                break;
            case STATE_TEXT_2:
                if (WHITESPACES.contains(character)) {
                    state = STATE_START_SEPARATOR;
                    i++;
                } else if (SEPARATOR_CUES.contains(character)) {
                    state = STATE_SEPARATOR;
                } else if (isValidTextChar(character)) {
                    i++;
                } else if (character == RIGHT_PARENTHESIS) {
                    try {
                        parenthesesPositions.pop();
                    } catch (EmptyStackException e) {
                        // log-error
                        System.out.println("ERROR RIGHT PARENTHESIS");
                    }
                    i++;
                    state = STATE_START_SEPARATOR;
                } else {
                    // log-error
                    System.out.println("ERROR");
                }
                break;
            case STATE_START_OP_SEP:
                if (WHITESPACES.contains(character)) {
                    i++;
                } else if (OPERATOR_CUES.contains(character) || character == MATCHES_OP.charAt(0) || character == CONTAINS_OP.charAt(0)) {
                    state = STATE_OPERATOR;
                } else if (SEPARATOR_CUES.contains(character) || character == OR_OP.charAt(0)) {
                    state = STATE_SEPARATOR;
                } else if (character == RIGHT_PARENTHESIS) {
                    state = STATE_START_SEPARATOR;
                } else {
                    // log-error + parentheses
                    System.out.println("ERROR");
                }
                break;
            case STATE_OPERATOR: {
                boolean matches = false;
                for (int j = 0; j < OPERATORS.size(); j++) {
                    String op = OPERATORS.get(j);
                    matches = input.regionMatches(i, op, 0, op.length());
                    if (matches) {
                        i += op.length();
                        state = STATE_START_TEXT_2;
                        break;
                    }
                }
                if (!matches) {
                    // log-error
                    System.out.println("ERROR");
                }
            break;
            }
            case STATE_START_SEPARATOR:
                if (WHITESPACES.contains(character)) {
                    i++;
                } else if (SEPARATOR_CUES.contains(character) || character == OR_OP.charAt(0)) {
                    state = STATE_SEPARATOR;
                } else if (character == RIGHT_PARENTHESIS) {
                    try {
                        parenthesesPositions.pop();
                    } catch (EmptyStackException e) {
                        // log-error
                        System.out.println("ERROR RIGHT PARENTHESIS");
                    }
                    i++;
                } else {
                    // log-error + parentheses
                    System.out.println("ERROR");
                }
                break;
            case STATE_SEPARATOR: {
                boolean matches = false;
                for (int j = 0; j < SEPARATORS.size(); j++) {
                    String sep = SEPARATORS.get(j);
                    matches = input.regionMatches(i, sep, 0, sep.length());
                    if (matches) {
                        i += sep.length();
                        state = STATE_START_TEXT_1;
                        break;
                    }
                }
                if (!matches) {
                    // log-error
                    System.out.println("ERROR");
                }
                break;
            }
            default:
                break;
            }

        }
        if (!((state == STATE_START_TEXT_1 /*&& token.empty()*/) || state == STATE_TEXT_1 || state == STATE_TEXT_2 ||
                state == STATE_START_SEPARATOR || state == STATE_START_OP_SEP)) {
            // log-error
            System.out.println("ERROR INVALID END STATE");
        }
    }
}
