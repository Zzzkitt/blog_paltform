package com.blog.blog.service;

import com.blog.blog.dto.ArticleCreateRequest;
import com.blog.blog.dto.ArticleUpdateRequest;
import com.blog.blog.entity.Article;
import com.blog.blog.entity.Category;
import com.blog.blog.entity.Tag;
import com.blog.blog.enums.ArticleStatus;
import com.blog.blog.repository.ArticleRepository;
import com.blog.blog.repository.CategoryRepository;
import com.blog.blog.repository.TagRepository;
import com.blog.blog.vo.ArchiveEntry;
import com.blog.blog.vo.ArticleVO;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final Parser markdownParser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    //创建文章
    public ArticleVO createArticle(ArticleCreateRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setRawContent(request.getContent());
        article.setContent(convertMarkdownToHtml(request.getContent()));
        article.setSummary(request.getSummary());
        article.setCoverImage(request.getCoverImage());
        article.setIsTop(request.getIsTop());
        article.setViewCount(0);

        //处理状态
        ArticleStatus status = ArticleStatus.valueOf(request.getStatus().toUpperCase());
        article.setStatus(status);
        if (status == ArticleStatus.PUBLISHED) {
            article.setPublishedAt(LocalDateTime.now());
        }

        //处理summary
        if (request.getSummary() != null && !request.getSummary().isEmpty()) {
            article.setSummary(request.getSummary());
        } else {
            //自动截取纯文本前150字
            String plainText = stripHtmlTags(article.getContent());
            article.setSummary(plainText.length() > 150 ? plainText.substring(0, 150) : plainText);
        }

        //设置分类
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        }

        //设置标签
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            article.setTags(tags);
        }

        Article saved = articleRepository.save(article);
        return convertToVO(saved);
    }

    //更新文章
    public ArticleVO updateArticle(Long id, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        article.setTitle(request.getTitle());
        article.setRawContent(request.getContent());
        article.setContent(convertMarkdownToHtml(request.getContent()));
        article.setCoverImage(request.getCoverImage());
        if (request.getIsTop() != null) article.setIsTop(request.getIsTop());

        //状态变更处理
        ArticleStatus newStatus = ArticleStatus.valueOf(request.getStatus().toUpperCase());
        if(newStatus != article.getStatus() && newStatus == ArticleStatus.PUBLISHED) {
            article.setPublishedAt(LocalDateTime.now());
        }
        article.setStatus(newStatus);

        // summary
        if (request.getSummary() != null && !request.getSummary().isEmpty()) {
            article.setSummary(request.getSummary());
        } else {
            String plainText = stripHtmlTags(article.getContent());
            article.setSummary(plainText.length() > 150 ? plainText.substring(0, 150) : plainText);
        }

        // 分类
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        } else {
            article.setCategory(null);
        }

        // 标签
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            article.setTags(tags);
        }

        Article saved = articleRepository.save(article);
        return convertToVO(saved);
    }

    // 删除文章
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        articleRepository.delete(article);
    }

    // 切换状态（发布/草稿）
    public ArticleVO toggleStatus(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        if (article.getStatus() == ArticleStatus.DRAFT) {
            article.setStatus(ArticleStatus.PUBLISHED);
            article.setPublishedAt(LocalDateTime.now());
        } else {
            article.setStatus(ArticleStatus.DRAFT);
            article.setPublishedAt(null);
        }
        return convertToVO(articleRepository.save(article));
    }

    // 分页查询（管理端全部文章，无需过滤状态）
    public Page<ArticleVO> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable).map(this::convertToVO);
    }

    // 根据ID查询详情（管理端）
    public ArticleVO findById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        return convertToVO(article);
    }

    // 转换实体到 VO
    private ArticleVO convertToVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());       // HTML
        vo.setRawContent(article.getRawContent()); // Markdown
        vo.setCoverImage(article.getCoverImage());
        vo.setStatus(article.getStatus());
        vo.setIsTop(article.getIsTop());
        vo.setViewCount(article.getViewCount());
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());
        vo.setPublishedAt(article.getPublishedAt());

        if (article.getCategory() != null) {
            vo.setCategoryId(article.getCategory().getId());
            vo.setCategoryName(article.getCategory().getName());
        }
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            vo.setTagIds(article.getTags().stream().map(Tag::getId).collect(Collectors.toSet()));
            vo.setTagNames(article.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        }
        return vo;
    }

    // Markdown 转 HTML
    private String convertMarkdownToHtml(String markdown) {
        Node document = markdownParser.parse(markdown);
        return htmlRenderer.render(document);
    }

    // 简单去除 HTML 标签（用于生成摘要）
    private String stripHtmlTags(String html) {
        return html.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim();
    }

    // ===== 公开查询方法 =====

    // 分页查询已发布文章
    @Transactional(readOnly = true)
    public Page<ArticleVO> findPublished(Pageable pageable) {
        return articleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.PUBLISHED, pageable)
                .map(this::convertToVO);
    }

    // 查询已发布文章详情（阅读数+1）
    @Transactional
    public ArticleVO findPublishedById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new RuntimeException("文章不存在");
        }
        article.setViewCount(article.getViewCount() + 1);
        Article saved = articleRepository.save(article);
        return convertToVO(saved);
    }

    // 查询置顶文章
    @Transactional(readOnly = true)
    public List<ArticleVO> findTopArticles() {
        return articleRepository.findByIsTopTrueAndStatusOrderByCreatedAtDesc(
                ArticleStatus.PUBLISHED, Pageable.unpaged())
                .stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 获取文章归档（按年月分组）
    @Transactional(readOnly = true)
    public List<ArchiveEntry> findArchive() {
        List<Article> articles = articleRepository.findByStatusAndPublishedAtBetween(
                ArticleStatus.PUBLISHED,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now());
        return articles.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getPublishedAt().getYear() + "-" + String.format("%02d", a.getPublishedAt().getMonthValue()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> new ArchiveEntry(entry.getKey(), entry.getValue().stream()
                        .map(this::convertToVO).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    // 按分类 slug 查询已发布文章
    @Transactional(readOnly = true)
    public Page<ArticleVO> findByCategorySlug(String slug, Pageable pageable) {
        return articleRepository.findByCategory_SlugAndStatusOrderByPublishedAtDesc(slug, ArticleStatus.PUBLISHED, pageable)
                .map(this::convertToVO);
    }

    // 按标签 slug 查询已发布文章
    @Transactional(readOnly = true)
    public Page<ArticleVO> findByTagSlug(String slug, Pageable pageable) {
        return articleRepository.findByTags_SlugAndStatusOrderByPublishedAtDesc(slug, ArticleStatus.PUBLISHED, pageable)
                .map(this::convertToVO);
    }

    // 全文检索
    @Transactional(readOnly = true)
    public Page<ArticleVO> searchByKeyword(String keyword, Pageable pageable) {
        return articleRepository.searchByKeyword(keyword, "PUBLISHED", pageable)
                .map(this::convertToVO);
    }
}
