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
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.lsp4j.Color;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserLexer;

/**
 *
 * @author Maxime Thibault
 *
 */
public class SyntaxHighlighting {

    /**
     *
     * @return colorInformation
     * @throws IOException
     */
    @SuppressWarnings("restriction")
    static public List<ColorInformation> getColorInformationList(String str) throws IOException {

        //Initialise the lexerParser, parse str and return list of CommonToken
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        ANTLRInputStream antlrStream = new ANTLRInputStream(input);
        FilterParserLexer lexer = new FilterParserLexer(antlrStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        List<CommonToken> commonTokenList = tokenStream.getTokens();

        //From commonTokens
        List<ColorInformation> colorInformations = new LinkedList<>();
        commonTokenList.forEach(commonToken->{
            Position start = new Position(commonToken.getLine(), commonToken.getStartIndex());
            Position end = new Position(commonToken.getLine(), commonToken.getStopIndex());
            Range range = new Range(start, end);
            Color color = SyntaxHighlighting.getColor(commonToken.getType());
            ColorInformation colorInformation = new ColorInformation(range, color);
            colorInformations.add(colorInformation);
        });
        return colorInformations;
    }

    /**
     * Returns rgb color that matches type
     *
     * @param index
     * @return Color
     */
    private static Color getColor(int type) {
        switch (type) {
        case FilterParserLexer.OP:
            return new Color(0, 1, 0, 1);
        case FilterParserLexer.TEXT:
            return new Color(0, 0, 0, 1);
        case FilterParserLexer.SEPARATOR:
            return new Color(0, 0, 1, 1);
        default:
            return new Color(0, 0, 0, 1);
        }
    }

}
