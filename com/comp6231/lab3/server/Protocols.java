package com.comp6231.lab3.server;

import com.comp6231.socklib.SimpleSocketProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;

public class Protocols {

    public static void pdpProtocol(String peername, String[] splited){
        //Gets command and peername as an input, finds the peer ip and port, calls sendMessage() with it..
        DatagramSocket dSocket;
        try {
            //Open a random port to send the package
            dSocket = new DatagramSocket();
            dSocket.setBroadcast(true);
            byte[] sendData = "PEER_REQUEST".getBytes();
            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) { continue; } // Omit loopbacks
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) { continue; } //Don't send if no broadcast IP.
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 6231);
                        dSocket.send(sendPacket); // Send the broadcast package!
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("\n> Request sent to IP: "
                            + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println("\n> Done looping through all interfaces. Now waiting for a reply!");

            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            dSocket.receive(receivePacket); //Wait for a response

            //We have a response
            System.out.println("\n> Received packet from " + receivePacket.getAddress().getHostAddress() + " : " + receivePacket.getPort());
            String msg = new String(receivePacket.getData()).trim();
            String[] peer_details = msg.split(" ");
            if (peer_details.length == 3 && peer_details[0].equals("PEER_RESPONSE")) {
                System.out.println("\n> Ready to connect! ");
                sendMessage(receivePacket.getAddress(), peer_details[1].trim(), splited);
            }
            else{
                System.out.println("Invalid response from the server.");
            }

            dSocket.close();  //Close the port!
        } catch (
                IOException ex) {
            System.out.println("Hey, there is an error!!!");
        }
    }


    public static String sendMessage(InetAddress ip, String port, String[] splited){
        //Gets message, ip and port as an input, sends message to the peer.8
        try {
            Socket clientSocket = new Socket(ip, Integer.parseInt(port));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String resp = in.readLine();
            System.out.println(resp);

            String command = String.join(" ", splited);
            out.println(command);
            String resp2 = in.readLine();

            //return resp2
            System.out.println(resp2);

            in.close();
            out.close();
            clientSocket.close();
            return resp2;
        } catch (UnknownHostException ex) {
            return "Server not found: " + ex.getMessage();
        } catch (IOException ex) {
            return "I/O error: " + ex.getMessage();
        } catch(NumberFormatException ne){
            return "Port should be a number.";
        }
    }
}
