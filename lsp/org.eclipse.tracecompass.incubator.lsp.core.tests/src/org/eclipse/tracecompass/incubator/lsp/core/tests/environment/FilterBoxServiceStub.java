package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;
/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.util.List;

import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.FilterBoxService;

public class FilterBoxServiceStub extends FilterBoxService {

    public FilterBoxServiceMockup mockup = new FilterBoxServiceMockup();

    FilterBoxServiceStub(List<LanguageClient> clients) {
        super(clients);
    }
}
