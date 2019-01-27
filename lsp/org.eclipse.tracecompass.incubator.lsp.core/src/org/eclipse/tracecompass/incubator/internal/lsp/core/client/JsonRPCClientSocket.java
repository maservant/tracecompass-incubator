package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

import java.net.Socket;

public class JsonRPCClientSocket {


    private String host;
    private int port;

    public JsonRPCClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
