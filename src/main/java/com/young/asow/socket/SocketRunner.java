package com.young.asow.socket;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SocketRunner implements CommandLineRunner {
    private final SocketIOServer server;

    public SocketRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        log.info("--------------------前端socket.io通信启动成功！---------------------");
    }
}
