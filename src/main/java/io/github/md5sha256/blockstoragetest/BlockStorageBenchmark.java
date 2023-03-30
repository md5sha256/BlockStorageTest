package io.github.md5sha256.blockstoragetest;

import org.bukkit.Location;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Supplier;

public class BlockStorageBenchmark {

    private static int[] populateBlockStorage(MockBlockStorage blockStorage, int count) {
        Random random = new Random();
        int randomX = random.nextInt(-1000, 1000);
        int randomZ = random.nextInt(-1000, 1000);
        int blockX = randomX << 4;
        int blockZ = randomZ << 4;
        // Populate blockstorage
        for (int i = 0; i < count; i++) {
            int x = blockX + random.nextInt(0, 16);
            int z = blockZ + random.nextInt(0, 16);
            int y = random.nextInt(-64, 320);
            Location location = new Location(null, x, y, z);
            blockStorage.addLocation(location);
        }
        return new int[]{randomX, randomZ};
    }

    public static boolean clear(TestState testState) {
        int chunkX = testState.chunkX;
        int chunkZ = testState.chunkZ;
        MockTickerTask tickerTask = testState.tickerTask;
        Map<Location, Object> storage = testState.blockStorage.getRawStorage();
        for (Location location : storage.keySet()) {
            if (location.getBlockX() >> 4 == chunkX && location.getBlockZ() >> 4 == chunkZ) {
                tickerTask.queueDelete(location, true);
            }
        }
        return true;
    }

    public static boolean bulkClearCollection(TestState testState, Supplier<Collection<Location>> collectionSupplier) {
        int chunkX = testState.chunkX;
        int chunkZ = testState.chunkZ;
        Collection<Location> toClear = collectionSupplier.get();
        Map<Location, Object> storage = testState.blockStorage.getRawStorage();
        for (Location location : storage.keySet()) {
            if (location.getBlockX() >> 4 == chunkX && location.getBlockZ() >> 4 == chunkZ) {
                toClear.add(location);
            }
        }
        testState.tickerTask.queueDelete(toClear, true);
        return true;
    }

    public static boolean bulkClearMap(TestState testState, Supplier<Map<Location, Boolean>> mapSupplier) {
        int chunkX = testState.chunkX;
        int chunkZ = testState.chunkZ;
        Map<Location, Boolean> toClear = mapSupplier.get();
        Map<Location, Object> storage = testState.blockStorage.getRawStorage();
        for (Location location : storage.keySet()) {
            if (location.getBlockX() >> 4 == chunkX && location.getBlockZ() >> 4 == chunkZ) {
                toClear.put(location, true);
            }
        }
        testState.tickerTask.queueDelete(toClear);
        return true;
    }

    private static int compareLocation(Location loc1, Location loc2) {
        return Integer.compare(loc1.getBlockY(), loc2.getBlockY());
    }

    @Benchmark
    public void bulkClear(TestState testState, Blackhole blackhole) {
        blackhole.consume(clear(testState));
    }

    @Benchmark
    public void bulkClearArrayList(TestState testState, Blackhole blackhole) {
        blackhole.consume(bulkClearCollection(testState, ArrayList::new));
    }

    @Benchmark
    public void bulkClearLinkedList(TestState testState, Blackhole blackhole) {
        blackhole.consume(bulkClearCollection(testState, LinkedList::new));
    }

    @Benchmark
    public void bulkClearHashMap(TestState testState, Blackhole blackhole) {
        blackhole.consume(bulkClearMap(testState, HashMap::new));
    }

    @Benchmark
    public void bulkClearTreeMap(TestState testState, Blackhole blackhole) {
        blackhole.consume(bulkClearMap(testState, () -> new TreeMap<>(BlockStorageBenchmark::compareLocation)));
    }

    @State(Scope.Benchmark)
    public static class TestState {

        public MockTickerTask tickerTask;
        public MockBlockStorage blockStorage;
        public int chunkX;
        public int chunkZ;
        @Param({"10", "100", "1000", "10000"})
        private int numberBlocks;

        @Setup(Level.Iteration)
        public void setup() {
            this.tickerTask = new MockTickerTask();
            this.blockStorage = new MockBlockStorage(numberBlocks);
            int[] chunk = populateBlockStorage(this.blockStorage, numberBlocks);
            this.chunkX = chunk[0];
            this.chunkZ = chunk[1];
        }


        @TearDown(Level.Iteration)
        public void teardown() {
            this.tickerTask.clear();
            this.tickerTask = null;
            this.blockStorage.clear();
            this.blockStorage = null;
        }

    }

}
