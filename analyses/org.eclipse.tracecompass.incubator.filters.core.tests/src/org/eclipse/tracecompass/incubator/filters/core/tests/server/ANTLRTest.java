/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.filters.core.tests.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.eclipse.lsp4j.Color;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.tracecompass.incubator.internal.filters.core.server.AutoCompletion;
import org.eclipse.tracecompass.incubator.internal.filters.core.server.SyntaxHighlighting;
import org.eclipse.tracecompass.incubator.internal.filters.core.server.FilterValidation;
import org.junit.Test;

/**
 * This class tests server-side class using antlr for syntax color, validation
 * and autocompletion
 *
 * @author Maxime Thibault
 * @author David-Alexandre Beaupre
 *
 */
public class ANTLRTest {

    @Test
    public void syntaxColorTests() throws IOException {
        String text = "TID";
        String op = "==";
        String separator = "&&";
        String full = text + " " + op + " " + separator;
        List<ColorInformation> ci = SyntaxHighlighting.getColorInformationList(full);

        ColorInformation ci1 = ci.get(0);
        assertEquals(new Color(0, 0, 0, 1), ci1.getColor());
        assertEquals(0, ci1.getRange().getStart().getCharacter());
        assertEquals(2, ci1.getRange().getEnd().getCharacter());

        ColorInformation ci2 = ci.get(1);
        assertEquals(new Color(0.3, 0.3, 1, 1), ci2.getColor());
        assertEquals(4, ci2.getRange().getStart().getCharacter());
        assertEquals(5, ci2.getRange().getEnd().getCharacter());

        ColorInformation ci3 = ci.get(2);
        assertEquals(new Color(0.9, 0.5, 0.1, 1), ci3.getColor());
        assertEquals(7, ci3.getRange().getStart().getCharacter());
        assertEquals(8, ci3.getRange().getEnd().getCharacter());

    }

    @Test
    public void validationMismatchedTokenException() throws IOException, RecognitionException {

        List<Diagnostic> diagnostics = FilterValidation.validate("TID = 123");
        int lineStart = diagnostics.get(0).getRange().getStart().getLine();
        int offsetStart = diagnostics.get(0).getRange().getStart().getCharacter();
        int lineEnd = diagnostics.get(0).getRange().getEnd().getLine();
        int offsetEnd = diagnostics.get(0).getRange().getEnd().getCharacter();

        // We expect antlr to see mismatchedTokenException at range (4, 5)
        // because '=' must equals '==' instead
        int lineExpected = 0;
        int startOffsetExpected = 4;
        int endOffsetExpected = 5;
        assertEquals(lineExpected, lineStart);
        assertEquals(lineExpected, lineEnd);
        assertEquals(startOffsetExpected, offsetStart);
        assertEquals(endOffsetExpected, offsetEnd);

        // Detected by both lexer and parser. It is irrelevant, but client
        // should be able to display only one of them
        assertEquals(diagnostics.size(), 1);

    }

    @Test
    public void validationNoViableAltException() throws IOException, RecognitionException {

        String str = "TID == ";

        List<Diagnostic> diagnostics = FilterValidation.validate(str);
        int lineStart = diagnostics.get(0).getRange().getStart().getLine();
        int offsetStart = diagnostics.get(0).getRange().getStart().getCharacter();
        int lineEnd = diagnostics.get(0).getRange().getEnd().getLine();
        int offsetEnd = diagnostics.get(0).getRange().getEnd().getCharacter();

        // We expect antlr to see NoViableAltException at position 6 because there is no value after the ==
        int lineExpected = 0;
        int startOffsetExpected = 0;
        int endOffsetExpected = str.length();
        assertEquals(lineExpected, lineStart);
        assertEquals(lineExpected, lineEnd);
        assertEquals(startOffsetExpected, offsetStart);
        assertEquals(endOffsetExpected, offsetEnd);

        assertEquals(diagnostics.size(), 1);

    }

    @Test
    public void validationSimpleStringNoErrors() throws IOException, RecognitionException {
        String str = "PID == 42";
        List<Diagnostic> diagnostics = FilterValidation.validate(str);
        assertEquals(diagnostics.size(), 0);
    }

    @Test
    public void validationComplexStringNoErrors() throws IOException, RecognitionException {
        String str = "TID < 12 && (PID == 42 && (Ericsson > 1 || Poly matches 2))";
        List<Diagnostic> diagnostics = FilterValidation.validate(str);
        assertEquals(diagnostics.size(), 0);
    }

