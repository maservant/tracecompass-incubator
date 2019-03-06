/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import java.util.List;

import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

/**
 * FilterBox stub: Wrap around an actual FilterBox implementation. It helps to
 * store data about the real implementation. Mockup actually store information
 * about the requests/reponses values.
 *
 * @author Maxime Thibault
 *
 *         TODO: NEEDS TO BE BOUND TO A REAL FILTERBOX EVENTUALLY
 */
public class FilterBoxStub implements IObserver {

    public FilterBoxMockup mockup = new FilterBoxMockup();

    @Override
    public void diagnostic(List<Diagnostic> diagnostics) {
        // TODO Auto-generated method stub

    }

    @Override
    public void completion(Either<List<CompletionItem>, CompletionList> completion) {
        // TODO Auto-generated method stub

    }

    @Override
    public void syntaxHighlighting(List<ColorInformation> colors) {
        // TODO Auto-generated method stub

    }
}
