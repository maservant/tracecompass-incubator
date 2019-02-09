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
 * LanguageClient stub: Wrap around an actual LanguageClientImplementation
 * It helps to store data about the real implementation.
 * Mockup actually store information about the requests/reponses values.
 * @author maxtibs
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
        // TODO Auto-generated method stub
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        mockup.received = diagnostics.getDiagnostics().get(0).getMessage();
        client.publishDiagnostics(diagnostics);
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

    @Override
    public void register(@NonNull IObserver obs) {
        this.observer = obs;
    }

    public void tellDidChange(String str) {
       mockup.received = str;
       client.tellDidChange(str);
    }

}