package com.bluedot.mapper.bean;

import java.util.List;

public class PageInfo {
    private Integer pageSize;
    private Integer currentPageNo;
    private Long totalDataSize;
    private Long totalPageSize;
    private List<Object> dataList;

    public PageInfo() {
    }

    public PageInfo(Integer pageSize, Integer currentPageNo, Long totalDataSize, Long totalPageSize, List<Object> dataList) {
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

    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageSize=" + pageSize +
                ", currentPageNo=" + currentPageNo +
                ", totalDataSize=" + totalDataSize +
                ", totalPageSize=" + totalPageSize +
                ", dataList=" + dataList +
                '}';
    }
}
