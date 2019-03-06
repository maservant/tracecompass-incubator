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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LSPClientAPI;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

/**
 * Widget to wrap a FilterText widget with additional logic of a filter lsp
 * client
 *
 * @author Jeremy Dube
 */
public class LspFilterTextbox implements IObserver {

    private @Nullable LSPClientAPI lspClient;
    private final Color fDefaultFilterTextColor;
    private final StyledText fFilterStyledText;
    private final TextViewer fTextViewer;

    /**
     * Constructor
     *
     * @param parent
     *            the parent view
     */
    public LspFilterTextbox(Composite parent) {
        fTextViewer = new TextViewer(parent, SWT.SINGLE | SWT.BORDER);
        fFilterStyledText = fTextViewer.getTextWidget();
        fFilterStyledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        setKeyListener();
        fDefaultFilterTextColor = fFilterStyledText.getBackground();
        try {
            lspClient = new LSPClientAPI(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor
     *
     * @param parent
     *            the parent view
     * @param overridenParameters
     *            map of listeners to override. The string consists of the name
     *            of the listener to override, without "set"
     */
    public LspFilterTextbox(Composite parent, Map<String, Boolean> overridenParameters) {
        fTextViewer = new TextViewer(parent, SWT.SINGLE | SWT.BORDER);
        fFilterStyledText = fTextViewer.getTextWidget();
        fFilterStyledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        if (!overridenParameters.containsKey("KeyListener") //$NON-NLS-1$
            || !Objects.requireNonNull(overridenParameters.get("KeyListener"))) { //$NON-NLS-1$
            setKeyListener();
        }
        fDefaultFilterTextColor = fFilterStyledText.getBackground();
        try {
            lspClient = new LSPClientAPI(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to set the focus on the FilterText
     *
     * @return true if the FilterText gets the focus
     */
    public boolean setFocus() {
        return fFilterStyledText.setFocus();
    }

    /**
     * Method to return the text in the Filter Box
     *
     * @return the text
     */
    public String getText() {
        return fFilterStyledText.getText();
    }

    /**
     * Method to set the text in the Filter Box
     *
     * @param text
     *            the value to set to
     */
    public void setText(String text) {
        fFilterStyledText.setText(text);
    }

    /**
     * Method called by the lsp client to notify the view of changes
     */
    @Override
    public void diagnostic(final List<Diagnostic> diagnostics) {
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                if (!diagnostics.isEmpty()) {
                    showErrorView();
                } else {
                    resetView();
                }
            }
        });
    }

    @Override
    public void completion(final Either<List<CompletionItem>, CompletionList> completion) {
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                //TODO: Needs to be implemented
            }
        });
    }

    @Override
    public void syntaxHighlighting() {
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                //TODO: Needs to be implemented
            }
        });
    }

    /**
     * Method to add a key Listener to the FilterText Widget
     */
    private void setKeyListener() {
        fFilterStyledText.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(@Nullable KeyEvent e) {
                String text = Objects.requireNonNull(fFilterStyledText.getText());
                notifyLspClient(text);
            }

            @Override
            public void keyPressed(@Nullable KeyEvent e) {
                // Do nothing
            }
        });
    }

    /**
     * Method to add a key Listener to the FilterText Widget
     *
     * @param consumer
     *            the function to be called
     */
    public void setKeyListener(Consumer<KeyEvent> consumer) {
        fFilterStyledText.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(@Nullable KeyEvent e) {
                String text = Objects.requireNonNull(fFilterStyledText.getText());
                notifyLspClient(text);
            }

            @Override
            public void keyPressed(@Nullable KeyEvent e) {
                consumer.accept(e);
            }
        });
    }

    /**
     * Method to notify the LSP Client of a change
     *
     * @param message
     *            string entered in the filter box
     */
    private void notifyLspClient(String message) {
        if (message.isEmpty()) {
            resetView();
        } else {
            if (lspClient != null) {
                lspClient.notify(message);
            }
        }
    }

    /**
     * Method to reset the filter box view (i.e. put back initial color, remove
     * error message, remove suggestions)
     */
    private void resetView() {
        fFilterStyledText.setBackground(fDefaultFilterTextColor);
    }

    /**
     * Method to put the filter box in error state
     */
    private void showErrorView() {
        Device device = Display.getCurrent();
        fFilterStyledText.setBackground(new Color(device, 255, 150, 150));
    }
}
