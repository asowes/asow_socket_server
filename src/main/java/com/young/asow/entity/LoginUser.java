package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    @Column
    @NonNull
    @Id
    Long id;

    @Column
    @NonNull
    String username;

    @Column
    @NonNull
    String password;
}
