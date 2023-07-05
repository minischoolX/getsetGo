package com.example.app;

public interface ValueStore {

    String getValue(String key);

    void setValue(String key, String value);
}
