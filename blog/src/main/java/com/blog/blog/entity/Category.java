package com.blog.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "category")
    @JsonIgnoreProperties("category")
    private List<Article> articles = new ArrayList<>();
}
