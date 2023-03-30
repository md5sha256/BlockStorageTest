package io.github.md5sha256.blockstoragetest;

import org.apache.commons.lang3.Validate;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockTickerTask {

    private final Map<Location, Boolean> deletionQueue = new ConcurrentHashMap<>();

    public void clear() {
        this.deletionQueue.clear();
    }
    public void queueDelete(Location l, boolean destroy) {
        Validate.notNull(l, "Location must not be null!");

        deletionQueue.put(l, destroy);
    }

    public void queueDelete(Collection<Location> locations, boolean destroy) {
        Validate.notNull(locations, "Locations must not be null");
        Map<Location, Boolean> toDelete = new HashMap<>(locations.size(), 1.0F);
        for (Location location : locations) {
            Validate.notNull(location, "Locations must not contain null locations");
            toDelete.put(location, destroy);
        }
        deletionQueue.putAll(toDelete);
    }

    public void queueDelete(Map<Location, Boolean> locations) {
        Validate.notNull(locations, "Locations must not be null");
        for (Map.Entry<Location, Boolean> entry : locations.entrySet()) {
            Validate.notNull(entry.getKey(), "Locations must not contain null keys");
            Validate.notNull(entry.getValue(), "Locations must not contain null values");
        }
        deletionQueue.putAll(locations);
    }

}
