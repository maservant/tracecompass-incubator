/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.lsp.core.tests.client;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.eclipse.tracecompass.incubator.lsp.core.environment.TestEnvironment;
import org.junit.Test;


public class LspClientTest {

    /**
     * Simple hello world tests.
     * LSPClient send 'hello' to LSPServer
     *
     * @throws InterruptedException
     */
    @Test
    public void hello() throws InterruptedException {
        String input = "Hello";
        String uri = "Mamma mia";
        TestEnvironment te = new TestEnvironment();
        te.fLSPClient.getLanguageClient().tellDidOpen(uri);
        te.fLSPClient.notify(uri, input);

        // TODO: Change synchronization mechanism
        //Wait for transactions to be done
        TimeUnit.SECONDS.sleep(1);

        //Check mockup for stored values
        assertEquals(input, te.fLSPServerStub.getTextDocumentService().fMockup.fInputReceived);
        assertEquals(0, te.fLSPClientStub.fMockup.fDiagnosticsReceived.size());
    }

    /**
     * TODO: ADD MORE TESTS!!
     */

}
