package org.eclipse.tracecompass.incubator.lsp.core.tests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.eclipse.tracecompass.incubator.lsp.core.tests.environment.TestEnvironment;
import org.junit.Test;

public class TestsClient {

    @Test
    public void Hello() {
        String str = "Hello";
        TestEnvironment te = new TestEnvironment();
        te.client.notify(str);

        //Wait for transacitons to be done
        try { TimeUnit.SECONDS.sleep(1); } catch(Exception e) { e.printStackTrace(); }

        //Check mockup for stored values
        assertEquals(str, te.serverStub.getTextDocumentService().mockup.received);
        assertEquals("VALID", te.clientStub.mockup.received);
        assertEquals("VALID", te.filterBoxStub.mockup.received);
    }

}
