package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

public class Client implements LanguageClient {

    LanguageServer serverProxy;

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
        System.out.println(messageParams.getMessage());
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

    public void setServer(LanguageServer proxy) {
        this.serverProxy = proxy;
    }

    public void requestWorld() {
        this.serverProxy.getTextDocumentService();
    }

    static void launchClient(String host, Integer port) {
        try (Socket socket = new Socket(host ,port)) {
            Client client = new Client();
            Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(client, socket.getInputStream(), socket.getOutputStream());
            client.setServer(launcher.getRemoteProxy());
            launcher.startListening();
            client.requestWorld();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}