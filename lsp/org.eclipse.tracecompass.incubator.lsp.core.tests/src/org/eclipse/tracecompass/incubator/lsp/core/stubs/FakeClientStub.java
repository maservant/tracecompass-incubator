/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.stubs;

import java.util.List;

import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.Observer;

/**
 * This fake client stub is used to handle function call from the LSPClient bound in the TestEnvironment
 * Use this class to store the data into the FakeClientMockup
 *
 * @author Maxime Thibault
 *
 *
 */
public class FakeClientStub implements Observer {

    public FakeClientMockup fMockup = new FakeClientMockup();

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