package com.blog.blog.service;

import com.blog.blog.entity.FriendLink;
import com.blog.blog.repository.FriendLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendLinkService {

    private final FriendLinkRepository friendLinkRepository;

    public List<FriendLink> findAll() {
        return friendLinkRepository.findAll();
    }

    public List<FriendLink> findVisible() {
        return friendLinkRepository.findByIsVisibleTrueOrderBySortOrderAsc();
    }

    public FriendLink findById(Long id) {
        return friendLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("友链不存在"));
    }

    public FriendLink create(FriendLink friendLink) {
        return friendLinkRepository.save(friendLink);
    }

    public FriendLink update(Long id, FriendLink friendLink) {
        FriendLink existing = findById(id);
        existing.setName(friendLink.getName());
        existing.setUrl(friendLink.getUrl());
        existing.setLogo(friendLink.getLogo());
        existing.setDescription(friendLink.getDescription());
        existing.setSortOrder(friendLink.getSortOrder());
        existing.setIsVisible(friendLink.getIsVisible());
        return friendLinkRepository.save(existing);
    }

    public void delete(Long id) {
        friendLinkRepository.delete(findById(id));
    }
}
