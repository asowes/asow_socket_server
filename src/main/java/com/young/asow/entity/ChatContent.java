package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity(name = "chat_content")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChatContent extends BaseEntity {

    @Length(max = 1024)
    @Column(columnDefinition = "varchar(1024)")
    String content;

    @Column
    LocalDateTime sendTime;

    // 消息类型  1：普通消息  2：图片  3：视频
    @Column
    String type;

    @OneToOne(fetch = FetchType.LAZY)
    Chat chat;
}
