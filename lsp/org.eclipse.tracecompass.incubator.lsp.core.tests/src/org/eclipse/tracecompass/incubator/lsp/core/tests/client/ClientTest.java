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


public class ClientTest {

    /**
     * Simple hello world tests.
     * LSPClient send 'hello' to LSPServer
     *
     * @throws InterruptedException
     */
    @Test
    public void hello() throws InterruptedException {
        String str = "Hello";
        TestEnvironment te = new TestEnvironment();
        te.fLSPClient.notify(str);

        // TODO: Change synchronization mechanism
        //Wait for transacitons to be done
        TimeUnit.SECONDS.sleep(1);

        //Check mockup for stored values
        assertEquals(str, te.fLSPServerStub.getTextDocumentService().fMockup.fReceived);
        assertEquals("VALID", te.fLSPClientStub.fMockup.fReceived);
        assertEquals("VALID", te.fLSPFakeClientStub.fMockup.fReceived);
    }

    /**
     * TODO: ADD MORE TESTS!!
     */

}
