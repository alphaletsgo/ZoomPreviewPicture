package com.imgpre.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ola on 2017/7/27.
 */

public class PageBean implements Parcelable {
    public String subContent;
    public int commentCount;
    public ThumbViewInfo thumbImage;

    public PageBean(String subContent, int commentCount, ThumbViewInfo thumbImage) {
        this.subContent = subContent;
        this.commentCount = commentCount;
        this.thumbImage = thumbImage;
    }

    protected PageBean(Parcel in) {
        subContent = in.readString();
        commentCount = in.readInt();
        thumbImage = in.readParcelable(ThumbViewInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subContent);
        dest.writeInt(commentCount);
        dest.writeParcelable(thumbImage, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PageBean> CREATOR = new Creator<PageBean>() {
        @Override
        public PageBean createFromParcel(Parcel in) {
            return new PageBean(in);
        }

        @Override
        public PageBean[] newArray(int size) {
            return new PageBean[size];
        }
    };
}
