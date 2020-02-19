package com.example.photosharing.models;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Comment {
    private String id;
    private String content;
    private String userId;
    private String imageId;
    private DateTime timestamp = DateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
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
}
