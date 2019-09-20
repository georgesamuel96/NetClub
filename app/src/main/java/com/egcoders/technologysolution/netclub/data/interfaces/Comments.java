package com.egcoders.technologysolution.netclub.data.interfaces;

import com.egcoders.technologysolution.netclub.model.post.Comment;

import java.util.List;

public interface Comments {

    interface View{
        void showComments(List<Comment> list);
        void showComment(Comment comment);
        void showError(String message);
    }

    interface Presenter{
        void getComments(String postId);
        void addComment(String comment, String postId);
    }
}
