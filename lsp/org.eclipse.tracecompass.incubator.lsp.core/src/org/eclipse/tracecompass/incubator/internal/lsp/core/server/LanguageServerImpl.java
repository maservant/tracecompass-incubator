/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LanguageClientImpl;

public class LanguageServerImpl implements LanguageServer, LanguageClientAware {

    private TextDocumentService filterBoxService;
    private final List<LanguageClient> clients = new CopyOnWriteArrayList<>();

    LanguageServerImpl() {
        this.filterBoxService = new FilterBoxService();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        // TODO Auto-generated method stub
        return CompletableFuture.completedFuture(new InitializeResult());
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub

    }

    @Override
    public TextDocumentService getTextDocumentService() {
        // TODO Auto-generated method stub
        System.out.println("In the service of the queen");
        return this.filterBoxService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        // TODO Auto-generated method stub
        //return workspaceService;
        return null;
    }

    @Override
    public void connect(LanguageClient client) {
        this.clients.add(client);
    }

    @JsonRequest
    public CompletableFuture<String> complete(String str) {
        if(str.equals("Hello")) {
            return CompletableFuture.completedFuture("Hello world!");
        }
        return CompletableFuture.completedFuture(str);
    }

    /*@JsonNotification
    public void sayHello(String s) {
        System.out.println(s);
        for(LanguageClientImpl client : clients) {
            client.didSayHello("hello");
        }
    }*/
}