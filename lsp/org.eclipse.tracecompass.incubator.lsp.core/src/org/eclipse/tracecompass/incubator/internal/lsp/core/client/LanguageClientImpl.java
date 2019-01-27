package org.eclipse.tracecompass.incubator.internal.lsp.core.client;


import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;
//import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.LanguageServerImpl;
//import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;

public class LanguageClientImpl implements LanguageClient {

    LanguageServerImpl server;

    @Override
    public void telemetryEvent(Object object) {
        // TODO Auto-generated method stub

    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        // TODO Auto-generated method stub

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

    @JsonNotification
    public void didSayHello(String str) {
        System.out.println(str);
    }

}
