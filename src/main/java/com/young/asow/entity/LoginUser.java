package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Data
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    @NonNull
    String username;

    @Column
    @NonNull
    String password;
}
