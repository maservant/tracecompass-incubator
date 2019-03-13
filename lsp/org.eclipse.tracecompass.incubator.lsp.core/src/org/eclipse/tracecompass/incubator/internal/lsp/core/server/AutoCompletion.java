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

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserLexer;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserParser;

/**
 * Class that offer autocompletion parameters based on antlr
 *
 * @author Maxime Thibault
 *
 */
public class AutoCompletion {

    @SuppressWarnings("restriction") // Suppress restriction on ANTLR FilterParser*
    static public void autoCompletion(String str) throws IOException, RecognitionException {

        // Initialize the lexerParser, parse str and return list of CommonToken
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        ANTLRInputStream antlrStream = new ANTLRInputStream(input);
        FilterParserLexer lexer = new FilterParserLexer(antlrStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        FilterParserParser filterParserParser = new FilterParserParser(tokenStream);
        filterParserParser.parse();
    }

}
