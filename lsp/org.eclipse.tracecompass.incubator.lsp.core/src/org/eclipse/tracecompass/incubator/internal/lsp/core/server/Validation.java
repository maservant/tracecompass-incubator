/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedNotSetException;
import org.antlr.runtime.MismatchedRangeException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserLexer;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserParser;

/**
 * Validates the user input with antlr and detects where the errors are in the string
 *
 * @author Maxime Thibault
 * @author David-Alexandre Beaupre
 * @author Remi Croteau
 *
 */
public class Validation {
    /**
     * Detects all the errors in the input string (if any) and return those as diagnostics
     *
     * @param str is the content of the filter box
     * @return diagnostics is a list containing all the errors found by the parser and lexer
     * @throws IOException
     * @throws RecognitionException
     */
    @SuppressWarnings("restriction")
    public static List<Diagnostic> validate(String str) throws IOException, RecognitionException {
        //Initialize the lexerParser, parse str and return list of CommonToken
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        ANTLRInputStream antlrStream = new ANTLRInputStream(input);

        FilterParserLexer lexer = new FilterParserLexer(antlrStream);
        ArrayList<RecognitionException> lexerExceptions = new ArrayList<>();
        lexer.setErrorListener(e -> {
            lexerExceptions.add((RecognitionException) e);
        });
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        FilterParserParser parser = new FilterParserParser(tokenStream);
        ArrayList<RecognitionException> parserExceptions = new ArrayList<>();
        parser.setErrorListener(e -> {
            parserExceptions.add((RecognitionException) e);
        });

        parser.parse();

        List<Diagnostic> diagnostics = new ArrayList<>();

        lexerExceptions.forEach(e -> {
           String message = lexer.getErrorMessage(e, lexer.getTokenNames());
           Range range = getRangeFromException(e, str);
           Diagnostic diagnostic = new Diagnostic(range, message);
           diagnostics.add(diagnostic);
        });

        parserExceptions.forEach(e -> {
           String message = parser.getErrorMessage(e, parser.getTokenNames());
           Range range = getRangeFromException(e, str);
           Diagnostic diagnostic = new Diagnostic(range, message);
           diagnostics.add(diagnostic);
        });

        return preprocessDiagnostics(diagnostics);

    }

    /**
     * Handler to find the appropriate range of error in the exception
     *
     * @see org.antlr.runtime.Lexer#getErrorMessage(RecognitionException e, String[] tokenNames)
     *                                                                                                         /
     * @param e The RecognitionException thrown by antlr
     * @param msg The input string to validate
     * @return Range of error to be used in diagnostic
     */
    private static Range getRangeFromException(RecognitionException e, String msg) {

        int line_start = 1, line_end = 1;
        int offset_start = 0, offset_end = 0;

        if ( e instanceof MismatchedTokenException ) {
            //@see: https://www.antlr3.org/api/Java/org/antlr/runtime/MismatchedTokenException.html
            line_start = line_end = e.line;
            offset_start = offset_end = e.index;
        }
        else if ( e instanceof NoViableAltException ) {
            //@see: https://www.antlr3.org/api/Java/org/antlr/runtime/NoViableAltException.html
            line_start = line_end = 1;
            offset_start = offset_end = msg.length();
        }
        else if ( e instanceof EarlyExitException ) {
            //@see: https://www.antlr3.org/api/Java/org/antlr/runtime/EarlyExitException.html
            //Just keep the initialized value.
            //We that that this exception triggers with the string "". Still, we don't know if we should take care of this..
        }
        else if ( e instanceof MismatchedNotSetException ) {
            //@see: https://www.antlr3.org/api/Java/org/antlr/runtime/MismatchedNotSetException.html
            //No known case of this exception happening..
        }
        else if ( e instanceof MismatchedSetException ) {
            //@see: https://www.antlr3.org/api/Java/org/antlr/runtime/MismatchedSetException.html
            //No known case of this exception happening..
        }
        else if ( e instanceof MismatchedRangeException ) {
            //@see: https://www.antlr3.org/api/Java/org/antlr/runtime/MismatchedRangeException.html
            //No known case of this exception happening..
        }
        else {
            //Any other exception..
        }

        Position start = new Position(line_start, offset_start);
        Position end = new Position(line_end, offset_end);
        return new Range(start, end);
    }

    /**
     * Remove duplicated error from list of diagnostics
     *
     * @param diagnostics
     */
    private static List<Diagnostic> preprocessDiagnostics(List<Diagnostic> diagnostics) {

        for(int i = 0; i < diagnostics.size(); i++) {
            for(int j = i + 1; j < diagnostics.size(); j++) {
                if(diagnostics.get(i).equals(diagnostics.get(j))) {
                    diagnostics.remove(j);
                }
            }
        }

        return diagnostics;
    }


}
