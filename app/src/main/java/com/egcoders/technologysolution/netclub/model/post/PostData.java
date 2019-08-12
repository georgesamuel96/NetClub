package com.egcoders.technologysolution.netclub.model.post;

import com.egcoders.technologysolution.netclub.model.post.Post;

import java.util.List;

public class PostData {

    private int current_page;
    private List<Post> data;
    private String first_page_url;
    private int from;
    private String next_page_url;
    private String path;
    private String per_page;
    private String prev_page_url;
    private int to;

    public int getCurrent_page() {
        return current_page;
    }

    public List<Post> getData() {
        return data;
    }

    public String getFirst_page_url() {
        return first_page_url;
    }

    public int getFrom() {
        return from;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public String getPath() {
        return path;
    }

    public String getPer_page() {
        return per_page;
    }

    public String getPrev_page_url() {
        return prev_page_url;
    }

    public int getTo() {
        return to;
    }

    public void setData(List<Post> data) {
        this.data = data;
    }
}
