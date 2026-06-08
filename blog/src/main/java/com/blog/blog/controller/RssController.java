package com.blog.blog.controller;

import com.blog.blog.entity.Article;
import com.blog.blog.enums.ArticleStatus;
import com.blog.blog.repository.ArticleRepository;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RssController {

    private final ArticleRepository articleRepository;

    @GetMapping(value = "/api/rss", produces = "application/rss+xml; charset=utf-8")
    public String rss() {
        List<Article> articles = articleRepository.findByStatusOrderByCreatedAtDesc(
                ArticleStatus.PUBLISHED, PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "publishedAt")))
                .getContent();

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle("My Blog");
        feed.setLink("http://localhost:8080");
        feed.setDescription("个人博客");

        List<SyndEntry> entries = articles.stream().map(article -> {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(article.getTitle());
            entry.setLink("http://localhost:8080/articles/" + article.getId());
            entry.setPublishedDate(java.util.Date.from(article.getPublishedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()));

            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(article.getSummary() != null ? article.getSummary() : "");
            entry.setDescription(description);
            return entry;
        }).collect(Collectors.toList());

        feed.setEntries(entries);

        try {
            return new SyndFeedOutput().outputString(feed);
        } catch (Exception e) {
            throw new RuntimeException("RSS 生成失败", e);
        }
    }
}
