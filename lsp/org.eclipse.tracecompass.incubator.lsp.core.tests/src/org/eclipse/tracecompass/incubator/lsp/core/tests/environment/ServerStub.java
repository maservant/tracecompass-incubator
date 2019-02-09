package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import org.eclipse.tracecompass.incubator.internal.lsp.core.server.LanguageServerImpl;

public class ServerStub extends LanguageServerImpl {

    ServerMockup mockup = new ServerMockup();

    public ServerStub() {
        super();
        this.filterBoxService = new FilterBoxServiceStub(this.clients);
    }

    @Override
    public FilterBoxServiceStub getTextDocumentService() {
        // TODO Auto-generated method stub
        return (FilterBoxServiceStub)this.filterBoxService;
    }
}