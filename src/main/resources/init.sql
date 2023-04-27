# user
INSERT INTO user(id, username, password, nickname, last_login_time, avatar, user_id)
VALUES (1, 'asow', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小杨', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771228636108726762425516032.png',
        '439094307840987136');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar, user_id)
VALUES (2, 'asow-admin', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '管理杨', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/1682577425969justus-menke-K7Sr4YDtL2U-unsplash.jpg',
        '439095402717908992');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar, user_id)
VALUES (3, 'chen', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小陈', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771289566108726968584339456.png',
        '439095402784521478');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar, user_id)
VALUES (4, 'li', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小李', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771338106108726556267085824.png',
        '439095404158745213');

INSERT INTO user(id, username, password, nickname, last_login_time, avatar, user_id)
VALUES (5, 'peng', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小彭', '2023-03-24 09:12:58',
        'https://raw.githubusercontent.com/walkonairy/asow-image/main/chat/16825771388896108726453187870720.png',
        '439095405547841147');

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
INSERT INTO chat_conversation(id, conversation_id, create_time, from_id, to_id, top_priority, unread, last_message_id)
VALUES (1, '4395213213213213123', '2023-01-26 17:02:05', '439094307840987136', '439095402784521478', null, 0,
        '4395213213213213123000018');

INSERT INTO chat_conversation(id, conversation_id, create_time, from_id, to_id, top_priority, unread, last_message_id)
VALUES (2, '4395213213213213199', '2023-01-26 17:02:05', '439094307840987136', '439095404158745213', null, 0,
        '4395213213213213199000001');

# chat_message
INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (1, '下次有机会去滑雪吧', '4395213213213213123', '439094307840987136', false, '4395213213213213123000001',
        '2023-04-26 17:02:05',
        '439095402784521478', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (2, '太好了，期待啊～1', '4395213213213213123', '439095402784521478', false, '4395213213213213123000002',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (3, '太好了，期待啊～2', '4395213213213213123', '439095402784521478', false, '4395213213213213123000003',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (4, '太好了，期待啊～3', '4395213213213213123', '439095402784521478', false, '4395213213213213123000004',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (5, '太好了，期待啊～4', '4395213213213213123', '439095402784521478', false, '4395213213213213123000005',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (6, '太好了，期待啊～5', '4395213213213213123', '439095402784521478', false, '4395213213213213123000006',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (7, '太好了，期待啊～6', '4395213213213213123', '439095402784521478', false, '4395213213213213123000007',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (8, '太好了，期待啊～7', '4395213213213213123', '439095402784521478', false, '4395213213213213123000008',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (9, '太好了，期待啊～8', '4395213213213213123', '439095402784521478', false, '4395213213213213123000009',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (10, '太好了，期待啊～9', '4395213213213213123', '439095402784521478', false, '4395213213213213123000010',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (11, '太好了，期待啊～10', '4395213213213213123', '439095402784521478', false, '4395213213213213123000011',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (12, '太好了，期待啊～11', '4395213213213213123', '439095402784521478', false, '4395213213213213123000012',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (13, '太好了，期待啊～12', '4395213213213213123', '439095402784521478', false, '4395213213213213123000013',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (14, '太好了，期待啊～13', '4395213213213213123', '439095402784521478', false, '4395213213213213123000014',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (15, '太好了，期待啊～14', '4395213213213213123', '439095402784521478', false, '4395213213213213123000015',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (16, '太好了，期待啊～15', '4395213213213213123', '439095402784521478', false, '4395213213213213123000016',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (17, '太好了，期待啊～16', '4395213213213213123', '439095402784521478', false, '4395213213213213123000017',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (18, '太好了，期待啊～17', '4395213213213213123', '439095402784521478', true, '4395213213213213123000018',
        '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (19, '你五一去哪啊？', '4395213213213213199', '439094307840987136', true, '4395213213213213199000001',
        '2023-04-26 17:02:05',
        '439095404158745213', '1');