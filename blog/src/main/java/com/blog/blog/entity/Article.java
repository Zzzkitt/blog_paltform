package com.blog.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.blog.blog.enums.ArticleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(columnDefinition = "LONGTEXT")
    private String rawContent;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("articles")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "article_tag",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnoreProperties("articles")
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "is_top", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isTop = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getArticles().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getArticles().remove(this);
    }
}
