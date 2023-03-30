package io.github.md5sha256.blockstoragetest;

import org.bukkit.Location;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockBlockStorage {

    private static final Object VALUE = new Object();

    private final Map<Location, Object> storage;

    public MockBlockStorage(int count) {
        this.storage = new ConcurrentHashMap<>(count);
    }

    public Map<Location, Object> getRawStorage() {
        return Collections.unmodifiableMap(this.storage);
    }


    public void addLocation(Location location) {
        this.storage.put(location, VALUE);
    }

    public void clear() {
        this.storage.clear();;
    }

}
