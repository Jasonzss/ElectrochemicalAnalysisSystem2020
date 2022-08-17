package com.bluedot.mapper.bean;

import java.util.List;

public class PageInfo<T> {
    private Integer pageSize;
    private Integer currentPageNo;
    private Long totalDataSize;
    private Long totalPageSize;
    private List<T> dataList;

    public PageInfo() {
    }

    public PageInfo(Integer pageSize, Integer currentPageNo, Long totalDataSize, Long totalPageSize, List dataList) {
        this.pageSize = pageSize;
        this.currentPageNo = currentPageNo;
        this.totalDataSize = totalDataSize;
        this.totalPageSize = totalPageSize;
        this.dataList = dataList;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(Integer currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public Long getTotalDataSize() {
        return totalDataSize;
    }

    public void setTotalDataSize(Long totalDataSize) {
        this.totalDataSize = totalDataSize;
    }

    public Long getTotalPageSize() {
        return totalPageSize;
    }

    public void setTotalPageSize(Long totalPageSize) {
        this.totalPageSize = totalPageSize;
    }

    public List getDataList() {
        return dataList;
    }

    public void setDataList(List dataList) {
        this.dataList = dataList;
    }
}
