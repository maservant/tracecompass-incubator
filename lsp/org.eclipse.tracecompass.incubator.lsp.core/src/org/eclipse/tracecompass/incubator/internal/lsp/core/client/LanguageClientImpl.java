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
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObservable;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class LanguageClientImpl implements LanguageClient, IObservable {

    LanguageServer serverProxy;
    IObserver observer;

    @Override
    public void telemetryEvent(Object object) {
        // TODO Auto-generated method stub

    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        // TODO Auto-generated method stub
        String v = diagnostics.getDiagnostics().get(0).getMessage();
        this.observer.notify(v);

        if(v.equals("VALID")) {
            //Ask for completion
            //this.askCompletion();
            System.out.println("String is VALID");

        } else if (v.equals("INVALID")) {
            //Error
            System.out.println("String is INVALID");
        }
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        // TODO Auto-generated method stub

    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void logMessage(MessageParams message) {
        // TODO Auto-generated method stub

    }

    public void setServer(LanguageServer server) {
        this.serverProxy = server;
    }

    @Override
    public void register(@NonNull IObserver obs) {
        this.observer = obs;
    }

    public void tellDidChange(String str) {
        if (str.equals("")) {
            return;
        }
        Integer min = 0;
        Integer max = str.length() - 1;

        Position p1 = new Position(min, Character.getNumericValue(str.charAt(min)) );
        Position p2 = new Position(max, Character.getNumericValue(str.charAt(min)) );
        Range r = new Range(p1,p2);
        TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent(r, max, str);
        List<TextDocumentContentChangeEvent> changelist = new ArrayList();
        changelist.add(change);
        DidChangeTextDocumentParams params = new DidChangeTextDocumentParams();
        params.setContentChanges(changelist);

        serverProxy.getTextDocumentService().didChange(params);
    }

}
