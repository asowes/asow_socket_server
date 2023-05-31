# 通过用户获取所有对话列表以及未读数量
select *
from chat_user_conversation
where user_id = 1;

# 通过会话列表获取会话
select *
from chat_conversation
where id = 1;

# 通过会话id获取所有消息
select *
from chat_message
where conversation_id = 1;

# 初始化 新增会话
insert into chat_conversation(id, create_time, top_priority, from_id, last_message_id, to_id)
VALUES (1, '2023-05-08 15:22:11', 0, 1, null, 2);

# 初始化 新增 用户-会话 中间表
insert into chat_user_conversation(unread, conversation_id, user_id)
VALUES (0, 1, 1);
insert into chat_user_conversation(unread, conversation_id, user_id)
VALUES (0, 1, 2);

# 发送消息
insert into chat_message(id, content, message_id, send_time, type, conversation_id, from_id, to_id)
VALUES (1, '测试', '10001', '2023-05-08 15:22:11', '1', 1, 1, 2);
# 将接收者的未读数量 + 1
update chat_user_conversation
set unread = unread + 1
where conversation_id = 1
  and user_id = 2;

insert into chat_message(id, content, message_id, send_time, type, conversation_id, from_id, to_id)
VALUES (2, '回复你了', '10001', '2023-05-08 15:22:11', '1', 1, 2, 1);
# 将接收者的未读数量 + 1
update chat_user_conversation
set unread = unread + 1
where conversation_id = 1
  and user_id = 1;

# 读消息
update chat_user_conversation
set unread = 0
where conversation_id = 1
  and user_id = 1;