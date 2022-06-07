package com.comp6231.server;

import java.net.*;

public class Test {
    public static void main(String[] args) throws SocketException, UnknownHostException {
//        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//        while (interfaces.hasMoreElements()) {
//            NetworkInterface networkInterface = interfaces.nextElement();
//            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
//                continue;
//            } // Omit loopbacks
//            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
//                System.out.println(interfaceAddress.getAddress());
//                InetAddress broadcast = interfaceAddress.getBroadcast();
//                if (broadcast == null) {
//                    continue;
//                } //Don't send if no broadcast IP.
//                //System.out.println(interfaceAddress);
//                //System.out.println("\n> Request sent to IP: "
//                //  + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
//            }
//        }
        ServerInfo.peers.add(new Peers("peer1", "1234", InetAddress.getByName("localhost")));
        System.out.println(ServerInfo.peers.contains(new Peers("peer1", "1234", InetAddress.getByName("localhost"))));
        System.out.println(new Peers("peer1", "1234", InetAddress.getByName("localhost")).equals(new Peers("peer1", "1234", InetAddress.getByName("localhost"))));
    }
}
