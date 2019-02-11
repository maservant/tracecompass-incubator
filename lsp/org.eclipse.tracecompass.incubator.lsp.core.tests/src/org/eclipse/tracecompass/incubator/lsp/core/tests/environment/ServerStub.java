package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.FilterWorkspaceService;

/**
 * LanguageServer stub: Wrap around an actual LanguageServerImplementation
 * It helps delegates calls from mockup to real implementation
 * Mockup actually store information about the requests/reponses values.
 * @author maxtibs
 *
 */
public class ServerStub implements LanguageServer  {

    ServerMockup mockup = new ServerMockup();
    LanguageServer server;
    protected FilterBoxServiceStub filterBoxService;
    private WorkspaceService filterWorkspaceService;

    public ServerStub(LanguageServer s) {
        server = s;
        this.filterBoxService = new FilterBoxServiceStub(server.getTextDocumentService());
        this.filterWorkspaceService = new FilterWorkspaceService();
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
        return this.filterBoxService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.filterWorkspaceService;
    }

}