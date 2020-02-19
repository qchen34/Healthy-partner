package com.example.photosharing.models;

import android.graphics.Bitmap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

public class Image implements Serializable {
    private String id;
    private String userId;
    private String thumbnail;
    private Bitmap thumbnailCache;
    private String photo;
    private String description;
    private DateTime timestamp = DateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void fromTimeStamp(long timestamp) {
        this.timestamp = new DateTime(timestamp, DateTimeZone.UTC).toDateTime(DateTimeZone.getDefault());
    }

    public long toTimeStamp() {
        return timestamp.toDateTime(DateTimeZone.UTC).getMillis();
    }

    public Bitmap getThumbnailCache() {
        return thumbnailCache;
    }

    public void setThumbnailCache(Bitmap thumbnailCache) {
        this.thumbnailCache = thumbnailCache;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image copyImage() {
        Image image = new Image();
        image.setId(getId());
        image.setDescription(getDescription());
        image.setTimestamp(getTimestamp());
        image.setUserId(getUserId());
        image.setPhoto(getPhoto());
        image.setThumbnail(getThumbnail());
        return image;
    }
}
