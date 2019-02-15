/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.lsp.ui.lspFilterTextbox;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.Client;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

/**
 * Widget to wrap all the logic of a filter lsp client
 *
 * @author Jeremy Dube
 */
public class LspFilterTextbox implements IObserver {

    private @Nullable Client lspClient;
    private final IFilterBoxView fFilterBoxView;

    /**
     * Constructor
     * @param view the filter box view
     * @throws UnknownHostException Exception thrown from LSPClientAPI
     * @throws IOException Exception thrown from LSPClientAPI
     */
    public LspFilterTextbox(IFilterBoxView view) throws UnknownHostException, IOException {
        lspClient = new Client(this);
        fFilterBoxView = view;
    }

    /**
     * Method to notify the LSP Client of a change
     * @param message string entered in the filter box
     */
    public void notifyLspClient(String message) {
        if (message.isEmpty()) {
            fFilterBoxView.defaultViewHandler();
        } else {
            if (lspClient != null) {
                lspClient.notify(message);
            }
        }
    }

    /**
     * Method called by the lsp client to notify the view of changes
     */
    @Override()
    public void notify(@Nullable Object v) {
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                String s = Objects.requireNonNull(v).toString();
                if (s.equals("INVALID")) {
                    fFilterBoxView.errorViewHandler();
                } else {
                    fFilterBoxView.defaultViewHandler();
                }
            }
        });
    }
}
