package com.kingphung.voucher.model;

public class Voucher {

    private String code;
    private String title;
    private String description;
    private String link;
    private String link_img;

    public Voucher() {
    }

    public Voucher(String code, String title, String description, String link, String link_img) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.link = link;
        this.link_img = link_img;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink_img() {
        return link_img;
    }

    public void setLink_img(String link_img) {
        this.link_img = link_img;
    }

}
