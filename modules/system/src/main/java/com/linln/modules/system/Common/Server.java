package com.linln.modules.system.Common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server")
public class Server {

    private String port;

    private String socketPort;

    private String reportPath;

    public Server() {
    }

    public Server(String port, String socketPort, String reportPath) {
        this.port = port;
        this.socketPort = socketPort;
        this.reportPath = reportPath;
    }
}
