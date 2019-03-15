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
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserLexer;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserParser;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserParser.parse_return;

/**
 *
 * @author Maxime Thibault
 *
 */
public class Validation {
    /**
     *
     * @param str
     * @return List of Diagnostic
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

        parse_return parse = parser.parse();
        CommonTree tree = parse.getTree();

        List<Diagnostic> diagnostics = new ArrayList<>();

        lexerExceptions.forEach(e -> {
           String message = lexer.getErrorMessage(e, lexer.getTokenNames());

           int idx = e.index;

           Position start = new Position(e.line, idx);
           Position end = new Position(e.line, idx);
           Range range = new Range(start, end);
           Diagnostic diagnostic = new Diagnostic(range, message);

           diagnostics.add(diagnostic);

        });

        parserExceptions.forEach(e -> {
           String message = parser.getErrorMessage(e, parser.getTokenNames());

           int idx = e.token.getCharPositionInLine();
           // EOF error
           if (idx == -1) {
               idx = str.length();
           }
           Position start = new Position(e.line, idx);
           Position end = new Position(e.line, idx);
           Range range = new Range(start, end);
           Diagnostic diagnostic = new Diagnostic(range, message);

           diagnostics.add(diagnostic);
        });

        return diagnostics;

    }

}
