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

import java.io.IOException;

import org.eclipse.tracecompass.incubator.lsp.core.environment.TestEnvironment;
import org.junit.Test;

public class LspClientTest {

    /**
     * Simple hello world tests. LSPClient send 'hello' to LSPServer
     *
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void hello() throws InterruptedException, IOException {
        String input = "Hello";
        String uri = "Mamma mia";
        /*
         * 3 transactions (thhat's what we want to verify)
         * 1. didOpen: client -> server
         * 2. didCHange: client -> server
         * 3. publishDiagnostics : server -> client
         */
        TestEnvironment te = new TestEnvironment(5);
        te.fLSPClient.getLanguageClient().tellDidOpen(uri);
        te.fLSPClient.notify(uri, input);

        // Lock till the transactions we're expecting is not over
        te.waitForTransactionToTerminate();

        // Check mockup for stored values
        assertEquals(input, te.fLSPServerStub.getTextDocumentService().fMockup.fInputReceived);
        assertEquals(0, te.fLSPClientStub.fMockup.fDiagnosticsReceived.size());
    }

    /**
     * TODO: ADD MORE TESTS!!
     */

}
