/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.stubs;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.FilterWorkspaceService;

/**
 * LanguageServer stub: Wrap around an actual LanguageServerImpl
 * Helps to store data about the real implementation.
 * Use the LSPServerMockup to store data from calls
 *
 * @author Maxime Thibault
 *
 */
public class LSPServerStub implements LanguageServer {

    public LSPServerMockup fMockup = new LSPServerMockup();
    public LanguageServer fServer;
    protected FilterBoxServiceStub fFilterBoxService;
    private WorkspaceService fFilterWorkspaceService;

    public LSPServerStub(LanguageServer s) {
        fServer = s;
        fFilterBoxService = new FilterBoxServiceStub(fServer.getTextDocumentService());
        fFilterWorkspaceService = new FilterWorkspaceService();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        return CompletableFuture.completedFuture(new InitializeResult());
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return null;
    }

    @Override
    public void exit() {

    }

    @Override
    public FilterBoxServiceStub getTextDocumentService() {
        return fFilterBoxService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return fFilterWorkspaceService;
    }

}