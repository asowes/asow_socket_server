package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Spit extends BaseEntity {

    @Column
    String title;

    @Column
    String content;

    @Column
    Level level;

    public enum Level {
        LOW,
        MEDIUM,
        HEIGHT,
        VERY,
        SUPER
        ;
    }
}
