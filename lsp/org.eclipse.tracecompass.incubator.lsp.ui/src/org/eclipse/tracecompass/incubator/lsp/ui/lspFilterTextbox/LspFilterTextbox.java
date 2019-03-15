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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LSPFilterClient;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.Observer;

/**
 * Widget to wrap a FilterText widget with additional logic of a filter lsp
 * client
 *
 * @author Jeremy Dube
 */
public class LspFilterTextbox implements Observer {

    private @Nullable LSPFilterClient lspClient;
    private List<ValidListener> fListeners = new ArrayList<>();
    private final Color fDefaultFilterTextColor;
    private final StyledText fFilterStyledText;
    private final TextViewer fTextViewer;
    private final CLabel fSearchButton;
    private final CLabel fCancelButton;
    private Boolean fIsValidString = false;
    private static Color ERROR_BACKGROUND_COLOR;

    /**
     * Constructor
     *
     * @param parent
     *            the parent view
     */
    public LspFilterTextbox(Composite parent) {
        final Composite baseComposite = new Composite(parent, SWT.BORDER);
        baseComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        final GridLayout baseCompositeGridLayout = new GridLayout(3, false);
        baseCompositeGridLayout.marginHeight = 0;
        baseCompositeGridLayout.marginWidth = 0;
        baseComposite.setLayout(baseCompositeGridLayout);

        // Search icon
        fSearchButton = new CLabel(baseComposite, SWT.CENTER);
        fSearchButton.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        fSearchButton.setText("search"); // Will be changed for an image

        // Text box
        fTextViewer = new TextViewer(baseComposite, SWT.SINGLE | SWT.BORDER);
        fFilterStyledText = fTextViewer.getTextWidget();
        fFilterStyledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        // Cancel icon
        fCancelButton = new CLabel(baseComposite, SWT.CENTER);
        fCancelButton.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        fCancelButton.setText("clear"); // Will be changed for an image

        setIconsListener();
        setKeyListener();
        fDefaultFilterTextColor = fFilterStyledText.getBackground();
        try {
            lspClient = new LSPFilterClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Device device = Display.getCurrent();
        ERROR_BACKGROUND_COLOR = new Color(device, 255, 150, 150);
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
     * Method to add an external listener when the string is valid
     *
     * @param validListener
     *            the listener to add
     */
    public void addValidListener(ValidListener validListener) {
        fListeners.add(validListener);
    }

    /**
     * Method to notify listeners of valid string
     */
    private void notifyValid() {
        if (fIsValidString) {
            for (ValidListener validListener : fListeners) {
                validListener.valid();
            }
        }
    }

    /**
     * Method to notify listeners of invalid string
     */
    private void notifyInvalid() {
        for (ValidListener validListener : fListeners) {
            validListener.invalid();
        }
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
                if (e == null) {
                    return;
                }
                if (e.character == SWT.CR) {
                    notifyValid();
                }
            }
        });
    }

    /**
     * Method to add a mouse Listener to the icons
     */
    private void setIconsListener() {
        fSearchButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
                notifyValid();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                // Nothing to do here
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // Nothing to do here
            }
        });
        fCancelButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
                fFilterStyledText.setText("");
                resetView();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                // Nothing to do here
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // Nothing to do here
            }
        });
    }

    /**
     * Method called by the lsp client to notify the view of changes
     */
    @Override
    public void diagnostic(final List<Diagnostic> diagnostics) {
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                if (diagnostics.size() > 0) {
                    diagnostics.forEach((diagnostic) -> {
                        int start = diagnostic.getRange().getStart().getCharacter();
                        int end = diagnostic.getRange().getEnd().getCharacter();
                        if (end - start > 0) {
                            showErrorView(diagnostic);
                        } else {
                            showBackgroundRed();
                        }
                    });
                    fIsValidString = false;
                    notifyInvalid();
                } else {
                    resetView();
                    fIsValidString = true;
                }
            }
        });
    }

    @Override
    public void completion(final Either<List<CompletionItem>, CompletionList> completion) {
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                // TODO: Needs to be implemented
            }
        });
    }

    @Override
    public void syntaxHighlighting(List<ColorInformation> colors) {
        Display.getDefault().syncExec(new Runnable() {
            @Override()
            public void run() {
                colors.forEach((color) -> {
                    addColor(color);
                });
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
     * Method to add a color according to a range
     *
     * @param colorInformation
     */
    private void addColor(ColorInformation colorInformation) {
        int start = colorInformation.getRange().getStart().getCharacter();
        int end = colorInformation.getRange().getEnd().getCharacter();
        Device device = Display.getCurrent();
        Color color = new Color(device, (int) (colorInformation.getColor().getRed() * 255),
                (int) (colorInformation.getColor().getGreen() * 255),
                (int) (colorInformation.getColor().getBlue() * 255));

        fTextViewer.setTextColor(color, start, end - start + 1, false);
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
    private void showErrorView(Diagnostic diagnostic) {
        int start = diagnostic.getRange().getStart().getCharacter();
        int end = diagnostic.getRange().getEnd().getCharacter();
        Device device = Display.getCurrent();
        Color color = new Color(device, 255, 0, 0);

        StyleRange style = new StyleRange();
        style.start = start;
        style.length = end - start + 1;
        style.underline = true;
        style.underlineColor = color;
        fFilterStyledText.setStyleRange(style);
    }

    private void showBackgroundRed() {
        fFilterStyledText.setBackground(ERROR_BACKGROUND_COLOR);
    }
}
