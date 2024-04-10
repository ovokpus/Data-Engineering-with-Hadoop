package com.jmb.dto;


import java.io.Serializable;

public class PingAnomaly implements Serializable {

    String source;
    String destination;

    public PingAnomaly(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }
    public String getDestination() {
        return destination;
    }

}
