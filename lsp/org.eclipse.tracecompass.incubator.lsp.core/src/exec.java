
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.Client;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Server;

public class exec {

    public static void main(String[] args) throws Exception {

        String host = "127.0.0.1";
        int port = 9090;

        new Server(host, port);
        new Client(host, port);

    }
}
