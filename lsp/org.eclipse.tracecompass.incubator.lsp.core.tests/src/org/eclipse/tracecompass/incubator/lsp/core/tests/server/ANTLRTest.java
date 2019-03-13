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
    public void validationTest() throws IOException, RecognitionException {

        System.out.println("TESING: TID =");
        List<Diagnostic> diagnostics = Validation.validate("TID =");
        assertEquals(diagnostics.size(), 1);

        System.out.println("TESING: TID = && ");
        diagnostics = Validation.validate("TID = && ");
        assertEquals(diagnostics.size(), 1);

        System.out.println("TESING: = 2");
        diagnostics = Validation.validate("= 2");
        assertEquals(diagnostics.size(), 1);

        System.out.println("TESING: TI = 100 & TID != 12");
        diagnostics = Validation.validate("TI = 100 & TID != 12");
        assertEquals(diagnostics.size(), 2);


        System.out.println("\"asdasd = a\"");
        diagnostics = Validation.validate("TI = 100 & TID != 12");
        assertEquals(diagnostics.size(), 2);
    }
}
