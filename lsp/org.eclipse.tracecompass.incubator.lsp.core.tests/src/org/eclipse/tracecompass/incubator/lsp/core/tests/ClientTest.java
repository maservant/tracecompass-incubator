/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.lsp.core.tests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.eclipse.tracecompass.incubator.lsp.core.tests.environment.TestEnvironment;
import org.junit.Test;

public class ClientTest {

    @Test
    public void hello() throws InterruptedException {
        String str = "Hello";
        TestEnvironment te = new TestEnvironment();
        te.client.notify(str);

        //Wait for transacitons to be done
        TimeUnit.SECONDS.sleep(1);

        //Check mockup for stored values
        assertEquals(str, te.serverStub.getTextDocumentService().mockup.received);
        assertEquals("VALID", te.clientStub.mockup.received);
        assertEquals("VALID", te.filterBoxStub.mockup.received);
    }

}
