
/*
 *  GreaterServerProtocol.java
 *
 *  A server side protocol implementation using socklib.ServerListener
 *  See also: GreeterServerExample.java
 *
 *  (C) 2022 Ali Jannatpour <ali.jannatpour@concordia.ca>
 *
 *  This code is licensed under GPL.
 *
 */

package com.comp6231.lab3.server;

import com.comp6231.socklib.ListenerInfo;
import com.comp6231.socklib.SimpleSocketProtocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerProtocol extends SimpleSocketProtocol {
    private String messageToSend;

    public ServerProtocol(Socket s, ListenerInfo info) {
        super(s, info);
    }

    public void run() throws IOException {
        // TODO modify this
        new Thread(Protocols::pdpProtocol).start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendln("OK Greeter server ready to greet you.");
        while (isRunning() && isConnected()) {
            String data = recvln();
            System.out.println(data);
            String[] splited = data.split(" ");

            //Checking if message has a peer_id, and it's not that of the current peer itself
            if(splited.length > 1 && splited[1].trim().contains(".")){
                //ERAP Protocol
                String peername = splited[1].split("\\.")[0].trim();
                splited[1] = splited[1].split("\\.")[1].trim();
                if(!peername.equals(ServerInfo.peerID)) {
                    int c = 0;
                    InetAddress peerip=InetAddress.getByName("localhost");
                    int peerport=0;
                    for(Peers p : ServerInfo.peers) {
                        if(p.getPeerId().equals(peername)){
                            c++;
                            peerip = p.getIp();
                            peerport = Integer.parseInt(p.getPort());
                        }
                        System.out.println(p.getPeerId() + " " + p.getIp().toString() + " " + p.getPort());
                        //Protocols.sendMessage(peername, splited);
                    }
                    if(c==1){
                        messageToSend = Protocols.sendMessage(peerip, peerport, splited) + " in " + peername;
                    }
                } else{
                    messageToSend = Protocols.rapProtocol(splited);
                }
            }

            //If there is no peer id in message, it will send the command to RAP Protocol
            else {
                messageToSend = Protocols.rapProtocol(splited);
            }
            sendln(messageToSend);
        }
        close();
    }


}
