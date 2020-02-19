package com.example.photosharing.models;

/**
 * Types of the image
 */
public enum ImageType {
    PROFILE(120),
    THUMBNAIL(100),
    PHOTO(1024);

    private final int pixel;

    ImageType(int pixel) {
        this.pixel = pixel;
    }

    /**
     * @return the maximum length of the image
     */
    public int getMaxLength() {
        return pixel;
    }

    /**
     * @return the maximum size of the image
     */
    public long getMaxSize() {
        return pixel * pixel;
    }
}
