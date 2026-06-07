package com.blog.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;


import java.util.List;

/**
 * 分页响应vo,用于封装分页查询结构
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    /*当前页记录列表*/
    private List<T> records;

    /*总记录数*/
    private long total;

    /*当前页码（从1开始）*/
    private int page;

    /*每页大小*/
    private int pageSize;

    /*总页数*/
    private int totalPages;

    public PageVO(Page<T> page) {
        this.records = page.getContent();
        this.total = page.getTotalElements();
        this.page = page.getNumber() + 1; // Page对象的页码从0开始转为从1开始
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
    }
}
