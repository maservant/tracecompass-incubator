package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.Client;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Server;

/**
 * Create a test environment for testing LSP implementations
 * This class returns a stub for every communication elements. Theses stubs gather data about transaction and delegates transactions
 * to the real communication elements. So the unit test can simply read from mockup to check if values are OK.
 * @author maxtibs
 *
 */
public class TestEnvironment {

    public Server server = null;
    public Client client = null;
    //public FilterBox fb = null;

    public ClientStub clientStub;
    public ServerStub serverStub;
    public FilterBoxStub filterBoxStub;


    public TestEnvironment() {
      //Server
        Stream serverStream = new Stream();
        Stream serverStubStream = new Stream();
        Stream clientStream = new Stream();
        Stream clientStubStream = new Stream();

        //Filterbox
        // TODO: filterBox = new FilterBox();
        filterBoxStub = new FilterBoxStub(); //TODO: Bind filterbox to filterBoxStub

        //Server configuration
        server = new Server(serverStubStream.read, serverStream.write);
        //ServerStub
        serverStub = new ServerStub(server.lspserver);
        Launcher<LanguageClient> l2 = LSPLauncher.createServerLauncher(serverStub, clientStream.read, serverStubStream.write);
        l2.startListening();

        //Client
        client = new Client(clientStubStream.read, clientStream.write, filterBoxStub);
        //ClientStub
        clientStub = new ClientStub(client.lspclient);
        Launcher<LanguageServer> l3 = LSPLauncher.createClientLauncher(clientStub, serverStream.read, clientStream.write);
        clientStub.setServer(l3.getRemoteProxy());
        l3.startListening();
    }
}
