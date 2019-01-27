package org.eclipse.tracecompass.incubator.internal.lsp.core.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

public class Server implements LanguageServer {

    LanguageClient proxyClient;

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub

    }

    @Override
    public TextDocumentService getTextDocumentService() {
        // TODO Auto-generated method stub
        //this.proxyClient...
        return null;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        // TODO Auto-generated method stub
        return null;
    }

    @JsonRequest
    public CompletableFuture<String> requestWorld(Object params) {
        if (params.toString().equals("Hello")) {
            return CompletableFuture.completedFuture("World");
        }
        return CompletableFuture.completedFuture("Invalid");
    }

    public void setClient(LanguageClient proxy) {
        this.proxyClient = proxy;
    }

    public static void launchServer(InetAddress host, Integer port) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try (ServerSocket serverSocket = new ServerSocket(port, 100, host)) {
                    Server server = new Server();
                    System.out.println("Server listening on port " + port);
                    Socket socket = serverSocket.accept();
                    System.out.println("Connection established!");
                    Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, socket.getInputStream(), socket.getOutputStream());
                    server.setClient(launcher.getRemoteProxy());
                    launcher.startListening();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

}