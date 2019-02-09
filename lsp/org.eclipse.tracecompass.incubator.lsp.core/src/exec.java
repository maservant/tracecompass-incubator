
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.Client;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Server;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.Configuration;
import org.eclipse.tracecompass.incubator.internal.lsp.core.shared.IObserver;

public class exec {

    public static void main(String[] args) throws Exception {

        new Server();
        Client client = new Client(Configuration.HOSTNAME, Configuration.PORT, new IObserver() {

            @Override
            public void notify(Object value) {
                // TODO Auto-generated method stub
                System.out.println(value.toString());
            }

        });

        client.notify("HELLO");
    }
}
