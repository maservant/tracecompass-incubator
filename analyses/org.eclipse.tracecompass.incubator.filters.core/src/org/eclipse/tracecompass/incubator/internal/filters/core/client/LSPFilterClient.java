/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.filters.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.filters.core.Activator;
import org.eclipse.tracecompass.incubator.internal.filters.core.shared.Configuration;
import org.eclipse.tracecompass.incubator.internal.filters.core.shared.LspObserver;

import com.google.common.annotations.VisibleForTesting;

/**
 * This class intent is to be use by the LspFilterTextbox.java
 *
 * This class simplify the LanguageFilterClient used by the LspFilterTextbox
 * reducing the number of call required to achieve an update in a document.
 *
 * @author Maxime Thibault
 *
 */
public class LSPFilterClient {

    private LanguageFilterClient fLanguageClient = null;
    private Socket fSocket = null;

    /**
     * Connect the client to server that match the hostname and the port number
     * Also register an observer which will use this class to update the server
     * and to be updated by the server.
     *
     * @param hostname
     *            server IP address
     * @param port
     *            server port number
     * @param observer
     *            to update and get update from
     * @param documentUri
     *            OPTIONAL document identifier on which the LSP should work. See
     *            LSP specifications
     */
    public LSPFilterClient(String hostname, Integer port, @NonNull LspObserver observer, String documentUri) {

        // Start a thread that periodically try to connect to the server if not
        // already connected
        new Thread(new Runnable() {
            @Override
            public void run() {
                // While we're not connected to the server
                while (fSocket == null) {
                    try {
                        // Try to connect to the server
                        fSocket = new Socket(hostname, port);

                    } catch (Exception e) {
                        // Thread sleep for 5 seconds if has not connected to
                        // server
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            Activator.getInstance().logError(e1.getMessage());
                        }

                    } finally {
                        // Setup the client if fSocket is not null (because
                        // we've successfully connected to server)
                        try {
                            if (fSocket != null) {
                                initialize(fSocket.getInputStream(), fSocket.getOutputStream(), observer, documentUri);
                            }
                        } catch (IOException e) {
                            Activator.getInstance().logError(e.getMessage());
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Constructor that uses a default hostname and port number. See
     * Configuration.java for more information.
     *
     * @param observer
     *            to update and get update from
     * @param documentUri
     *            OPIONAL document identifier on which the LSP should work. See
     *            LSP specifications
     */
    public LSPFilterClient(@NonNull LspObserver observer, String documentUri) throws UnknownHostException, IOException {
        this(Configuration.HOSTNAME, Configuration.PORT, observer, documentUri);
    }

    /**
     * **USE THIS FOR TESTING ONLY**
     *
     * @param in
     *            where the data is coming form
     * @param out
     *            where the data is going to
     * @param observer
     *            to update and get update from
     */
    @VisibleForTesting
    public LSPFilterClient(InputStream in, OutputStream out, @NonNull LspObserver observer) {
        initialize(in, out, observer, null);
    }

    /**
     * Initialize the LSPFilterClient.
     *
     * See LSP specification.
     *
     * @param in
     * @param out
     * @param observer
     * @param documentUri
     *            OPTIONAL
     */
    private void initialize(InputStream in, OutputStream out, @NonNull LspObserver observer, String documentUri) {
        fLanguageClient = new LanguageFilterClient();
        Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(fLanguageClient, in, out);
        fLanguageClient.setServer(launcher.getRemoteProxy());
        fLanguageClient.register(observer);
        launcher.startListening();

        // If a documentUri is specified, tell the server about it
        if (documentUri != null) {
            fLanguageClient.tellDidOpen(documentUri);
        }
    }

    /**
     * See LSP specifications
     *
     * @return Language client
     */
    public LanguageFilterClient getLanguageClient() {
        return fLanguageClient;
    }

    /**
     * Observers use this to tell the server that the file has change
     *
     * @param Uri
     *            the file uri on which there's a change
     * @param input
     *            new file content
     * @param cursorPos
     *            position of cursor when the document change
     *
     */
    public void notify(String Uri, String input, int cursorPos) {

        if (fLanguageClient == null) {
            return;
        }
        fLanguageClient.tellDidChange(Uri, input, cursorPos);
    }

    /**
     * Close client-side socket connection. Also tell the server to shutdown.
     *
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void dispose() {

        try {
            // Tell server to shutdown
            if (fLanguageClient != null) {
                fLanguageClient.shutdown();
            }
        } catch (Exception e) {
            Activator.getInstance().logError(e.getMessage());
        } finally {

            try {
                // Close socket if not null
                if (fSocket != null) {
                    fSocket.close();
                }
            } catch (Exception e) {
                Activator.getInstance().logError(e.getMessage());
            }
        }

    }
}
