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
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.*;

/**
 * LSP Client simplification that offers an API to its observer.
 * The underlying class is the LanguageClient implementation
 * @author maxtibs
 *
 */
public class Client {

    public LanguageClientImpl lspclient;

    /*
     * Create client:
     *  -Connect to server with socket from default hostname and port
     *  -Register an observer who can use client API and get notified when server responds
     * @param observer that uses this API and get notified
     */
    public Client(@NonNull IObserver observer) {
        SocketClient sc = new SocketClient(Configuration.HOSTNAME, Configuration.PORT);
        initialize(sc.getInputStream(), sc.getOutputStream(), observer);
    }

    /*
     * Create client:
     *  -Connect to server with socket from hostname and port
     *  -Register an observer who can use client API and get notified when server responds
     * @param hostname: address of server to connect to
     * @param port: port of server
     * @param observer that uses this API and get notified
     */
    public Client(String hostname, Integer port, @NonNull IObserver observer) {
        SocketClient sc = new SocketClient(hostname, port);
        initialize(sc.getInputStream(), sc.getOutputStream(), observer);
    }

    /*
     * Create client:
     *  -Use InputStream and OutputStream instead of socket
     *  -Register an observer who can use client API and get notified when server responds
     * @param in: input stream of a stream communication
     * @param out: output stream of a stream communication
     * @param observer that uses this API and get notified
     */
    public Client(InputStream in, OutputStream out, @NonNull IObserver observer) {
        initialize(in, out, observer);
    }

    /**
     * Initialize the LanguageServer from LanguageClient impementation
     * @param in
     * @param out
     * @param observer
     */
    private void initialize(InputStream in, OutputStream out, @NonNull IObserver observer) {
        lspclient = new LanguageClientImpl();
        Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(lspclient, in, out);
        lspclient.setServer(launcher.getRemoteProxy());
        lspclient.register(observer);
        launcher.startListening();
    }

    /**
     * PUBLIC API: Send string to server using the LSP client
     * @param str: string to send
     */
    public void notify(String str) {
        lspclient.tellDidChange(str);
    }
}
