package com.example.fishingtest.Model;
/**
 *
 * Project: Fishing Competition
 * Author: Ziqi Zhang
 * Date: 8/06/2019
 * The class is object of Comment
 *
 */
public class Comment {
    public String commentId;
    public String compId;
    public String postId;
    public String userId;
    public String content;
    public String timeStamp;

    public Comment() {

    }

    // constructing comment
    public Comment(String commentId, String compId, String postId, String userId, String content, String timeStamp) {
        this.commentId = commentId;
        this.compId = compId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.timeStamp = timeStamp;
    }
}
