package com.kingphung.voucher.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Voucher implements Parcelable {

    private String code;
    private String title;
    private String description;
    private String link;
    private String img_url;

    public Voucher() {
    }

    public Voucher(String code, String title, String description, String link, String img_url) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.link = link;
        this.img_url = img_url;
    }


    protected Voucher(Parcel in) {
        code = in.readString();
        title = in.readString();
        description = in.readString();
        link = in.readString();
        img_url = in.readString();
    }

    public static final Creator<Voucher> CREATOR = new Creator<Voucher>() {
        @Override
        public Voucher createFromParcel(Parcel in) {
            return new Voucher(in);
        }

        @Override
        public Voucher[] newArray(int size) {
            return new Voucher[size];
        }
    };

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

    public String getimg_url() {
        return img_url;
    }

    public void setimg_url(String img_url) {
        this.img_url = img_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(code);
        dest.writeString(description);
        dest.writeString(link);
        dest.writeString(img_url);
    }
}
