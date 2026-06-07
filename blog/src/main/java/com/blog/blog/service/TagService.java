package com.blog.blog.service;

import com.blog.blog.entity.Tag;
import com.blog.blog.repository.ArticleRepository;
import com.blog.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;

    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Tag updateTag(Long id, Tag tag) {
        Tag existing = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        existing.setName(tag.getName());
        existing.setSlug(tag.getSlug());
        return tagRepository.save(existing);
    }

    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        if (articleRepository.existsByTagsContains(tag)) {
            throw new RuntimeException("该标签被文章引用，无法删除");
        }
        tagRepository.delete(tag);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
    }
}