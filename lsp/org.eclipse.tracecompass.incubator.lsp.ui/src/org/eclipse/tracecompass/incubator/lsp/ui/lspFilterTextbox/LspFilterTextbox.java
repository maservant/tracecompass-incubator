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
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
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
//    private final StyledText fFilterStyledText;
    private final Text fFilterText;

    /**
     * Constructor
     *
     * @param parent
     *            the parent view
     */
    public LspFilterTextbox(Composite parent) {
        fFilterText = new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
        fFilterText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        setSelectionListener();
        setKeyListener();
        fDefaultFilterTextColor = fFilterText.getBackground();
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
        fFilterText = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
        fFilterText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        if (!overridenParameters.containsKey("SelectionListener") //$NON-NLS-1$
            || !Objects.requireNonNull(overridenParameters.get("SelectionListener"))) { //$NON-NLS-1$
            setSelectionListener();
        }
        if (!overridenParameters.containsKey("KeyListener") //$NON-NLS-1$
            || !Objects.requireNonNull(overridenParameters.get("KeyListener"))) { //$NON-NLS-1$
            setKeyListener();
        }
        fDefaultFilterTextColor = fFilterText.getBackground();
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
        return fFilterText.setFocus();
    }

    /**
     * Method to return the text in the Filter Box
     *
     * @return the text
     */
    public String getText() {
        return fFilterText.getText();
    }

    /**
     * Method to set the text in the Filter Box
     *
     * @param text
     *            the value to set to
     */
    public void setText(String text) {
        fFilterText.setText(text);
    }

    /**
     * Method called by the lsp client to notify the view of changes
     */
    @Override
    public void notify(@Nullable Object obj) {
        final List<Diagnostic> diagnostics = (List<Diagnostic>)Objects.requireNonNull(obj);
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                String s = diagnostics.get(0).getMessage();
                if (s.equals("INVALID")) {
                    showErrorView();
                } else {
                    resetView();
                }
            }
        });
    }

    /**
     * Method to add a selection Listener to the FilterText Widget
     */
    private void setSelectionListener() {
        fFilterText.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(@Nullable SelectionEvent e) {
                // Nothing to do here
            }

            @Override
            public void widgetDefaultSelected(@Nullable SelectionEvent e) {
                SelectionEvent event = Objects.requireNonNull(e);
                if (event.detail == SWT.ICON_CANCEL) {
                    resetView();
                }
            }
        });
    }

    /**
     * Method to add a selection Listener to the FilterText Widget
     *
     * @param consumer
     *            the function to be called
     */
    public void setSelectionListener(Consumer<SelectionEvent> consumer) {
        fFilterText.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(@Nullable SelectionEvent e) {
                // Nothing to do here
            }

            @Override
            public void widgetDefaultSelected(@Nullable SelectionEvent e) {
                SelectionEvent event = Objects.requireNonNull(e);
                if (event.detail == SWT.ICON_CANCEL) {
                    resetView();
                }
                consumer.accept(e);
            }
        });
    }

    /**
     * Method to add a key Listener to the FilterText Widget
     */
    private void setKeyListener() {
        fFilterText.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(@Nullable KeyEvent e) {
                String text = Objects.requireNonNull(fFilterText.getText());
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
        fFilterText.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(@Nullable KeyEvent e) {
                String text = Objects.requireNonNull(fFilterText.getText());
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
        fFilterText.setBackground(fDefaultFilterTextColor);
    }

    /**
     * Method to put the filter box in error state
     */
    private void showErrorView() {
        Device device = Display.getCurrent();
        fFilterText.setBackground(new Color(device, 255, 150, 150));
    }
}
