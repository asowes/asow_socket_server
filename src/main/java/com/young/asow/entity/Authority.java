package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)

@Embeddable
public class Authority implements GrantedAuthority {

    String authority;

    public Authority(ROLE role) {
        this.authority = role.name();
    }

    public enum ROLE {
        USER,
        ADMIN,
        SUPER_ADMIN;

        private static final String ROLE_AUTHORITY_PREFIX = "ROLE_";

        public String value() {
            return ROLE_AUTHORITY_PREFIX + this.name();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority1 = (Authority) o;
        return Objects.equals(authority, authority1.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }

}
