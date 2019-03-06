/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.FilterWorkspaceService;

/**
 * LanguageServer stub: Wrap around an actual LanguageServerImplementation It
 * helps delegates calls from mockup to real implementation Mockup actually
 * store information about the requests/reponses values.
 *
 * @author Maxime Thibault
 *
 */
public class ServerStub implements LanguageServer {

    public ServerMockup mockup = new ServerMockup();
    public LanguageServer server;
    protected FilterBoxServiceStub filterBoxService;
    private WorkspaceService filterWorkspaceService;

    public ServerStub(LanguageServer s) {
        server = s;
        filterBoxService = new FilterBoxServiceStub(server.getTextDocumentService());
        filterWorkspaceService = new FilterWorkspaceService();
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
        return filterBoxService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return filterWorkspaceService;
    }

}