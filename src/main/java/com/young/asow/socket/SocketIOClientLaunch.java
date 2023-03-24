package com.young.asow.socket;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SocketIOClientLaunch {
    public static void main(String[] args) {
        // 服务端socket.io连接通信地址
        String url = "http://127.0.0.1:9099";
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket", "xhr-polling", "jsonp-polling"};
            options.reconnectionAttempts = 2;
            // 失败重连的时间间隔
            options.reconnectionDelay = 1000;
            // 连接超时时间(ms)
            options.timeout = 500;
            // userId: 唯一标识 传给服务端存储
            final Socket socket = IO.socket(url + "?userId=2", options);

            socket.on(Socket.EVENT_CONNECT, args1 -> socket.send("hello..."));

            System.out.println("开始连接");
            // 自定义事件`connected` -> 接收服务端成功连接消息
            socket.on("connected", objects -> {
                System.out.println("服务端:" + objects[0].toString());
            });
            System.out.println("连接结束");

            // 自定义事件`push_data_event` -> 接收服务端消息
            socket.on("push_data_event", objects -> log.info("服务端:" + objects[0].toString()));

            // 自定义事件`myBroadcast` -> 接收服务端广播消息
            socket.on("myBroadcast", objects -> log.info("服务端：" + objects[0].toString()));

            socket.emit("chatevent", "1230");

            socket.connect();

            int i = 1;

            while (true) {
                Thread.sleep(3000);
                i++;
                // 自定义事件`push_data_event` -> 向服务端发送消息
                socket.emit("push_data_event", "发送数据 " + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
