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
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DocumentColorParams;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
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
    public List<Observer> fObservers = new ArrayList<>();
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
        String uri = diagnostics.getUri();
        for (Observer observer : fObservers) {
            observer.diagnostic(uri, diagnostics.getDiagnostics());
        }
        System.out.println(uri);
        TextDocumentIdentifier filterBoxId = new TextDocumentIdentifier(uri);
        // Make request for completion
        Runnable completionTask = () -> {
            Position position = new Position();
            position.setLine(0);
            position.setCharacter(fCursor);
            CompletionParams completionParams = new CompletionParams(filterBoxId, position);

            try {
                Either<List<CompletionItem>, CompletionList> completion = fServerProxy.getTextDocumentService().completion(completionParams).get();
                for (Observer observer : fObservers) {
                    observer.completion(uri, completion);
                }
            } catch (Exception e) {
                Activator.getInstance().logError(e.getMessage());
            }
        };
        /// Make request for syntax highlighting
        Runnable syntaxHighlightingTask = () -> {
            DocumentColorParams colorParams = new DocumentColorParams(filterBoxId);
            try {
                List<ColorInformation> colors = fServerProxy.getTextDocumentService().documentColor(colorParams).get();
                for (Observer observer : fObservers) {
                    observer.syntaxHighlighting(uri, colors);
                }
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
        fObservers.add(obs);
    }

    public void tellDidOpen(String uri) {
        TextDocumentItem filterBoxId = new TextDocumentItem();
        filterBoxId.setUri(uri);
        DidOpenTextDocumentParams didOpenParams = new DidOpenTextDocumentParams(filterBoxId);

        fServerProxy.getTextDocumentService().didOpen(didOpenParams);
    }

    public void tellDidChange(String uri, String input) {
        if (input.isEmpty()) {
            return;
        }
        Integer min = 0;
        Integer max = input.length() - 1;
        fCursor = max;
        Position p1 = new Position(0, min);
        Position p2 = new Position(0, max);
        Range r = new Range(p1, p2);
        TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent(r, max + 1, input);
        List<TextDocumentContentChangeEvent> changelist = new ArrayList();
        changelist.add(change);
        VersionedTextDocumentIdentifier filterBoxId = new VersionedTextDocumentIdentifier();
        filterBoxId.setUri(uri);
        DidChangeTextDocumentParams params = new DidChangeTextDocumentParams(filterBoxId, changelist);

        fServerProxy.getTextDocumentService().didChange(params);
    }
}
