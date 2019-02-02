/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.client;


import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

public class LanguageClientImpl implements LanguageClient {

    LanguageServer serverProxy;

    @Override
    public void telemetryEvent(Object object) {
        // TODO Auto-generated method stub

    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        System.out.println(diagnostics.getDiagnostics().get(0).getMessage());
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        // TODO Auto-generated method stub

    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {
        // TODO Auto-generated method stub

    }

    public void setServer(LanguageServer server) {
        this.serverProxy = server;
    }

}
