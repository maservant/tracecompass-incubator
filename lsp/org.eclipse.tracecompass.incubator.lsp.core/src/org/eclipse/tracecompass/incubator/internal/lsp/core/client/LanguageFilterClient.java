/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DocumentColorParams;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.Activator;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.Observable;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.Observer;

/**
 * LanguageClient to be used by 1 LSPFilterClient.
 * This class implements the LSP4J LanguageClient for the tracecompass FilterBox
 *
 * @author Maxime Thibault
 *
 */
public class LanguageFilterClient implements LanguageClient, Observable {

    public LanguageServer fServerProxy;
    public Observer fObserver;
    private Integer fCursor = 0;
    private ThreadPoolExecutor fThreadPoolExecutor;

    private final static int fCorePoolSize = 1;
    private final static int fMaxPoolSize = 3;
    private final static long fKeepAliveTime = 5000;

    public LanguageFilterClient() {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        fThreadPoolExecutor = new ThreadPoolExecutor(
                fCorePoolSize,
                fMaxPoolSize,
                fKeepAliveTime,
                TimeUnit.MILLISECONDS,
                queue);
    }

    @Override
    public void telemetryEvent(Object object) {
        // Not implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        fObserver.diagnostic(diagnostics.getDiagnostics());

        // Make request for completion
        Runnable completionTask = () -> {
            CompletionParams completionParams = new CompletionParams();
            Position position = new Position();
            position.setLine(0);
            position.setCharacter(fCursor);
            completionParams.setPosition(position);

            try {
                Either<List<CompletionItem>, CompletionList> completion = fServerProxy.getTextDocumentService().completion(completionParams).get();
                fObserver.completion(completion);
            } catch (Exception e) {
                Activator.getInstance().logError(e.getMessage());
            }
        };
        /// Make request for syntax highlighting
        Runnable syntaxHighlightingTask = () -> {
            DocumentColorParams colorParams = new DocumentColorParams();
            try {
                List<ColorInformation> colors = fServerProxy.getTextDocumentService().documentColor(colorParams).get();
                fObserver.syntaxHighlighting(colors);
            } catch (Exception e) {
                Activator.getInstance().logError(e.getMessage());
            }
        };

        fThreadPoolExecutor.execute(completionTask);
        fThreadPoolExecutor.execute(syntaxHighlightingTask);

    }

    @Override
    public void showMessage(MessageParams messageParams) {
        // Not implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        // Not implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public void logMessage(MessageParams message) {
        // Not implemented
        throw new UnsupportedOperationException();
    }

    public void setServer(LanguageServer server) {
        fServerProxy = server;
    }

    @Override
    public void register(@NonNull Observer obs) {
        fObserver = obs;
    }

    public void tellDidChange(String str) {
        if (str.isEmpty()) {
            return;
        }
        Integer min = 0;
        Integer max = str.length() - 1;
        fCursor = max;
        Position p1 = new Position(0, min);
        Position p2 = new Position(0, max);
        Range r = new Range(p1, p2);
        TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent(r, max + 1, str);
        List<TextDocumentContentChangeEvent> changelist = new ArrayList();
        changelist.add(change);
        DidChangeTextDocumentParams params = new DidChangeTextDocumentParams();
        params.setContentChanges(changelist);

        fServerProxy.getTextDocumentService().didChange(params);
    }

}
