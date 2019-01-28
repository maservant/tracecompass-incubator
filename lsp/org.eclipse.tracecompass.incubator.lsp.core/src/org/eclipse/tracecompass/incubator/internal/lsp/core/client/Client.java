/*******************************************************************************
 * Copyright (c) 2017 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class Client {

    private class SocketClient {

        private Socket socket;
        public String host;
        public int port;

        public SocketClient(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void start() {
            try {
                socket = new Socket(host, port);
                System.out.println("Connected to server");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public InputStream getInputStream() {
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return inputStream;
        }

        public OutputStream getOutputStream() {
            OutputStream outputStream = null;
            try {
                outputStream = socket.getOutputStream();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return outputStream;
        }

    }

    public Client(String host, int port) {

        SocketClient socketClient = new SocketClient(host, port);
        socketClient.start();

        InputStream in = socketClient.getInputStream();
        OutputStream out = socketClient.getOutputStream();

        LanguageClientImpl client = new LanguageClientImpl();
        Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(client, in, out);
        client.setServer(launcher.getRemoteProxy());
        launcher.startListening();

        try {
            String h1 = "World";
            String h2 = "Hell";
            String h3 = "Hello";
            String h4 = "Hello-o";

            System.out.println("Client testing " + h1);
            String hello1 = launcher.getRemoteEndpoint().request("complete", h1).get().toString();
            System.out.println(hello1);

            System.out.println("Client testing " + h2);
            String hello2 = launcher.getRemoteEndpoint().request("complete", h2).get().toString();
            System.out.println(hello2);

            System.out.println("Client testing " + h3);
            String hello3 = launcher.getRemoteEndpoint().request("complete", h3).get().toString();
            System.out.println(hello3);

            System.out.println("Client testing " + h4);
            String hello4 = launcher.getRemoteEndpoint().request("complete", h4).get().toString();
            System.out.println(hello4);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
