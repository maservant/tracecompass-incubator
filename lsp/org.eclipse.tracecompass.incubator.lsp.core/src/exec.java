
import org.eclipse.tracecompass.incubator.internal.lsp.core.client.ClientImpl;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.ServerImpl;

public class exec {

    public static void main(String[] args) throws Exception {

        //String host = "127.0.0.1";
        //int port = 9090;

        new ServerImpl();
        new ClientImpl();

    }
}
