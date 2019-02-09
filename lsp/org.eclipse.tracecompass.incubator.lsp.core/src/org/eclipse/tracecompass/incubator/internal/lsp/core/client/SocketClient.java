package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient {

    private Socket socket;

    /**
     * Simple socket with methods to get Input and Output stream
     * @param hostname: hostname of server to connect to
     * @param port: port of server
     */
    public SocketClient(String hostname, Integer port) {
        try {
            socket = new Socket(hostname, port);
            System.out.println("SocketClient connected to server");
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
