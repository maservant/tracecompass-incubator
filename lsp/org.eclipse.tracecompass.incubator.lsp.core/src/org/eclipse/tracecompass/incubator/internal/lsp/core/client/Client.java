/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.*;

public class Client {

    private LanguageClientImpl client;

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

    public Client(@NonNull IObserver observer) {

        SocketClient socketClient = new SocketClient(Configuration.HOSTNAME, Configuration.PORT);
        socketClient.start();

        InputStream in = socketClient.getInputStream();
        OutputStream out = socketClient.getOutputStream();

        client = new LanguageClientImpl();
        Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(client, in, out);
        client.setServer(launcher.getRemoteProxy());
        client.register(observer);
        launcher.startListening();
    }

    public void notify(String str) {
        client.tellDidChange(str);
    }
}
