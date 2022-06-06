
package com.comp6231.socklib;

import com.comp6231.lab3.server.ServerInfo;

import java.net.*;

public class ServerPDP {
	DatagramSocket dSocket = null;
	private int udpPort = 6231;
	public void start() {
	new Thread(new Runnable() {
		@Override
		public void run(){
			runSdu();
			dSocket.close();
			}
		}).start();
	}
	private void runSdu() {
		try {
			byte[] receiveBuff = new byte[1024]; //receiving buffer

			dSocket = new DatagramSocket(null);
			dSocket.setReuseAddress(true);
			dSocket.bind(new InetSocketAddress(udpPort));
			dSocket.setBroadcast(true);

			DatagramPacket dPacket = new DatagramPacket(receiveBuff, receiveBuff.length);
			System.out.println("\nStarted UDP server, listening on Broadcast IP, port 6231\n");
			while (true) {
				System.out.println("> Ready to receive b-cast packets...");
				dSocket.receive(dPacket); //receiving data
				//System.out.println("> Received packet from " + dPacket.getAddress().getHostAddress()
				//	+ ":" + dPacket.getPort());
				String msg = new String(dPacket.getData(), dPacket.getOffset(), dPacket.getLength());
				if (msg.equals("PEER_REQUEST")) {
					//TODO implement TCP port passing to clients
					
					String srvResponse = "PEER_RESPONSE " + ServerInfo.tcpPort + " " + ServerInfo.peerID;
					//System.out.println(srvResponse);
					byte[] sendBuff = srvResponse.getBytes();
					DatagramPacket dPacket2 = new DatagramPacket(sendBuff, sendBuff.length, dPacket.getAddress(), dPacket.getPort());
					dSocket.send(dPacket2); 	//Send a response
					//System.out.println(getClass().getName() + "> Sent response to client IP: "
					//	+ dPacket.getAddress().getHostAddress() + ":" + dPacket.getPort());
				}
				//Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  }
