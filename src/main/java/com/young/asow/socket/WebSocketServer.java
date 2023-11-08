package com.young.asow.socket;

import com.alibaba.fastjson.JSONObject;
import com.young.asow.modal.MessageModal;
import com.young.asow.util.auth.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
@ServerEndpoint(value = "/websocket/{token}")
public class WebSocketServer {

    @Autowired
    public static WebSocketService webSocketService;

    private static final long sessionTimeout = 60000;

    // 用来存放每个客户端对应的WebSocketServer对象
    private static final ConcurrentHashMap<Long, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收id
    private Long uid;

    private String token;

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        Long userId = JWTUtil.getUserId(token);
        log.info("用户Id：" + userId);
        session.setMaxIdleTimeout(sessionTimeout);
        this.session = session;
        this.uid = userId;
        this.token = token;
        if (isConnected(userId)) {
            webSocketMap.remove(userId);
        }
        webSocketMap.put(userId, this);
        log.info("websocket连接成功编号uid: " + userId + "，当前在线数: " + getOnlineClients());
        MessageModal modal = new MessageModal();
        modal.setEvent("init");
        modal.setContent("websocket连接成功编号uid: " + userId + "，当前在线数: " + getOnlineClients());
        sendMessage(userId, JSONObject.toJSONString(modal));
    }

    @OnClose
    public void onClose(Session session) {
        try (session) {
            if (isConnected(uid)) {
                webSocketMap.remove(uid);
            }
            log.info("websocket退出编号uid: " + uid + "，当前在线数为: " + getOnlineClients());
        } catch (Exception e) {
            log.error("websocket编号uid连接关闭错误: " + uid + "，原因: " + e.getMessage());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage(maxMessageSize = 1024 * 1000)
    public void onMessage(String message, Session session) throws IOException {
        Long userId = JWTUtil.getUserId(token);

        MessageModal client = JSONObject.parseObject(message, MessageModal.class);

        handleMessageWithType(client, session);

        log.info("websocket收到客户端编号uid消息: " + userId);
    }


    private void handleMessageWithType(MessageModal clientMessage, Session session) throws IOException {
        switch (clientMessage.getEvent()) {
            case "heartbeat":
                handlePing();
                break;
            case "chat":
                handleChat(clientMessage);
                break;
            case "notify":
                handleNotify();
                break;
            case "friend_apply":
                handleFriendApply(clientMessage);
                break;
            case "typing":
                handleTyping(clientMessage);
                break;
        }
    }

    private void handlePing() {
        MessageModal sm = new MessageModal();
        sm.setType("pong");
        sm.setEvent("heartbeat");
        sm.setContent(LocalDateTime.now().toString());
        sendMessage(this.uid, JSONObject.toJSONString(sm));
    }

    private void handleChat(MessageModal clientMessage) {
        try {
            // 保存消息到数据库，刷新列表时加载   初步定下来：等保存成功再发送
            Long userId = JWTUtil.getUserId(token);
            List<MessageModal> dbModals = webSocketService.saveMessageWithConversation(clientMessage, userId);

            // 发给自己
            // messageId 都一样 所以取第一条的id即可
            clientMessage.setId(dbModals.get(0).getId());
            sendMessage(clientMessage.getFromId(), JSONObject.toJSONString(clientMessage));
            Thread.sleep(100);

            // 发给目标
            // 给目标增加未读
            dbModals.forEach(messageModal -> {
                clientMessage.setUnread(messageModal.getUnread());
                sendMessage(messageModal.getToId(), JSONObject.toJSONString(clientMessage));
            });
        } catch (Exception e) {
            log.error("发送消息发送异常：" + e.getMessage());
        }
    }

    // TODO
    private void handleNotify() {
    }

    private void handleFriendApply(MessageModal clientMessage) {
        // TODO
        Long userId = JWTUtil.getUserId(token);
    }

    private void handleTyping(MessageModal clientMessage) {
        sendMessage(clientMessage.getToId(), JSONObject.toJSONString(clientMessage));
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket编号uid错误: " + this.uid + "原因: " + error.getMessage());
        MessageModal sm = new MessageModal();
        sm.setEvent("error");
        sm.setContent(error.getMessage());
        sendMessage(this.uid, JSONObject.toJSONString(sm));
    }


    public static void sendMessage(Long uid, String message) {
        synchronized (webSocketMap) {
            WebSocketServer userSocket = webSocketMap.get(uid);
            if (userSocket != null && isConnected(uid)) {
                Session session = userSocket.session;
                if (session != null && session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 获取客户端在线数
     */
    public static synchronized int getOnlineClients() {
        return webSocketMap.size();
    }

    /**
     * 连接是否存在
     */
    public static boolean isConnected(Long uid) {
        return webSocketMap.containsKey(uid);
    }
}
