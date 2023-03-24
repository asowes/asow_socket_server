# user
INSERT INTO user(id, username, password, nickname, last_login_time)
VALUES (1, 'asow', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小杨', '2023-03-24 09:12:58');

INSERT INTO user(id, username, password, nickname, last_login_time)
VALUES (2, 'asow-admin', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '管理杨', '2023-03-24 09:12:58');

INSERT INTO user(id, username, password, nickname, last_login_time)
VALUES (3, 'chen', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小陈', '2023-03-24 09:12:58');

INSERT INTO user(id, username, password, nickname, last_login_time)
VALUES (4, 'li', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小李', '2023-03-24 09:12:58');

INSERT INTO user(id, username, password, nickname, last_login_time)
VALUES (5, 'peng', '$2a$10$WU15.AWNYI984CxEA9RZb.vfTXKzslxSLI4hWpLlvMFm8zEA9MvSy', '小彭', '2023-03-24 09:12:58');

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

# chat
INSERT INTO chat(id, from_id, to_id, status)
VALUES (1, 1, 3, '1');

INSERT INTO chat(id, from_id, to_id, status)
VALUES (2, 3, 1, '1');

INSERT INTO chat(id, from_id, to_id, status)
VALUES (3, 1, 3, '1');

INSERT INTO chat(id, from_id, to_id, status)
VALUES (4, 3, 1, '1');

INSERT INTO chat(id, from_id, to_id, status)
VALUES (5, 1, 3, '1');

INSERT INTO chat(id, from_id, to_id, status)
VALUES (6, 1, 3, '1');

# chat_content
INSERT INTO chat_content(content, send_time, type, chat_id)
VALUES ('现在港澳通行证可以到自助机签约的吧？签了有效期是多久的来着，想签下香港的，在哪里弄呢', '2023-03-24 13:01:51', '1', 1);

INSERT INTO chat_content(content, send_time, type, chat_id)
VALUES ('签证有效期好像有分两种，几个月一次或是一年几次来着', '2023-03-24 13:02:15', '1', 2);

INSERT INTO chat_content(content, send_time, type, chat_id)
VALUES ('可以在自助机签的是吧', '2023-03-24 13:03:12', '1', 3);

INSERT INTO chat_content(content, send_time, type, chat_id)
VALUES ('可以搜一下哪里有自助机', '2023-03-24 13:10:11', '1', 4);

INSERT INTO chat_content(content, send_time, type, chat_id)
VALUES ('好的 没问题', '2023-03-24 13:14:56', '1', 5);

INSERT INTO chat_content(content, send_time, type, chat_id)
VALUES ('那我有空就去试试', '2023-03-24 13:15:32', '1', 6);