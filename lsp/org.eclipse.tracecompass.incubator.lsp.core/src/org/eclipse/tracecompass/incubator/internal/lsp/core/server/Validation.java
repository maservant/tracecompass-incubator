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

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserLexer;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserParser;

public class Validation {
    @SuppressWarnings("restriction")
    public static void validate(String str) throws IOException, RecognitionException {
        //Initialize the lexerParser, parse str and return list of CommonToken
        ByteArrayInputStream input = new ByteArrayInputStream("123 contai 1".getBytes());
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

        lexerExceptions.forEach(e -> {
           String mString = lexer.getErrorMessage(e, lexer.getTokenNames());
           System.out.println(mString);

        });
        parserExceptions.forEach(e -> {
           String mString = parser.getErrorMessage(e, parser.getTokenNames());
           System.out.println(mString);
        });
    }
}
