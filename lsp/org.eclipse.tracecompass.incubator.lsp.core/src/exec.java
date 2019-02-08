
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.Client;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Server;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

public class exec {


    public static void main(String[] args) throws Exception {

        new Server();
        new Client(new IObserver() {

            @Override
            public void notify(Object value) {
                // TODO Auto-generated method stub

            }
        });

    }

}
