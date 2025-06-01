package com.streamline.frontend.terminal.page;

import java.util.HashMap;
import java.util.Map;

public class PageState {

    public static final String SCROLL_POSITION = "scrollPosition";
    public static final String SELECTED_ITEM = "selectedItem";
    public static final String FILTER_TEXT = "filterText";
    public static final String PAGE_NUMBER = "pageNumber";

    private final Map<String, Object> properties;
    private final long timestamp;

    public PageState() {
        this.properties = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public <T> void set(String key, T value) {
        properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = properties.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }

    public <T> T get(String key, Class<T> type, T defaultValue) {
        T value = get(key, type);
        return value != null ? value : defaultValue;
    }

    public boolean has(String key) {
        return properties.containsKey(key);
    }
}
