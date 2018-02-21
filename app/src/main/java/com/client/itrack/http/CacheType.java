package com.client.itrack.http;

/**
 * Created by OWNER on 2015/7/11.
 */
public enum CacheType {
    NO_CACHE(1),
    INVALIDATE_CACHE(2),
    CACHE(3);
    private int value;

    private CacheType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
