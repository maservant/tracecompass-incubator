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
import java.util.Stack;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BaseTree;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.eclipse.lsp4j.Position;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserLexer;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserParser;
import org.eclipse.tracecompass.tmf.filter.parser.FilterParserParser.parse_return;

/**
 * Class that offer autocompletion parameters based on antlr
 *
 * @author Maxime Thibault
 *
 */
public class AutoCompletion {

    static private CommonTree getSimpleExpressionAtCursor(CommonTree tree, Position cursor) {
        Stack<CommonTree> treeStack = new Stack<>();
        treeStack.push(tree);
        CommonTree currentTree = null;
        while (!treeStack.isEmpty()) {
            currentTree = treeStack.pop();
            int childCount = currentTree.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                treeStack.push((CommonTree)currentTree.getChild(i));
            }
            if (childCount == 0) {
                CommonToken token = (CommonToken)currentTree.getToken();
                if (cursor.getCharacter() - 1 <= token.getStopIndex()) {
                    return (CommonTree)currentTree.getParent();
                }
            }
        }
        if (currentTree != null) {
            return (CommonTree)currentTree.getParent();
        }
        return null;
    }

    @SuppressWarnings("restriction") // Suppress restriction on ANTLR
                                     // FilterParser*
    static public void autoCompletion(String str, Position cursor) throws IOException, RecognitionException {

        // Initialize the lexerParser, parse str and return list of CommonToken
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        ANTLRInputStream antlrStream = new ANTLRInputStream(input);
        FilterParserLexer lexer = new FilterParserLexer(antlrStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        FilterParserParser filterParserParser = new FilterParserParser(tokenStream);

        parse_return parse = filterParserParser.parse();
        CommonTree tree = parse.getTree();

        CommonTree simpleTree = getSimpleExpressionAtCursor(tree, cursor);

        if (simpleTree.getChildCount() == 1) {
            CommonTree child = (CommonTree)simpleTree.getChild(0);
            CommonToken token = (CommonToken)child.getToken();
            if (cursor.getCharacter() > token.getStopIndex()) {
                // TODO: hello
            }
        }
    }

}
