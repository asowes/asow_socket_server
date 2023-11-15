package com.young.asow.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class UserRelationshipId implements Serializable {

    private Long userId;

    private Long friendId;
}
