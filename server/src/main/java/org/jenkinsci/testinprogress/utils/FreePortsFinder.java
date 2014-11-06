package org.jenkinsci.testinprogress.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class FreePortsFinder {

    public static int findFreePort() {
        return findFreePorts(1).get(0);
    }
   
    public static List<Integer> findFreePorts(int numberOfPorts) {
        List<Integer> ports = new ArrayList<Integer>();
        List<ServerSocket> sockets = new ArrayList<ServerSocket>();
        try {
            for (int i = 0; i < numberOfPorts; i++) {
                ServerSocket socket = new ServerSocket(0);
                sockets.add(socket);
                ports.add(socket.getLocalPort());
            }
        } catch (IOException e) {
        } finally {
            for (ServerSocket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        if (ports.size() != numberOfPorts) {
            throw new RuntimeException(MessageFormat.format(
                    "Could not find {0} free ports", numberOfPorts));
        }
        return ports;
    }   
   
}