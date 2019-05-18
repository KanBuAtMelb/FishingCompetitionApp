package com.example.fishingtest.Model;

public class Comment {
    public String commentId;
    public String postId;
    public String userId;
    public String content;
    public String timeStamp;

    public Comment() {

    }

    public Comment(String commentId, String postId, String userId, String content, String timeStamp) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.timeStamp = timeStamp;
    }
}
