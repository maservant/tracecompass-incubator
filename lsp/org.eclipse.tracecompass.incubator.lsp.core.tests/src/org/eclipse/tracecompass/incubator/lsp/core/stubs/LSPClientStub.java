/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.stubs;

import java.util.concurrent.CompletableFuture;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LanguageFilterClient;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.LspObservable;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.LspObserver;

/**
 * LanguageClient stub: Wrap around LanguageClientImpl
 * Helps to store data about the real implementation.
 * Use the LSPClientMockup to store data from calls
 *
 * @author Maxime Thibault
 *
 */
public class LSPClientStub implements LanguageClient, LspObservable {

    public LanguageFilterClient fClient;
    public LSPClientMockup fMockup = new LSPClientMockup();
    public LanguageServer fServerProxy;
    public LspObserver fObserver;

    public LSPClientStub(LanguageFilterClient languageClient) {
        fClient = languageClient;
    }

    @Override
    public void telemetryEvent(Object object) {
        // Not implemented
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        fMockup.fDiagnosticsReceived = diagnostics.getDiagnostics();
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        // Not implemented
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {
        // Not implemented
    }

    public void setServer(LanguageServer server) {
        fServerProxy = server;
    }

    @Override
    public void register(@NonNull LspObserver obs) {
        fObserver = obs;
    }

    public void tellDidChange(String uri, String input) {
        fMockup.fInputReceived = input;
        fClient.tellDidChange(uri, input);
    }

}