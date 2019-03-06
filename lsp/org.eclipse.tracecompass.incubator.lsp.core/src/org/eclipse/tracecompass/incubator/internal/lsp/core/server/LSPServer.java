/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * LSPServer wrapper
 *
 * @author Maxime Thibault
 *
 */
public class LSPServer {

    public LanguageServerImpl lspserver;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    /**
     * Create serverSocket then wait for a client socket to connect
     * When a client socket is connected, create the lspLauncher to listen to incoming requests
     *
     * @throws IOException
     */
    public LSPServer() throws IOException {
        serverSocket = new ServerSocket(Configuration.PORT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = serverSocket.accept();

                    // Instantiate LSP client
                    InputStream in = clientSocket.getInputStream();
                    OutputStream out = clientSocket.getOutputStream();

                    lspserver = new LanguageServerImpl();
                    Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(lspserver, in, out);
                    lspserver.connect(launcher.getRemoteProxy());
                    launcher.startListening();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Create server from InputStream and OutputStream
     *
     * @param in:
     *            InputStream (data in)
     * @param out
     *            OutputStream (data out)
     */
    public LSPServer(InputStream in, OutputStream out) {
        lspserver = new LanguageServerImpl();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(lspserver, in, out);
        lspserver.connect(launcher.getRemoteProxy());
        launcher.startListening();
    }

    /**
     * Close client-end and server socket
     *
     * @throws IOException
     */
    public void dispose() throws IOException {
        serverSocket.close();
    }
}
