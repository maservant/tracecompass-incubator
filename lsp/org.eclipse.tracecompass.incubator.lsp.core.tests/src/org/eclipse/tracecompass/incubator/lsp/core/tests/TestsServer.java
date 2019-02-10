package org.eclipse.tracecompass.incubator.lsp.core.tests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.eclipse.tracecompass.incubator.lsp.core.tests.environment.TestEnvironment;
import org.junit.Test;

public class TestsServer {

    @Test
    public void ValidityReply() {
        String[] strArray = {"TID", "TID==", "TID==28"};
        String[] validity = {"VALID", "INVALID", "VALID"};

        TestEnvironment te = new TestEnvironment();

        for (int i = 0; i < strArray.length; i++) {
            te.clientStub.tellDidChange(strArray[i]);

            //Wait for transactions to be done
            try { TimeUnit.SECONDS.sleep(1); } catch(Exception e) { e.printStackTrace(); }

            //Check mockup for stored values
            assertEquals(validity[i], te.clientStub.mockup.received);
        }
    }
}
