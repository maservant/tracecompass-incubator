/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import java.util.concurrent.CompletableFuture;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LanguageClientImpl;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObservable;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

/**
 * LanguageClient stub: Wrap around an actual LanguageClientImplementation It
 * helps to store data about the real implementation. Mockup actually store
 * information about the requests/reponses values.
 *
 * @author Maxime Thibault
 *
 */
public class ClientStub implements LanguageClient, IObservable {

    public LanguageClientImpl client;
    public ClientMockup mockup = new ClientMockup();
    public LanguageServer serverProxy;
    public IObserver observer;

    public ClientStub(LanguageClientImpl c) {
        client = c;
    }

    @Override
    public void telemetryEvent(Object object) {
        // Not implemented
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        mockup.received = diagnostics.getDiagnostics().get(0).getMessage();
        client.publishDiagnostics(diagnostics);
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
        serverProxy = server;
    }

    @Override
    public void register(@NonNull IObserver obs) {
        observer = obs;
    }

    public void tellDidChange(String str) {
        mockup.received = str;
        client.tellDidChange(str);
    }

}