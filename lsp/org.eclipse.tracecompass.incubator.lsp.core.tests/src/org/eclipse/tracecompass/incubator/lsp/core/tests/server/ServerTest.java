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

import java.util.concurrent.TimeUnit;

import org.eclipse.tracecompass.incubator.lsp.core.environment.TestEnvironment;
import org.junit.Test;

public class ServerTest {

    @Test
    public void ValidityReply() throws InterruptedException {
        String[] strArray = {"TID", "TID==", "TID==28"};
        String[] validity = {"VALID", "INVALID", "VALID"};

        //TODO: TestEnvironment needs to be reset every time we call a new function!!
        TestEnvironment te = new TestEnvironment();

        for (int i = 0; i < strArray.length; i++) {
            te.fLSPClientStub.tellDidChange(strArray[i]);

            // TODO: Change synchronization mechanism
            //Wait for transactions to be done
            TimeUnit.SECONDS.sleep(1);

            //Check mockup for stored values
            assertEquals(validity[i], te.fLSPClientStub.fMockup.fReceived);
        }
    }

    /**
     * TODO: ADD MORE TESTS!
     */
}