    @Test
    public void validationMissingClosingParentheseOneChar() throws IOException, RecognitionException {
        String str = "(TID == 1";

        List<Diagnostic> diagnostics = FilterValidation.validate(str);
        int lineStart = diagnostics.get(0).getRange().getStart().getLine();
        int offsetStart = diagnostics.get(0).getRange().getStart().getCharacter();
        int lineEnd = diagnostics.get(0).getRange().getEnd().getLine();
        int offsetEnd = diagnostics.get(0).getRange().getEnd().getCharacter();

        // We expect antlr to see mismatchedTokenException at range (8, 9)
        // because there is no closing parenthese after last character
        int lineExpected = 0;
        int startOffsetExpected = 8;
        int endOffsetExpected = 9;
        assertEquals(lineExpected, lineStart);
        assertEquals(lineExpected, lineEnd);
        assertEquals(startOffsetExpected, offsetStart);
        assertEquals(endOffsetExpected, offsetEnd);

        assertEquals(diagnostics.size(), 1);

    }

    @Test
    public void validationMissingClosingParentheseMultipleChars() throws IOException, RecognitionException {
        String str = "(TID == 123456789";

        List<Diagnostic> diagnostics = FilterValidation.validate(str);
        int lineStart = diagnostics.get(0).getRange().getStart().getLine();
        int offsetStart = diagnostics.get(0).getRange().getStart().getCharacter();
        int lineEnd = diagnostics.get(0).getRange().getEnd().getLine();
        int offsetEnd = diagnostics.get(0).getRange().getEnd().getCharacter();

        // We expect antlr to see mismatchedTokenException at range (8, 17)
        // because there is no closing parenthese after last value
        int lineExpected = 0;
        int startOffsetExpected = 8;
        int endOffsetExpected = 17;
        assertEquals(lineExpected, lineStart);
        assertEquals(lineExpected, lineEnd);
        assertEquals(startOffsetExpected, offsetStart);
        assertEquals(endOffsetExpected, offsetEnd);

        assertEquals(diagnostics.size(), 1);

    }

    @Test
    public void completionOperatorsSeparators() throws IOException, RecognitionException {
        String str = "TID";
        Position cursor = new Position(0, 3);

        List<String> suggestions = AutoCompletion.autoCompletion(str, cursor);
        int sizeExpected = 9;
        assertEquals(sizeExpected, suggestions.size());
    }

    @Test
    public void completionSeparatorsAfterParentheses() throws IOException, RecognitionException {
        String str = "(TID == 42)";
        Position cursor = new Position(0, 11);

        List<String> suggestions = AutoCompletion.autoCompletion(str, cursor);
        int sizeExpected = 2;
        assertEquals(sizeExpected, suggestions.size());
    }

    @Test
    public void completionLongInputOperatorsSeparators() throws IOException, RecognitionException {
        String str = "(TID == 42 && PID != 12) || Poly";
        Position cursor = new Position(0, 32);

        List<String> suggestions = AutoCompletion.autoCompletion(str, cursor);
        int sizeExpected = 9;
        assertEquals(sizeExpected, suggestions.size());
    }

    @Test
    public void completionLongInputOperatorsSeparatorsCursorMiddle() throws IOException, RecognitionException {
        String str = "(TID == 42 && PID != 12) || Poly";
        Position cursor = new Position(0, 5);

        List<String> suggestions = AutoCompletion.autoCompletion(str, cursor);
        int sizeExpected = 9;
        assertEquals(sizeExpected, suggestions.size());
    }

    @Test
    public void completionLongInputOperatorsSeparatorsCursorMiddleNoSuggestions() throws IOException, RecognitionException {
        String str = "(TID == 42 && PID != 12) || Poly";
        Position cursor = new Position(0, 7);

        List<String> suggestions = AutoCompletion.autoCompletion(str, cursor);
        int sizeExpected = 0;
        assertEquals(sizeExpected, suggestions.size());
    }

    @Test
    public void completionSeparatorsOnly() throws IOException, RecognitionException {
        String str = "TID == 42";
        Position cursor = new Position(0, 9);

        List<String> suggestions = AutoCompletion.autoCompletion(str, cursor);
        int sizeExpected = 2;
        assertEquals(sizeExpected, suggestions.size());
    }
}
