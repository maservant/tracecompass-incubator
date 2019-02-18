/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.shared;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.lsp4j.DiagnosticSeverity;

/**
 *
 * @author David-Alexandre Beaupre and Remi Croteau
 *
 */
public class DiagnosticCodes {
    private static Map<String, DiagnosticSeverity> codeToSeverity = new HashMap();
    private static Map<String, String> codeToMessage = new HashMap();
    public static final String RIGHT_PARENTHESIS_UNBALANCED = "E000";
    public static final String RIGHT_PARENTHESIS_UNEXPECTED = "E001";
    public static final String ILLEGAL_CHARACTER = "E002";
    public static final String LEFT_PARENTHESIS_UNEXPECTED = "E003";
    public static final String CHARACTER_UNEXPECTED = "E004";
    public static final String OPERATOR_SEPARATOR_EXPECTED = "E005";
    public static final String INVALID_OPERATOR = "E006";
    public static final String INVALID_SEPARATOR = "E007";
    public static final String SEPARATOR_EXPECTED = "E008";
    public static final String INVALID_ENDSTATE = "E009";
    static {
        codeToSeverity.put(RIGHT_PARENTHESIS_UNBALANCED, DiagnosticSeverity.Error);
        codeToSeverity.put(RIGHT_PARENTHESIS_UNEXPECTED, DiagnosticSeverity.Error);
        codeToSeverity.put(ILLEGAL_CHARACTER, DiagnosticSeverity.Error);
        codeToSeverity.put(LEFT_PARENTHESIS_UNEXPECTED, DiagnosticSeverity.Error);
        codeToSeverity.put(CHARACTER_UNEXPECTED, DiagnosticSeverity.Error);
        codeToSeverity.put(OPERATOR_SEPARATOR_EXPECTED, DiagnosticSeverity.Error);
        codeToSeverity.put(INVALID_OPERATOR, DiagnosticSeverity.Error);
        codeToSeverity.put(INVALID_SEPARATOR, DiagnosticSeverity.Error);
        codeToSeverity.put(SEPARATOR_EXPECTED, DiagnosticSeverity.Error);
        codeToSeverity.put(INVALID_ENDSTATE, DiagnosticSeverity.Warning);

        codeToMessage.put(RIGHT_PARENTHESIS_UNBALANCED, "Closing parenthesis has no opening equivalent.");
        codeToMessage.put(RIGHT_PARENTHESIS_UNEXPECTED, "Unexpected closing parenthesis, field or value was expected.");
        codeToMessage.put(ILLEGAL_CHARACTER, "Character is illegal.");
        codeToMessage.put(LEFT_PARENTHESIS_UNEXPECTED, "Unexpected opening parenthesis.");
        codeToMessage.put(CHARACTER_UNEXPECTED, "Unexpected character.");
        codeToMessage.put(OPERATOR_SEPARATOR_EXPECTED, "Operator or separator was expected.");
        codeToMessage.put(INVALID_OPERATOR, "Operator is invalid.");
        codeToMessage.put(INVALID_SEPARATOR, "Separator is invalid.");
        codeToMessage.put(SEPARATOR_EXPECTED, "Separator was expected.");
        codeToMessage.put(INVALID_ENDSTATE, "Incomplete filter string.");
    }

    public static DiagnosticSeverity getSeverity(String errorCode) {
        return codeToSeverity.get(errorCode);
    }

    public static String getMessage(String errorCode) {
        return codeToMessage.get(errorCode);
    }
}
