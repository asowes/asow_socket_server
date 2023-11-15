package com.young.asow.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class BaseEntity implements Serializable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "primary-id")
    @GenericGenerator(name = "primary-id", strategy = "com.young.asow.util.PrimaryIDGenerator")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column
    LocalDateTime createTime;

    @Column
    LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
