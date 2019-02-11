package org.eclipse.tracecompass.incubator.internal.lsp.core.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Simple socket for client-side
 * @author maxtibs
 *
 */
public class SocketClient {

    private Socket socket;

    /**
     * Simple socket with methods to get Input and Output stream
     * @param hostname of server to connect to
     * @param port of server
     */
    public SocketClient(String hostname, Integer port) {
        try {
            socket = new Socket(hostname, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the socket InputStream
     * @return InputStream
     */
    public InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * Return the socket OutputStream
     * @return OutputStram
     */
    public OutputStream getOutputStream() {
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream;
    }
}
