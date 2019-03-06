/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.antlr.runtime.RecognitionException;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentColorParams;
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
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.SyntaxHighlighting;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Validation;

/**
 * FilterBoxService offers the interface to the client in order to notify the
 * server of the changes and to ask for completions, validations and syntax tips
 * for the filter string.
 *
 * @author David-Alexandre Beaupre and Remi Croteau
 *
 */
public class FilterBoxService implements TextDocumentService {

    private String fInput;
    private final List<LanguageClient> fClients;

    protected FilterBoxService(List<LanguageClient> clients) {
        fInput = new String();
        fClients = clients;
    }

    /**
     * Offers completion suggestions based on the user input
     *
     * @param position
     *            is the current cursor position
     */
    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
        Position startPos = new Position(position.getPosition().getLine(), position.getPosition().getCharacter());
        Position endPos = new Position(startPos.getLine(), startPos.getCharacter() + 1);
        List<CompletionItem> items = new ArrayList();
        LspFilterParser currentPositionFilterParser = new LspFilterParser();
        currentPositionFilterParser.parseFilter(fInput, startPos);
        int currentState = currentPositionFilterParser.getCurrentState();
        List<String> suggestions = currentPositionFilterParser.getSuggestions(currentState);
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println("Suggestion " + i + ": " + suggestions.get(i));
            CompletionItem item = new CompletionItem();
            item.setTextEdit(new TextEdit(new Range(startPos, endPos), suggestions.get(i)));
            items.add(item);
        }
        return CompletableFuture.completedFuture(Either.forLeft(items));
    }

    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
        // May eventually be used
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
        // Not used.
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
        // May be implemented eventually
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {
        try {
            List<ColorInformation> colorInformation = SyntaxHighlighting.getColorInformationList(fInput);
            return CompletableFuture.completedFuture(colorInformation);
        } catch (IOException error) {
            error.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
        // May be implemented eventually
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        // May be implemented eventually
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
        // May be implemented eventually
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
        // Should be implemented eventually
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
        // Should be implemented eventually
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
        // Should be implemented eventually
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        // Does not apply to filter box. There is no file to be opened.
        throw new UnsupportedOperationException();
    }

    /**
     * Check the string validity and sends a diagnostic to the client
     *
     * @param params
     *            contains the changes to the string input
     */
    @Override
    public void didChange(DidChangeTextDocumentParams params) throws NullPointerException {
        TextDocumentContentChangeEvent contentChange = params.getContentChanges().get(0);
        if (contentChange == null) {
            throw new NullPointerException("Event change param cannot be null");
        }
        fInput = params.getContentChanges().get(0).getText();
        try {
            List<Diagnostic> diagnostics = Validation.validate(fInput);
            PublishDiagnosticsParams pd = new PublishDiagnosticsParams();
            pd.setDiagnostics(diagnostics);
            LanguageClient client = fClients.get(0);
            if (client == null) {
                throw new NullPointerException("Client cannot be null");
            }
            fClients.get(0).publishDiagnostics(pd);
        } catch (RecognitionException error) {
            error.printStackTrace();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    public List<LanguageClient> getClients() {
        return fClients;
    }

    public String getInput() {
        return fInput;
    }

    public void setInput(String input) {
        fInput = input;
    }

}
