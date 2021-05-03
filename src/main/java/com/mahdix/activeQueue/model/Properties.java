package com.mahdix.activeQueue.model;

import java.util.HashMap;

public class Properties extends HashMap<String, Object> {
    public static Properties singleton(String key, Object value) {
        Properties result = new Properties();
        result.put(key, value);
        return result;
    }
}
