package io.github.md5sha256.blockstoragetest;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        final OptionsBuilder builder = new OptionsBuilder();
        final Options options = builder
                // Output everything in Milliseconds | Single Shot as we are testing the time for one invocation
                .timeUnit(TimeUnit.MILLISECONDS)
                .mode(Mode.SingleShotTime)
                // We want 10 forks
                // The reason for so many forks is that I cannot increase the measurement iterations too much
                // As then the test becomes unfair as the GC has more memory pressure due to the larger BlockStorage
                // sizes as the number of blocks increase
                // We add more forks to increase the number of samples whilst also resetting the memory environemtn
                .forks(10)
                // We have to warm up the jvm. 100 iterations to warm up the jvm
                .warmupIterations(100)
                // 1000 trials, this number could be decreased to 500
                // NOTE that this benchmark will break if the measurement batch size is changed
                // this is due to the BlockStorage and TickerTask state being inconsistent between batch runs
                .measurementIterations(1000)
                // Output results in CSV format
                .result("results.csv")
                .resultFormat(ResultFormatType.CSV)
                // Ensure that we use G1GC. Due to the large numbers of BlockStorages instances being created there will be
                // some degree of memory pressure. We set Xms to 4G as that's in the SF FAQ
                // we enable AlwaysPreTouch similar to Aikar's flags to somewhat simulate a server environment
                .jvmArgs("-XX:+UseG1GC", "-Xms4G", "-XX:+AlwaysPreTouch")
                // My local machine has 16 cores and 24 threads, I set it to 15 just to speed up the test
                // however you can use as many or little as desired.
                .threads(15)
                // We sync iterations as we're using multiple threads
                .syncIterations(true)
                .build();
        // Run the test!
        try {
            new Runner(options).run();
        } catch (final RunnerException ex) {
            ex.printStackTrace();
        }
    }

}
