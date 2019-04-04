/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.lsp.core.tests.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.eclipse.lsp4j.Color;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.SyntaxHighlighting;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Validation;
import org.junit.Test;

/**
 *
 * @author Maxime Thibault
 * @author David-Alexandre Beaupre
 *
 * This class tests server-side class using antlr for syntax color, validation and autocompletion
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

        List<Diagnostic> diagnostics = Validation.validate("TID = 123");
        int lineStart = diagnostics.get(0).getRange().getStart().getLine();
        int offsetStart = diagnostics.get(0).getRange().getStart().getCharacter();
        int lineEnd = diagnostics.get(0).getRange().getEnd().getLine();
        int offsetEnd = diagnostics.get(0).getRange().getEnd().getCharacter();

        //We expect antlr to see mismatchedTokenException at range (4, 5) because '=' must equals '==' instead
        int lineExpected = 0;
        int startOffsetExpected = 4;
        int endOffsetExpected = 5;
        assertEquals(lineExpected, lineStart);
        assertEquals(lineExpected, lineEnd);
        assertEquals(startOffsetExpected, offsetStart);
        assertEquals(endOffsetExpected, offsetEnd);

        //Detected by both lexer and parser. It is irrelevant, but client should be able to display only one of them
        assertEquals(diagnostics.size(), 1);

    }

    @Test
    public void validationNoViableAltException() throws IOException, RecognitionException {

        String str = "TID == ";

        List<Diagnostic> diagnostics = Validation.validate(str);
        int lineStart = diagnostics.get(0).getRange().getStart().getLine();
        int offsetStart = diagnostics.get(0).getRange().getStart().getCharacter();
        int lineEnd = diagnostics.get(0).getRange().getEnd().getLine();
        int offsetEnd = diagnostics.get(0).getRange().getEnd().getCharacter();

        //We expect antlr to see NoViableAltException at position 6 because
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
        List<Diagnostic> diagnostics = Validation.validate(str);
        assertEquals(diagnostics.size(), 0);
    }

    @Test
    public void validationComplexStringNoErrors() throws IOException, RecognitionException {
        String str = "TID < 12 && (PID == 42 && (Ericsson > 1 || Poly matches 2))";
        List<Diagnostic> diagnostics = Validation.validate(str);
        assertEquals(diagnostics.size(), 0);
    }
}
