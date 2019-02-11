package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

public class NoServerException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoServerException(String errorMessage) {
        super(errorMessage);
    }
}
