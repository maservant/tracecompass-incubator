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
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

/**
 * Use this class to save data from LSPClientStub Add attributes/functions if
 * necessary
 *
 * @author Maxime Thibault
 *
 */
public class LSPClientMockup {
    public String fInputReceived = null;
    public List<Diagnostic> fDiagnosticsReceived = null;
    public CompletableFuture<List<ColorInformation>> fColorsReceived = null;
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> fCompletionsReceived = null;
}
