package com.fl.Agent;

import lombok.Data;

@Data
public class AgentUrl {

    public AgentUrl(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    private String ip;

    private int port;
}
