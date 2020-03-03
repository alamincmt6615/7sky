package com.alamin.healthcare.chatapp.models;

public class Comment {

    private String cid;
    private String cmt_msg;
    private String cmt_date;
    private String cmt_time;
    private String commmentator_uid;
    private String post_uid;
    private String post_id;

    public Comment() {
    }

    public Comment(String cid, String cmt_msg, String cmt_date, String cmt_time, String commmentator_uid, String post_uid,String post_id) {
        this.cid = cid;
        this.cmt_msg = cmt_msg;
        this.cmt_date = cmt_date;
        this.cmt_time = cmt_time;
        this.commmentator_uid = commmentator_uid;
        this.post_uid = post_uid;
        this.post_id = post_id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCmt_msg() {
        return cmt_msg;
    }

    public void setCmt_msg(String cmt_msg) {
        this.cmt_msg = cmt_msg;
    }

    public String getCmt_date() {
        return cmt_date;
    }

    public void setCmt_date(String cmt_date) {
        this.cmt_date = cmt_date;
    }

    public String getCmt_time() {
        return cmt_time;
    }

    public void setCmt_time(String cmt_time) {
        this.cmt_time = cmt_time;
    }

    public String getCommmentator_uid() {
        return commmentator_uid;
    }

    public void setCommmentator_uid(String commmentator_uid) {
        this.commmentator_uid = commmentator_uid;
    }

    public String getPost_uid() {
        return post_uid;
    }

    public void setPost_uid(String post_uid) {
        this.post_uid = post_uid;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
