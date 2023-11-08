package com.young.asow.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "primary-id")
//    @GenericGenerator(name = "primary-id", strategy = "com.young.asow.util.PrimaryIDGenerator")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column
    @CreatedDate
    LocalDateTime createTime;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
