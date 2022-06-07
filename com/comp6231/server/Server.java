
/*
 *  GreeterServerExample.java
 *
 *  A sample server example using socklib.ServerListener
 *  See also: GreaterServerProtocol.java
 *
 *  (C) 2022 Ali Jannatpour <ali.jannatpour@concordia.ca>
 *
 *  This code is licensed under GPL.
 *
 */

package com.comp6231.server;

import com.comp6231.socklib.ServerListener;
import com.comp6231.socklib.ServerPDP;

import java.util.Scanner;

public class Server {
    public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Incorrect arguments. Run *java GreeterServerExample <tcp_port_number> <peer_id>* ");
			return;
		}
		try {
			ServerInfo.tcpPort = Integer.parseInt(args[0]);
			ServerInfo.peerID = args[1];
		}catch(NumberFormatException ne){
			System.out.println("Incorrect arguments. Run *java GreeterServerExample <tcp_port_number> <peer_id>* ");
			return;
		}

        try {
		ServerListener server = new ServerListener("TCPServer", ServerInfo.tcpPort, ServerProtocol::new);
		server.start();
		ServerPDP spdp = new ServerPDP();
		spdp.start();
		System.out.println("\nStarted TCP server port (6232)\nYou may open multiple simultaneous connections.");
		System.out.println("\nPress hit ENTER if you wish to stop the server. Note that the service may NOT stop immediately.");
		new Scanner(System.in).nextLine(); // waiting for EOL from the console to terminate
		server.stop();
		//spdp.stop();
        }
        catch (Exception ex) {
            System.out.println("FATAL ERROR: " + ex.getMessage());
        }
    }

}
