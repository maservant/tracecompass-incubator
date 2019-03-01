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
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
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
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObservable;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

/**
 * LSPClient custom implementation
 *
 * @author Maxime Thibault
 *
 */
public class LanguageClientImpl implements LanguageClient, IObservable {

    public LanguageServer serverProxy;
    public IObserver observer;
    private Integer cursor = 0;
    private ThreadPoolExecutor threadPoolExecutor;

    public LanguageClientImpl() {
        final int corePoolSize = 1;
        final int maxPoolSize = 3;
        final long keepAliveTime = 5000;
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                queue
                );
    }

    @Override
    public void telemetryEvent(Object object) {
        // Not implemented

    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        observer.diagnostic(diagnostics.getDiagnostics());

        //Make request for completion
        Runnable completionTask = () -> {
            CompletionParams completionParams = new CompletionParams();
            Position position = new Position();
            position.setLine(0);
            position.setCharacter(cursor);
            completionParams.setPosition(position);

            try {
                Either<List<CompletionItem>, CompletionList> completion = serverProxy.getTextDocumentService().completion(completionParams).get();
                observer.completion(completion);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        ///Make request for syntax highlighting
        Runnable syntaxHighlightingTask = () -> {
            observer.syntaxHighlighting();
            //TODO: Needs to be implemented
        };

       threadPoolExecutor.execute(completionTask);
       threadPoolExecutor.execute(syntaxHighlightingTask);

    }

    @Override
    public void showMessage(MessageParams messageParams) {
        // Not implemented

    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        // Not implemented
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {
        // Not implemented
    }

    public void setServer(LanguageServer server) {
        serverProxy = server;
    }

    @Override
    public void register(@NonNull IObserver obs) {
        observer = obs;
    }

    public void tellDidChange(String str) {
        if (str.isEmpty()) {
            return;
        }
        Integer min = 0;
        Integer max = str.length() - 1;
        cursor = max;
        Position p1 = new Position(0, min);
        Position p2 = new Position(0, max);
        Range r = new Range(p1, p2);
        TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent(r, max + 1, str);
        List<TextDocumentContentChangeEvent> changelist = new ArrayList();
        changelist.add(change);
        DidChangeTextDocumentParams params = new DidChangeTextDocumentParams();
        params.setContentChanges(changelist);

        serverProxy.getTextDocumentService().didChange(params);
    }

}
