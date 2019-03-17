/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

/**
 * LanguageServer implementation for the tracecompass FilterBox
 *
 *
 */
public class LanguageFilterServer implements LanguageServer, LanguageClientAware {

    private TextDocumentService filterBoxService;
    private WorkspaceService filterWorkspaceService;
    private LanguageClient fClient;

    public LanguageFilterServer() {
        this.filterBoxService = new FilterBoxService(this);
        this.filterWorkspaceService = new FilterWorkspaceService();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        // TODO : send initialize result with actual capabilities
        return CompletableFuture.completedFuture(new InitializeResult());
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        // Nothing to do
        return null;
    }

    @Override
    public void exit() {
        // Nothing to do
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        // TODO Auto-generated method stub
        return filterBoxService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        // TODO Auto-generated method stub
        return filterWorkspaceService;
    }

    @Override
    public void connect(LanguageClient client) {
        fClient = client;
    }

    /**
     * Used by the filterBoxService when it needs to make a call on the client.
     */
    public LanguageClient getClient() {
        return fClient;
    }
}