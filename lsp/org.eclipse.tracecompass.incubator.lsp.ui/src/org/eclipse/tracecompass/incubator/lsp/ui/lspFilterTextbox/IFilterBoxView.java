/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.lsp.ui.lspFilterTextbox;

/**
 * Interface that needs to be implemented by a filter box to use the lsp client
 *
 * @author Jeremy Dube
 */
public interface IFilterBoxView {
    /**
     * Method to put the initial view back
     */
    public void defaultViewHandler();

    /**
     * Method to put the filter box in error mode
     */
    public void errorViewHandler();

    /**
     * Method to put the filter box in success mode
     */
//    public void successViewHandler();
}
