/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.environment;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LSPFilterClient;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.LSPServer;
import org.eclipse.tracecompass.incubator.lsp.core.stubs.FakeClientStub;
import org.eclipse.tracecompass.incubator.lsp.core.stubs.LSPClientStub;
import org.eclipse.tracecompass.incubator.lsp.core.stubs.LSPServerStub;

/**
 * Create a test environment for testing LSP implementations
 * This class returns a stub for LSPServer, LSPClient and a fakeClient.
 * Theses stubs gather data about transactions and calls the real
 * implementations.
 * The unit tests can simply read from stub's mockup to check if values are valid.
 *
 * @author Maxime Thibault
 *
 */
public class TestEnvironment {

    public LSPServer fLSPServer = null;
    public LSPFilterClient fLSPClient = null;

    public LSPClientStub fLSPClientStub;
    public LSPServerStub fLSPServerStub;
    public FakeClientStub fLSPFakeClientStub;

    /**
     * Create a test environment.
     * Use this object to invoke function from fLSPServer or fLSPClient,
     * then probe data from stub's mockup.
     */
    public TestEnvironment() {
        initialize();
    }

    /**
     * Reset the test environment
     * Simply re-initialize it
     */
    public void reset() {
        initialize();
    }

    /**
     * Initialize the test environment
     */
    private void initialize() {
        // Streams to simulate socket communication between LSPClient and LSPServer
        Stream LSPserverStream = new Stream();
        Stream LSPserverStubStream = new Stream();
        Stream LSPclientStream = new Stream();
        Stream LSPclientStubStream = new Stream();

        fLSPFakeClientStub = new FakeClientStub();

        fLSPServer = new LSPServer(LSPserverStubStream.read, LSPserverStream.write);
        fLSPServerStub = new LSPServerStub(fLSPServer.fLSPServer);
        // LSPServer reads from LSPClient and write into LSPClientStub
        Launcher<LanguageClient> LSPServerLauncher = LSPLauncher.createServerLauncher(fLSPServerStub, LSPclientStream.read, LSPserverStubStream.write);
        // Then listen for incoming request
        LSPServerLauncher.startListening();

        fLSPClient = new LSPFilterClient(LSPclientStubStream.read, LSPclientStream.write, fLSPFakeClientStub);
        fLSPClientStub = new LSPClientStub(fLSPClient.getLanguageClient());
        // LSPClient reads from LSPServer and write into LSPServertStub
        Launcher<LanguageServer> LSPClientLauncher = LSPLauncher.createClientLauncher(fLSPClientStub, LSPserverStream.read, LSPclientStream.write);
        // Get reference to the server
        fLSPClientStub.setServer(LSPClientLauncher.getRemoteProxy());
        // Listen for incoming request
        LSPClientLauncher.startListening();
    }
}
