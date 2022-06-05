package com.comp6231.lab3.server;

import java.net.InetAddress;

public class Peers {
    public String peerId, port;
    public InetAddress ip;
    public Peers(String peerId, String port, InetAddress ip){
        this.peerId = peerId;
        this.port = port;
        this.ip = ip;
    }
    public String getPeerId(){
        return peerId;
    }

    public String getPort() {
        return port;
    }

    public InetAddress getIp() {
        return ip;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Peers)){
            return false;
        }
        Peers p = (Peers) object;
        return peerId.equals(p.peerId) && ip.equals(p.ip) && port.equals(p.port);
    }
}
