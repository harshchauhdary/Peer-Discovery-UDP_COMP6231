
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
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GreeterServerProtocol extends SimpleSocketProtocol {
    public static HashMap<String, Set<Integer>> dictionary= new HashMap<>();
    private String messageToSend;

    public GreeterServerProtocol(Socket s, ListenerInfo info) {
        super(s, info);
    }

    public void run() throws IOException {
        String id = "peer1";
        // TODO modify this
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
                if(!peername.equals(id)) {
                    Protocols.pdpProtocol(peername, splited);
                } else{
                    rapProtocol(splited);
                }
            }

            //If there is no peer id in message, it will send the command to RAP Protocol
            else {
                rapProtocol(splited);
            }
            sendln(messageToSend);
        }
        close();
    }

    //RAP Protocol
    public void rapProtocol(String[] splited) throws IOException {
        //RAP Protocol
        switch (splited[0].trim().toUpperCase()) {
            case "ADD":
                this.insertToDict(splited);
                break;
            case "DELETE":
                this.deleteFromDict(splited);
                break;
            case "SET":
                this.setInDict(splited);
                break;
            case "GETVALUE":
                if(dictionary.containsKey(splited[1].trim())) {
                    this.messageToSend = dictionary.get(splited[1].trim()).iterator().next().toString();
                }else{this.messageToSend = "Key not found";};
                break;
            case "GETVALUES":
                if(dictionary.containsKey(splited[1].trim())) {
                    this.messageToSend = dictionary.get(splited[1].trim()).toString();
                }else{this.messageToSend = "Key not found";};
                break;
            case "EXIT":
                close();
                break;
//                case "KEYS":
//                    return;
//                case "RESET":
//                    return;
//                case "AVG":
//                    return;
//                case "MAX":
//                    return;
//                case "MIN":
//                    return;
//                case "SUM":
//                    return;
            default:
                this.messageToSend = "Invalid Command";
                break;
        }
    }


    //Dictionary Utils
    public void insertToDict(String[] message){
        try {
            if (dictionary.containsKey(message[1].trim())) {
                dictionary.get(message[1].trim()).add(Integer.parseInt(message[2].trim()));
            } else {
                dictionary.put(message[1].trim(), new HashSet<>(Arrays.asList(Integer.parseInt(message[2].trim()))));
            }
            this.messageToSend = "Inserted " + message[2].trim() + " at key " + message[1].trim();
        }
        catch (NumberFormatException ne){
            this.messageToSend = "Invalid Command! Try again.";
        }
    }

    public void deleteFromDict(String[] message){
        try {
            if (dictionary.containsKey(message[1].trim())) {
                dictionary.get(message[1].trim()).remove(Integer.parseInt(message[2].trim()));
                this.messageToSend = "Deleted " + message[2].trim() + " at key " + message[1].trim();
            }
            else {
                this.messageToSend = "Key doesn't exist";
            }

        }
        catch (NumberFormatException ne){
            this.messageToSend = "Invalid Command! Try again.";
        }
    }

    public void setInDict(String[] message){
        try {
            if (dictionary.containsKey(message[1].trim())) {
                dictionary.put(message[1].trim(), new HashSet<>(Arrays.asList(Integer.parseInt(message[2].trim()))));
                this.messageToSend = "Set " + message[2].trim() + " at key " + message[1].trim();
            }else{
                this.messageToSend = "Key not found in the dictionary";
            }
        }
        catch (NumberFormatException ne){
            this.messageToSend = "Invalid Command! Try again.";
        }
    }
}
