package com.blog.blog.entity;

import com.blog.blog.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "blog_user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "avatar")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.ADMIN;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
