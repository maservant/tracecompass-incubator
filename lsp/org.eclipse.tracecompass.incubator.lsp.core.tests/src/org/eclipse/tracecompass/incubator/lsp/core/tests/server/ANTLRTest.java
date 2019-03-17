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
        assertEquals(new Color(1, 1, 1, 1), ci1.getColor());
        assertEquals(0, ci1.getRange().getStart().getCharacter());
        assertEquals(2, ci1.getRange().getEnd().getCharacter());

        ColorInformation ci2 = ci.get(1);
        assertEquals(new Color(1, 0, 0, 1), ci2.getColor());
        assertEquals(4, ci2.getRange().getStart().getCharacter());
        assertEquals(5, ci2.getRange().getEnd().getCharacter());

        ColorInformation ci3 = ci.get(2);
        assertEquals(new Color(0, 0, 1, 1), ci3.getColor());
        assertEquals(7, ci3.getRange().getStart().getCharacter());
        assertEquals(8, ci3.getRange().getEnd().getCharacter());

    }

    @Test
    public void validationMismatchedTokenException() throws IOException, RecognitionException {

        List<Diagnostic> diagnostics = Validation.validate("TID = 123");
        int line_start = diagnostics.get(0).getRange().getStart().getLine();
        int offset_start = diagnostics.get(0).getRange().getStart().getCharacter();
        int line_end = diagnostics.get(0).getRange().getEnd().getLine();
        int offset_end = diagnostics.get(0).getRange().getEnd().getCharacter();

        //We expect antlr to see mismatchedTokenException at position 5 because '=' must equals '==' instead
        int line_expected = 1;
        int offset_expected = 5;
        assertEquals(line_expected, line_start);
        assertEquals(line_expected, line_end);
        assertEquals(offset_expected, offset_start);
        assertEquals(offset_expected, offset_end);

        //Detected by both lexer and parser. It is irrelevant, but client should be able to display only one of them
        assertEquals(diagnostics.size(), 1);

    }

    @Test
    public void validationNoViableAltException() throws IOException, RecognitionException {

        String str = "TID == ";

        List<Diagnostic> diagnostics = Validation.validate(str);
        int line_start = diagnostics.get(0).getRange().getStart().getLine();
        int offset_start = diagnostics.get(0).getRange().getStart().getCharacter();
        int line_end = diagnostics.get(0).getRange().getEnd().getLine();
        int offset_end = diagnostics.get(0).getRange().getEnd().getCharacter();

        //We expect antlr to see NoViableAltException at position 6 because
        int line_expected = 1;
        int offset_expected = str.length();
        assertEquals(line_expected, line_start);
        assertEquals(line_expected, line_end);
        assertEquals(offset_expected, offset_start);
        assertEquals(offset_expected, offset_end);

        assertEquals(diagnostics.size(), 1);

    }
}