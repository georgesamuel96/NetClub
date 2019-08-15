package com.egcoders.technologysolution.netclub.model.post;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SavePostData {
    @SerializedName("current_page")
    private Integer currentPage;
    @SerializedName("data")
    private List<SavePostDetail> data = null;
    @SerializedName("first_page_url")
    private String firstPageUrl;
    @SerializedName("from")
    private Integer from;
    @SerializedName("next_page_url")
    private String nextPageUrl;
    @SerializedName("path")
    private String path;
    @SerializedName("per_page")
    private Integer perPage;
    @SerializedName("prev_page_url")
    private String prevPageUrl;
    @SerializedName("to")
    private Integer to;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<SavePostDetail> getData() {
        return data;
    }

    public void setData(List<SavePostDetail> data) {
        this.data = data;
    }

    public String getFirstPageUrl() {
        return firstPageUrl;
    }

    public void setFirstPageUrl(String firstPageUrl) {
        this.firstPageUrl = firstPageUrl;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Object getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Object getPrevPageUrl() {
        return prevPageUrl;
    }

    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }
}
