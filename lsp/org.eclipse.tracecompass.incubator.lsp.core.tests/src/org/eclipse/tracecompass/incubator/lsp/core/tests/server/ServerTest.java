/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.lsp.core.tests.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.tracecompass.incubator.lsp.core.environment.TestEnvironment;
import org.junit.Test;

public class ServerTest {

    @Test
    public void ValidityReply() throws InterruptedException, IOException {
        String[] strArray = { "TID", "TID==", "TID==28" };
        int[] validity = { 0, 1, 0 };

        String uri = "Arriba";

        for (int i = 0; i < strArray.length; i++) {
            /**
             * We expect 5 transactions:
             * 1.DidOpen: client -> Server
             * 2.DidChange: client -> server
             * 3.publishDiagnostics: server -> client
             * 4.syntaxHighlight: client <-> server
             * 5.documentColor: client <->server
             */
            TestEnvironment te = new TestEnvironment(5);
            te.fLSPClient.getLanguageClient().tellDidOpen(uri);
            te.fLSPClient.getLanguageClient().tellDidChange(uri, strArray[i]);

            // Wait for transactions to complete
            te.waitForTransactionToTerminate();

            // Check mockup for stored values
            assertEquals(validity[i], te.fLSPClientStub.fMockup.fDiagnosticsReceived.size());
        }
    }

    /**
     * TODO: ADD MORE TESTS!
     */
}
