package com.blog.blog.repository;

import com.blog.blog.entity.AboutInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutRepository extends JpaRepository<AboutInfo, Long> {
}
