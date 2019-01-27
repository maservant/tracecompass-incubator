package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LanguageClientImpl;

public class LanguageServerImpl implements LanguageServer, LanguageClientAware {

    private WorkspaceService workspaceService;
    private TextDocumentService textDocumentService;
    private final List<LanguageClientImpl> clients = new CopyOnWriteArrayList<>();


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
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        // TODO Auto-generated method stub
        return workspaceService;
    }

    @Override
    public void connect(LanguageClient client) {
        this.clients.add((LanguageClientImpl)client);
    }

    @JsonRequest
    CompletableFuture<String> fetchMessage() {
        return CompletableFuture.completedFuture("Hello");
    }

    @JsonNotification
    public void sayHello(String s) {
        System.out.println(s);
        for(LanguageClientImpl client : clients) {
            client.didSayHello("hello");
        }
    }


}