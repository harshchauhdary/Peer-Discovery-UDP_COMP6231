package com.comp6231.lab3.server;

import com.comp6231.socklib.SimpleSocketProtocol;

import java.net.InetAddress;

public class Protocols {

    public static void pdpProtocol(String peername, String[] splited){
        //Gets command and peername as an input, finds the peer ip and port, calls sendMessage() with it..
    }

    public static void sendMessage(InetAddress ip, int port, String message){
        //Gets message, ip and port as an input, sends message to the peer.
    }
}
