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

import java.io.InputStream;
import java.io.OutputStream;

public class Server {

    private class mSocket {

        public int port;
        public ServerSocket serverSocket;

        public mSocket(int port) {
            this.port = port;
            try {
                this.serverSocket = new ServerSocket(this.port);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public void start() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = serverSocket.accept();
                        String ip = socket.getInetAddress().toString();
                        System.out.println("Un client s'est connecté à partir de " + ip);

                        //Instantiate LSP client
                        InputStream in = socket.getInputStream();
                        OutputStream out = socket.getOutputStream();

                        LanguageServerImpl server = new LanguageServerImpl();
                        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);
                        server.connect(launcher.getRemoteProxy());
                        launcher.startListening();


                        //Close thread
                        serverSocket.close();

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
            t.start();
        }
    }

    public Server() {
        mSocket socket = new mSocket(Configuration.PORT);
        socket.start();
    }

    public Server(InputStream in, OutputStream out) {
        LanguageServerImpl server = new LanguageServerImpl();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);
        server.connect(launcher.getRemoteProxy());
        launcher.startListening();
    }
}
