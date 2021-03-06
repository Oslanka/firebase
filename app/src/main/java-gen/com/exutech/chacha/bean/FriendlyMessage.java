package com.exutech.chacha.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FRIENDLY_MESSAGE".
 */
public class FriendlyMessage {

    private Long id;
    private String uid;
    private String text;
    private String name;
    private String photoUrl;
    /** Not-null value. */
    private String email;

    public FriendlyMessage() {
    }

    public FriendlyMessage(Long id) {
        this.id = id;
    }

    public FriendlyMessage(Long id, String uid, String text, String name, String photoUrl, String email) {
        this.id = id;
        this.uid = uid;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /** Not-null value. */
    public String getEmail() {
        return email;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEmail(String email) {
        this.email = email;
    }

}
