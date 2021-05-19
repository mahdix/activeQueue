package com.mahdix.activeQueue.model;

import java.util.HashMap;
import java.util.Map;

//TODO: define dedup thread/queue with custom uid generator fn
@SuppressWarnings("unused")
public class Properties extends HashMap<String, Object> {
    protected Properties() {
    }

    public static Properties of() {
        return new Properties();
    }

    public static Properties singleton(String key, Object value) {
        Properties result = new Properties();
        result.put(key, value);
        return result;
    }

    public static Properties ofMap(Map<String, Object> baseMap) {
        Properties result = new Properties();
        result.putAll(baseMap);

        return result;
    }
}
