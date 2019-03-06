/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.lsp.core.shared;

import org.eclipse.jdt.annotation.NonNull;

/**
 *
 * @author Jeremy Dube
 *
 */
public interface IObservable {
    public void register(@NonNull IObserver observer);
}
