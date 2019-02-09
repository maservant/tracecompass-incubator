package org.eclipse.tracecompass.incubator.lsp.core.tests.environment;

import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

public class FilterBoxStub implements IObserver {

    public FilterBoxMockup mockup = new FilterBoxMockup();

    @Override
    public void notify(Object value) {
        // TODO Auto-generated method stub
        mockup.received = (String)value;
    }

}
