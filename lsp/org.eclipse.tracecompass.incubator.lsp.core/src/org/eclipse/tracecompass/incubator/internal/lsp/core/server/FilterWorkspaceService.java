/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;

/**
 * Empty implementation of the LSP specification WorkspaceService. Not useful
 * for the filterBox.
 *
 * @author David-Alexandre Beaupre
 * @author Remi Croteau
 *
 */
public class FilterWorkspaceService implements WorkspaceService {

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
        // Does not apply to filter box
        throw new UnsupportedOperationException();
    }

    @Override
    public void didChangeConfiguration(@Nullable DidChangeConfigurationParams params) {
        // Not used
        throw new UnsupportedOperationException();
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        // Not used
        throw new UnsupportedOperationException();
    }

}
