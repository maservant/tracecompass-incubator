package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

//import org.eclipse.tracecompass.incubator.internal.lsp.core.client.LanguageClientImpl;
import java.net.ServerSocket;
//import java.net.InetAddress;
import java.net.Socket;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
//import org.eclipse.lsp4j.services.LanguageServer;

import java.io.InputStream;
import java.io.OutputStream;

public class Server {

    private class mSocket {

        public String host;
        public int port;
        public ServerSocket serverSocket;

        public mSocket(String host, int port) {
            this.host = host;
            this.port = port;
            try {
                this.serverSocket = new ServerSocket(port);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public void start() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    /*while (true) {*/
                        try {
                            Socket socket = serverSocket.accept();
                            String ip = socket.getInetAddress().toString();
                            System.out.println("Un client s'est connecté à partir de " + ip);

                            //Instantiate LSP client
                            InputStream in = socket.getInputStream();
                            OutputStream out = socket.getOutputStream();

                            LanguageServerImpl server = new LanguageServerImpl();
                            Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);
                           // server.connect(launcher.getRemoteProxy());
                            launcher.startListening();


                            //Close thread
                            serverSocket.close();

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                   // }
                }
            });
            t.start();
        }
    }

    public Server(String host, int port) {
        mSocket socket = new mSocket(host, port);
        socket.start();
    }

}
