# user
INSERT INTO user(id, username, password, nickname, last_login_time, user_id)
VALUES (1, 'asow', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小杨', '2023-03-24 09:12:58',
        '439094307840987136');

INSERT INTO user(id, username, password, nickname, last_login_time, user_id)
VALUES (2, 'asow-admin', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '管理杨', '2023-03-24 09:12:58',
        '439095402717908992');

INSERT INTO user(id, username, password, nickname, last_login_time, user_id)
VALUES (3, 'chen', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小陈', '2023-03-24 09:12:58',
        '439095402784521478');

INSERT INTO user(id, username, password, nickname, last_login_time, user_id)
VALUES (4, 'li', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小李', '2023-03-24 09:12:58',
        '439095404158745213');

INSERT INTO user(id, username, password, nickname, last_login_time, user_id)
VALUES (5, 'peng', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小彭', '2023-03-24 09:12:58',
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
        '4395215123165913124');

INSERT INTO chat_conversation(id, conversation_id, create_time, from_id, to_id, top_priority, unread, last_message_id)
VALUES (2, '4395213213213213199', '2023-01-26 17:02:05', '439094307840987136', '439095404158745213', null, 0,
        '4395215123165913125');

# chat_message
INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (1, '下次有机会去滑雪吧', '4395213213213213123', '439094307840987136', false, '4395215123165913123', '2023-04-26 17:02:05',
        '439095402784521478', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (2, '太好了，期待啊～', '4395213213213213123', '439095402784521478', true, '4395215123165913124', '2023-04-26 17:02:05',
        '439094307840987136', '1');

INSERT INTO chat_message(id, content, conversation_id, from_id, is_latest, message_id, send_time, to_id, type)
VALUES (3, '你五一去哪啊？', '4395213213213213199', '439094307840987136', true, '4395215123165913125', '2023-04-26 17:02:05',
        '439095404158745213', '1');