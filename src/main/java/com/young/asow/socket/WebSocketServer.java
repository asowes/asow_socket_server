package com.young.asow.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.young.asow.modal.MessageModal;
import com.young.asow.response.RestResponse;
import com.young.asow.service.ChatService;
import com.young.asow.util.auth.JWTUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
@ServerEndpoint(value = "/websocket/{token}")
public class WebSocketServer {

    @Autowired
    public static WebSocketService webSocketService;

    private static final long sessionTimeout = 60000;

    // 用来存放每个客户端对应的WebSocketServer对象
    private static final Map<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收id
    private String uid;

    private String token;

    // 连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        String userId = JWTUtil.getUserId(token);
        log.info("用户Id：" + userId);
        session.setMaxIdleTimeout(sessionTimeout);
        this.session = session;
        this.uid = userId;
        this.token = token;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
        webSocketMap.put(userId, this);
        log.info("websocket连接成功编号uid: " + userId + "，当前在线数: " + getOnlineClients());
        try {
            sendMessage("websocket连接成功编号uid: " + userId + "，当前在线数: " + getOnlineClients());
        } catch (IOException e) {
            log.error("websocket发送连接成功错误编号uid: " + userId + "，网络异常!!!");
        }
    }

    // 连接关闭调用的方法
    @OnClose
    public void onClose(Session session) throws IOException {
        try {
            if (webSocketMap.containsKey(uid)) {
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
     * @param session
     */
    @OnMessage(maxMessageSize = 1024 * 1000)
    public void onMessage(String message, Session session) throws IOException {
        String userId = JWTUtil.getUserId(token);

        MessageModal client = JSONObject.parseObject(message, MessageModal.class);

        handleMessageWithType(client, session);

        log.info("websocket收到客户端编号uid消息: " + userId);
    }


    private void handleMessageWithType(MessageModal clientMessage, Session session) throws IOException {
        switch (clientMessage.getEvent()) {
            case "chat":
                handleChat(clientMessage);
                break;
            case "notify":
                handleNotify();
                break;
            default:
                handlePing(session);
                break;
        }
    }

    private void handlePing(Session session) throws IOException {
        MessageModal sm = new MessageModal();
        sm.setType("pong");
        sm.setEvent("heartbeat");
        sm.setContent(LocalDateTime.now().toString());
        session.getBasicRemote().sendText(JSONObject.toJSONString(sm));
    }

    private void handleChat(MessageModal clientMessage) {

        //  发给自己，可以看作是系统消息
//            session.getBasicRemote().sendText(JSONObject.toJSONString(sm));

//        clientMessage.setLoading(false);
        // 发给目标
        sendMessageByWayBillId(clientMessage.getToId(), JSONObject.toJSONString(clientMessage));
        // 发给自己
        sendMessageByWayBillId(clientMessage.getFromId(), JSONObject.toJSONString(clientMessage));

        // 保存消息到数据库，刷新列表时加载 应该要等发送成功再保存
        webSocketService.saveMessageWithConversation(clientMessage);
    }

    private void handleNotify() {
        // TODO
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        log.error("websocket编号uid错误: " + this.uid + "原因: " + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 单机使用，外部接口通过指定的客户id向该客户推送消息
     *
     * @param key
     * @param message
     * @return boolean
     */
    public static boolean sendMessageByWayBillId(@NonNull String key, String message) {
        WebSocketServer webSocketServer = webSocketMap.get(key);
        if (Objects.nonNull(webSocketServer)) {
            try {
                webSocketServer.sendMessage(message);
                log.info("websocket发送消息编号uid为: " + key + "发送消息: " + message);
                return true;
            } catch (Exception e) {
                log.error("websocket发送消息失败编号uid为: " + key + "消息: " + message);
                return false;
            }
        } else {
            log.error("websocket未连接编号uid号为: " + key + "消息: " + message);
            return false;
        }
    }

    // 群发自定义消息
    public static void sendInfo(String message) {
        webSocketMap.forEach((k, v) -> {
            WebSocketServer webSocketServer = webSocketMap.get(k);
            try {
                webSocketServer.sendMessage(message);
                log.info("websocket群发消息编号uid为: " + k + "，消息: " + message);
            } catch (IOException e) {
                log.error("群发自定义消息失败: " + k + "，message: " + message);
            }
        });
    }

    /**
     * 服务端群发消息-心跳包
     *
     * @param message
     * @return int
     */
    public static synchronized int sendPing(String message) {
        if (webSocketMap.size() <= 0) {
            return 0;
        }
        StringBuffer uids = new StringBuffer();
        AtomicInteger count = new AtomicInteger();
        webSocketMap.forEach((uid, server) -> {
            count.getAndIncrement();
            if (webSocketMap.containsKey(uid)) {
                WebSocketServer webSocketServer = webSocketMap.get(uid);
                try {
                    webSocketServer.sendMessage(message);
                    if (count.equals(webSocketMap.size() - 1)) {
                        uids.append("uid");
                        return; // 跳出本次循环
                    }
                    uids.append(uid).append(",");
                } catch (IOException e) {
                    webSocketMap.remove(uid);
                    log.info("客户端心跳检测异常移除: " + uid + "，心跳发送失败，已移除！");
                }
            } else {
                log.info("客户端心跳检测异常不存在: " + uid + "，不存在！");
            }
        });
        log.info("客户端心跳检测结果: " + uids + "连接正在运行");
        return webSocketMap.size();
    }

    // 实现服务器主动推送
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    // 获取客户端在线数
    public static synchronized int getOnlineClients() {
        if (Objects.isNull(webSocketMap)) {
            return 0;
        } else {
            return webSocketMap.size();
        }
    }

    /**
     * 连接是否存在
     *
     * @param uid
     * @return boolean
     */
    public static boolean isConnected(String uid) {
        return Objects.nonNull(webSocketMap) && webSocketMap.containsKey(uid);
    }
}
