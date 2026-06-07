package com.blog.blog.repository;

import com.blog.blog.entity.Article;
import com.blog.blog.entity.Category;
import com.blog.blog.entity.Tag;
import com.blog.blog.enums.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 根据状态查询，按创建时间倒序（分页）
    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    // 根据分类的 slug 查询已发布文章（分页），按发布时间倒序（或创建时间）
    Page<Article> findByCategory_SlugAndStatusOrderByPublishedAtDesc(String slug, ArticleStatus status, Pageable pageable);

    // 根据标签的 slug 查询已发布文章（分页），按发布时间倒序
    // 注意：需要传递单个 slug，JPQL 会通过关联表查询所有包含该标签的文章
    Page<Article> findByTags_SlugAndStatusOrderByPublishedAtDesc(String slug, ArticleStatus status, Pageable pageable);

    // 根据状态和发布时间区间查询（用于归档或统计）
    List<Article> findByStatusAndPublishedAtBetween(ArticleStatus status, LocalDateTime start, LocalDateTime end);

    // 统计指定状态的文章数量
    long countByStatus(ArticleStatus status);

    // 查询最新 5 篇已发布文章（用于侧边栏）
    List<Article> findTop5ByStatusOrderByCreatedAtDesc(ArticleStatus status);

    // 查询置顶文章（用于首页）
    List<Article> findByIsTopTrueAndStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    // 全文检索（使用 native query 配合 MySQL 全文索引）
    @Query(value = "SELECT * FROM article WHERE MATCH(title, content) AGAINST (:keyword IN NATURAL LANGUAGE MODE) AND status = :status",
            nativeQuery = true)
    Page<Article> searchByKeyword(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);

    // 检查分类下是否有文章
    boolean existsByCategory(Category category);

    // 检查标签是否被文章引用
    boolean existsByTagsContains(Tag tag);
}
