package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

//import org.eclipse.tracecompass.incubator.internal.lsp.core.server.LanguageServerImpl;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
//import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class ClientImpl {

    private class SocketClient {

        private Socket socket;
        public String host;
        public int port;

        public SocketClient(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void start() {
            try {
                socket = new Socket(host, port);
                System.out.println("Connected to server");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public InputStream getInputStream() {
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return inputStream;
        }

        public OutputStream getOutputStream() {
            OutputStream outputStream = null;
            try {
                outputStream = socket.getOutputStream();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return outputStream;
        }

    }


    public ClientImpl() {

        SocketClient socketClient = new SocketClient("127.0.0.1", 9090);
        socketClient.start();

        InputStream in = socketClient.getInputStream();
        OutputStream out = socketClient.getOutputStream();

        LanguageClientImpl client = new LanguageClientImpl();
        Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(client, in, out);
        launcher.startListening();

    }

}
