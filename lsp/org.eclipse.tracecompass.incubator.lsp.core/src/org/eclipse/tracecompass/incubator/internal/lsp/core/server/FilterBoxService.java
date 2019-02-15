/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.tracecompass.internal.provisional.tmf.core.model.filter.parser.FilterCu;
import org.eclipse.tracecompass.internal.provisional.tmf.core.model.filter.parser.FilterExpressionCu;
import org.eclipse.tracecompass.internal.provisional.tmf.core.model.filter.parser.FilterSimpleExpressionCu;

public class FilterBoxService implements TextDocumentService {

    private String input;
    private final List<LanguageClient> clients;

    protected FilterBoxService(List<LanguageClient> clients) {
        this.input = new String();
        this.clients = clients;
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
        Position startPos = new Position(position.getPosition().getLine(), position.getPosition().getCharacter());
        Position endPos = new Position(startPos.getLine(), startPos.getCharacter() + 1);
        List<CompletionItem> items = new ArrayList();
        FilterCu inputValidity = FilterCu.compile2(this.input);
        if (inputValidity == null) {
            return CompletableFuture.completedFuture(null);
        }
        FilterExpressionCu expression = inputValidity.getExpressions().get(0);
        if (expression.getElement().get(0) instanceof FilterSimpleExpressionCu) {
            FilterSimpleExpressionCu simpleExpression = (FilterSimpleExpressionCu) expression.getElement().get(0);
            if (simpleExpression.getOperator().equals("matches")) {
                CompletionItem item1 = new CompletionItem();
                item1.setTextEdit(new TextEdit(new Range(startPos, endPos), "=="));
                CompletionItem item2 = new CompletionItem();
                item2.setTextEdit(new TextEdit(new Range(startPos, endPos), "!="));
                items.add(item1);
                items.add(item2);
            }
        }
        return CompletableFuture.completedFuture(Either.forLeft(items));
    }

    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        this.input = params.getContentChanges().get(0).getText();
        PublishDiagnosticsParams pd = new PublishDiagnosticsParams();
        List<Diagnostic> diagnostics = pd.getDiagnostics();
        String diagMsg = new String();
        Range range = params.getContentChanges().get(0).getRange();
        FilterCu inputValidity = FilterCu.compile(this.input);
        if (inputValidity != null) {
            diagMsg = "VALID";
        } else {
            diagMsg = "INVALID";
        }
        diagnostics.add(new Diagnostic(range, diagMsg));
        pd.setDiagnostics(diagnostics);
        this.clients.get(0).publishDiagnostics(pd);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // TODO Auto-generated method stub

    }

    public List<LanguageClient> getClients() {
        return clients;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

}
