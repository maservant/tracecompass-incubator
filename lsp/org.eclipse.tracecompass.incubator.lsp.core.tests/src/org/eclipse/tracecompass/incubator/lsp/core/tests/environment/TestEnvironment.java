package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.Client;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Server;

/**
 * This class initialize stubs for Client, Server and FilterBox. Theses stubs has a mockup attributes in wich the stubs
 * needs to write value when they receive signals from others stubs from original classes (Client, Server or FilterBox)
 * @author maxtibs
 *
 */
public class TestEnvironment {

    public static enum ENVIRONMENT {
        SERVER,
        CLIENT,
        FILTERBOX
    }

    public Server server = null;
    public Client client = null;
    //public FilterBox fb = null;

    public ClientStub clientStub;
    public ServerStub serverStub;
    public FilterBoxStub filterBoxStub;

    /**
     * Initialize a test environnment based on env selected
     * @param env: SERVER, CLIENT or FILTERBOX
     */
    public TestEnvironment(ENVIRONMENT env) {
        switch(env) {
            case SERVER:
                createServerEnv();
                break;
            case CLIENT:
                createClientEnv();
                break;
           /* TODO: case FILTERBOX:
                break;*/
            default:
                break;
        }
    }

    /**
     * Create test environment for Client
     *  -Create a Client
     *  -Create a ClientStub
     *  -Create a ServerStub
     *  -Create a FilterBoxStub
     */
    private void createClientEnv() {

        Stream blackholeStream = new Stream();

        Stream clientStream = new Stream();
        Stream serverStubStream = new Stream();

        //Create filterBoxStub
        filterBoxStub = new FilterBoxStub();

        //Create clientStub
        Stream clientStubStream = new Stream();
        clientStub = new ClientStub();
        Launcher<LanguageServer> clientLauncher = LSPLauncher.createClientLauncher(clientStub, serverStubStream.read, clientStubStream.write);
        clientStub.setServer(clientLauncher.getRemoteProxy());
        clientStub.register(filterBoxStub);
        clientLauncher.startListening();

        //Create serverStub
        serverStub = new ServerStub();
        Launcher<LanguageClient> serverLauncher = LSPLauncher.createServerLauncher(serverStub, clientStream.read, serverStubStream.write);
        serverStub.connect(serverLauncher.getRemoteProxy());
        serverLauncher.startListening();


        //Create Client
        client = new Client(blackholeStream.read, clientStream.write, filterBoxStub);

    }


    /**
     * Create test environment for Server
     *  -Create a Server
     *  -Create a ClientStub
     *  -Create a ServerStub
     *  -Create a FilterBoxStub
     */
    private void createServerEnv() {

        Stream blackholeStream = new Stream();

        Stream serverStream = new Stream();
        Stream clientStubStream = new Stream();


        //Create filterBoxStub
        filterBoxStub = new FilterBoxStub();

        //Create clientStub
        clientStub = new ClientStub();
        Launcher<LanguageServer> clientLauncher = LSPLauncher.createClientLauncher(clientStub, serverStream.read, clientStubStream.write);
        clientStub.setServer(clientLauncher.getRemoteProxy());
        clientLauncher.startListening();

        //Create serverStub
        serverStub = new ServerStub();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(serverStub, clientStubStream.read, serverStream.write);
        serverStub.connect(launcher.getRemoteProxy());
        launcher.startListening();

        //Create Server
        server = new Server(blackholeStream.read, serverStream.write);

    }

}
