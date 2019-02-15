/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

/**
 * FilterBox stub: Wrap around an actual FilterBox implementation. It helps to
 * store data about the real implementation. Mockup actually store information
 * about the requests/reponses values.
 *
 * @author Maxime Thibault
 *
 *         TODO: NEEDS TO BE BOUND TO A REAL FILTERBOX EVENTUALLY
 */
public class FilterBoxStub implements IObserver {

    public FilterBoxMockup mockup = new FilterBoxMockup();

    @Override
    public void notify(Object value) {
        mockup.received = (String) value;
    }

}
