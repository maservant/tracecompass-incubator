package org.eclipse.tracecompass.incubator.internal.lsp.core.demo;

import java.net.InetAddress;

import org.eclipse.tracecompass.incubator.internal.lsp.core.client.Client;
import org.eclipse.tracecompass.incubator.internal.lsp.core.server.Server;

public class Demo {
    public static void main(String args[]) {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String address = ip.getHostAddress();
            System.out.println("Demo started...");
            System.out.println("IP address = " + address);
            Server.launchServer(ip, 9090);
            Client.launchClient(ip, 9090);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}