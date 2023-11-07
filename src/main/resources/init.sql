# user
INSERT INTO user(id, username, password, nickname, last_login_time, avatar)
VALUES (1, 'asow', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小杨', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771228636108726762425516032.png');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar)
VALUES (2, 'asow-admin', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '管理杨', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/1682577425969justus-menke-K7Sr4YDtL2U-unsplash.jpg');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar)
VALUES (3, 'chen', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小陈', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771289566108726968584339456.png');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar)
VALUES (4, 'li', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小李', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771338106108726556267085824.png');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar)
VALUES (5, 'peng', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小彭', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771388896108726453187870720.png');

# user_authorities
INSERT INTO user_authorities(user_id, authority)
VALUES (1, 'ROLE_USER');

INSERT INTO user_authorities(user_id, authority)
VALUES (2, 'ROLE_SUPER_ADMIN');

INSERT INTO user_authorities(user_id, authority)
VALUES (3, 'ROLE_ADMIN');

INSERT INTO user_authorities(user_id, authority)
VALUES (4, 'ROLE_USER');

INSERT INTO user_authorities(user_id, authority)
VALUES (5, 'ROLE_SUPER_ADMIN');

# chat_conversation
INSERT INTO chat_conversation(id, create_time, from_id, to_id, top_priority, last_message_id, type)
VALUES (1, '2023-01-26 17:02:05', 1, 2, 0,
        null, 0);

INSERT INTO chat_conversation(id, create_time, from_id, to_id, top_priority, last_message_id, type)
VALUES (2, '2023-01-26 17:02:05', 3, 1, 0,
        null, 0);

INSERT INTO chat_conversation(id, create_time, from_id, to_id, top_priority, last_message_id, type)
VALUES (3, '2023-01-26 17:02:05', 3, null, 0,
        null, 1);

# chat_group
INSERT INTO chat_group(id, avatar, create_time, name, conversation_id)
VALUES (1, 'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/dog-ball2.png', '2023-11-02 15:18:12',
        '测试群', 3);


# 初始化 新增 用户-会话 中间表
INSERT INTO chat_user_conversation(unread, conversation_id, user_id)
VALUES (0, 1, 1);
INSERT INTO chat_user_conversation(unread, conversation_id, user_id)
VALUES (0, 1, 2);
INSERT INTO chat_user_conversation(unread, conversation_id, user_id)
VALUES (0, 2, 1);
INSERT INTO chat_user_conversation(unread, conversation_id, user_id)
VALUES (0, 2, 3);
# 群组的
INSERT INTO chat_user_group(id, unread, user_in_group_name, chat_group_id, user_id)
VALUES (1, 0, '', 1, 1);
INSERT INTO chat_user_group(id, unread, user_in_group_name, chat_group_id, user_id)
VALUES (2, 0, '', 1, 3);
INSERT INTO chat_user_group(id, unread, user_in_group_name, chat_group_id, user_id)
VALUES (3, 0, '', 1, 4);

