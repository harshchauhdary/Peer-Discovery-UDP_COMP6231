package com.comp6231.lab3.server;

import com.comp6231.socklib.SimpleSocketProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class Protocols {
    public static HashMap<String, Set<Integer>> dictionary= new HashMap<>();

    public static void pdpProtocol(){
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
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                } // Omit loopbacks
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    } //Don't send if no broadcast IP.
                    System.out.println("Broadcast" + broadcast);
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 6231);
                        dSocket.send(sendPacket); // Send the broadcast package!
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    //System.out.println("\n> Request sent to IP: "
                          //  + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            //System.out.println("\n> Done looping through all interfaces. Now waiting for a reply!");
            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            while (true) {
                dSocket.receive(receivePacket); //Wait for a response
                System.out.println("received " + receivePacket.getData());

                //We have a response
                //System.out.println("\n> Received packet from " + receivePacket.getAddress().getHostAddress() + " : " + receivePacket.getPort());
                String msg = new String(receivePacket.getData()).trim();
                System.out.println(msg);
                String[] peer_details = msg.split(" ");
                if (peer_details.length == 3 && peer_details[0].equals("PEER_RESPONSE")) {
                    System.out.println("found 1");
                    if(!ServerInfo.peers.contains(new Peers(peer_details[2].trim(), peer_details[1].trim(), receivePacket.getAddress()))) {
                        ServerInfo.peers.add(new Peers(peer_details[2].trim(), peer_details[1].trim(), receivePacket.getAddress()));
                    }
                } else {
                    System.out.println("Invalid response from the server.");
                }


            }
            //dSocket.close(); //Close the port!
        }
        catch(IOException ex){
            System.out.println("Hey, there is an error!!!");
        }

    }


    public static String sendMessage(InetAddress ip, int port, String[] splited){
        //Gets message, ip and port as an input, sends message to the peer.8

        try {
            Socket clientSocket = new Socket(ip, port);
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

    //RAP Protocol
    public static String rapProtocol(String[] splited) throws IOException {
        //RAP Protocol
        switch (splited[0].trim().toUpperCase()) {
            case "ADD":
                return insertToDict(splited);

            case "DELETE":
                return deleteFromDict(splited);

            case "SET":
                return setInDict(splited);

            case "GETVALUE":
                if(dictionary.containsKey(splited[1].trim())) {
                    return dictionary.get(splited[1].trim()).iterator().next().toString();
                }else{return "Key not found";}

            case "GETVALUES":
                if(dictionary.containsKey(splited[1].trim())) {
                    return dictionary.get(splited[1].trim()).toString();
                }else{return "Key not found";}

            case "GP":
                String peersnames = "Peers: ";
                if(!ServerInfo.peers.isEmpty())
                {
                    for(Peers peer:ServerInfo.peers){
                        peersnames += peer.getPeerId() + " ";
                    }
                }
                return peersnames;

            case "KEYS":
                String keys = "Keys: ";
                for(String key : dictionary.keySet()){
                    keys += key + " | ";
                }
                return keys;

            case "RESET":
                dictionary.clear();
                return "Reset successful";

            case "AVG":

            case "SUM":

            case "COUNT":

            case "MIN":

            case "MAX":
                return calcAggregates(splited);

            case "DSUM":
                return getDSUM(splited);

            default:
                return "Invalid Command";
        }
    }


    //Dictionary Utils
    public static String insertToDict(String[] message){
        try {
            if (dictionary.containsKey(message[1].trim())) {
                dictionary.get(message[1].trim()).add(Integer.parseInt(message[2].trim()));
            } else {
                dictionary.put(message[1].trim(), new HashSet<>(Arrays.asList(Integer.parseInt(message[2].trim()))));
            }
            return "Inserted " + message[2].trim() + " at key " + message[1].trim();
        }
        catch (NumberFormatException ne){
            return "Invalid Command! Try again.";
        }
    }

    public static String deleteFromDict(String[] message){
        try {
            if (dictionary.containsKey(message[1].trim())) {
                dictionary.get(message[1].trim()).remove(Integer.parseInt(message[2].trim()));
                return "Deleted " + message[2].trim() + " at key " + message[1].trim();
            }
            else {
                return "Key doesn't exist";
            }

        }
        catch (NumberFormatException ne){
            return "Invalid Command! Try again.";
        }
    }

    public static String setInDict(String[] message){
        try {
            if (dictionary.containsKey(message[1].trim())) {
                dictionary.put(message[1].trim(), new HashSet<>(Arrays.asList(Integer.parseInt(message[2].trim()))));
                return "Set " + message[2].trim() + " at key " + message[1].trim();
            }else{
                return "Key not found in the dictionary";
            }
        }
        catch (NumberFormatException ne){
            return "Invalid Command! Try again.";
        }
    }

    public static String calcAggregates(String[] message){
        if(dictionary.containsKey(message[1].trim()) && !dictionary.get(message[1].trim()).isEmpty()) {
            double value = 0;
            IntSummaryStatistics ss = dictionary.get(message[1].trim()).stream().mapToInt(Integer::intValue).summaryStatistics();
            if(message[0].trim().equalsIgnoreCase("SUM"))
                value = ss.getSum();
            if(message[0].trim().equalsIgnoreCase("COUNT"))
                value = ss.getCount();
            if(message[0].trim().equalsIgnoreCase("AVG"))
                value = ss.getAverage();
            if(message[0].trim().equalsIgnoreCase("MIN"))
                value = ss.getMin();
            if(message[0].trim().equalsIgnoreCase("MAX"))
                value = ss.getMax();
            return Double.toString(value);
        }else{return "Key not found";}
    }

    public static String getDSUM(String[] message) throws UnknownHostException {
        int dsum = 0;
        try {
            dsum += Double.parseDouble(calcAggregates(new String[]{"sum", message[1].trim()}));
        }
        catch (NumberFormatException ne){
            return "Key no found in " + ServerInfo.peerID;
        }

        if(message.length>2 && message[2].trim().equalsIgnoreCase("INCLUDING")){
            String[] pl = Arrays.copyOfRange(message, 3, message.length);
            System.out.println(pl.length);
            for(String peer:pl){
                int c = 0;
                InetAddress peerip=InetAddress.getByName("localhost");
                int peerport=0;
                for(Peers p : ServerInfo.peers) {
                    if(p.getPeerId().equals(peer.trim())){
                        c++;
                        peerip = p.getIp();
                        peerport = Integer.parseInt(p.getPort());
                    }
                    System.out.println(p.getPeerId() + " " + p.getIp().toString() + " " + p.getPort());
                    //Protocols.sendMessage(peername, splited);
                }
                if(c==1){
                    try {
                        System.out.println(Protocols.sendMessage(peerip, peerport, new String[]{"sum", message[1].trim()}));
                        dsum += Double.parseDouble(Protocols.sendMessage(peerip, peerport, new String[]{"sum", message[1].trim()}));
                    }catch(NumberFormatException ne){
                        return "Key not found in "+peer;
                    }
                    }else {
                    return  "SERVER: ERR Non-existence or ambiguous repository " + peer;}
            }

        }
        return String.valueOf(dsum);

    }

}
