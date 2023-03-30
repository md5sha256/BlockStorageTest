# BlockStorageTest

This repo contains a benchmark for new api methods for 
the Slimefun `TickerTask` and `BlockStorage`. The PR can be found [here](https://github.com/Slimefun/Slimefun4/pull/3798).

This benchmark uses JMH 1.36 and the Paper-API for 1.19.4. <br>
This benchmark requires Java 17 to be compiled and ran.

## Compiling
`./gradlew build`<br>
Jars can be found in `build/libs`<br>
The uber/fat jar is `BlockStorageTest-VERSION-all.jar`

## Running the benchmark
It is suggested that the only running process is the benchmark itself. However, the benchmark 
can be ran both as a java executable and from within your preferred IDE.
<br>
To run the benchmark from CLI: `java -jar BlockStorageTest-VERSIOn-all.jar`. <br>

VM args including memory constraints
for the test environment are hard-coded in `io.github.md5sha256.blockstoragetest.Main`

The output of the test is logged to `System.out`. However, the results of the test 
are outputted to `results.csv` and are in CSV format.

