/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.EmptyStackException;
import java.util.Stack;
import org.eclipse.lsp4j.Position;


/**
 * Proposes completions to the input string in the globalFilter box, and locates syntax errors if present
 * @author David-Alexandre Beaupre and Remi Croteau
 *
 */
public class LspFilterParser {

    private static final char LEFT_PARENTHESIS = '(';
    private static final char RIGHT_PARENTHESIS = ')';

    public static void runAllChecks(String input) {
        Position position = checkParentheses(input);
        if (position != null) {
            System.out.println("Parentheses error at " + position.getCharacter());
        } else {
            System.out.println("Parentheses OK");
        }
    }

    private static Position checkParentheses(String input) {
        Stack<Position> parenthesesPositions = new Stack();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == LEFT_PARENTHESIS) {
                parenthesesPositions.push(new Position(0, i));
            }
            if (input.charAt(i) == RIGHT_PARENTHESIS) {
                try {
                    parenthesesPositions.pop();
                } catch (EmptyStackException e) {
                    return new Position(0, i);
                }
            }
        }
        if (!parenthesesPositions.empty()) {
            return parenthesesPositions.pop();
        }
        return null;
    }
}
