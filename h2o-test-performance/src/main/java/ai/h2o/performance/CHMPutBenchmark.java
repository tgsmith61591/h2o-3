/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
//java -cp h2o-3/build/h2o.jar:target/benchmarks.jar org.openjdk.jmh.Main -rf csv -rff perf.csv
package org.sample;

import org.openjdk.jmh.annotations.*;

import water.Key;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Fork(5)
@BenchmarkMode(Mode.AverageTime)
@Measurement(iterations=20, timeUnit=TimeUnit.MILLISECONDS, time=100)
@Warmup(iterations=10, timeUnit=TimeUnit.MILLISECONDS, time=10)
@OutputTimeUnit(value= TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class CHMPutBenchmark {

    ConcurrentHashMap<String,Integer> chm = new ConcurrentHashMap<String,Integer>(524288);

    // prior to each jmh iteration, clear out the hash map
    @Setup(Level.Iteration)
    public void clearCHM() { chm.clear(); }

    // after each jmh iteration, make sure no resize operations took place because we don't want to measure these
    @TearDown(Level.Iteration)
    public void checkCHM() throws InterruptedException {
        if (chm.size() > 524288*.75) {
            System.out.println("CHM probably resized. Invalid experiment.");
            throw new InterruptedException();
        }
    }

    // measure the amount of time it takes to do a put operation for various numbers of threads. note that we are also
    // measuring the amount of time it takes to conduct Key.rand() operation. i think this is okay because these puts
    // will be compared to h2o NBHM puts, and we do the same thing there. we could have avoided this by generating
    // random keys prior to each jmh invocation (Level.Invocation), but, per jmh docs, this is discouraged for very
    // short operations.
    @Benchmark
    @Threads(value=1)
    public void chmPutTest1() { chm.put(Key.rand(),0); }

    @Benchmark
    @Threads(value=2)
    public void chmPutTest2() { chm.put(Key.rand(),0); }

    @Benchmark
    @Threads(value=4)
    public void chmPutTest4() { chm.put(Key.rand(),0); }

    @Benchmark
    @Threads(value=8)
    public void chmPutTest8() { chm.put(Key.rand(),0); }

    @Benchmark
    @Threads(value=16)
    public void chmPutTest16() { chm.put(Key.rand(),0); }
}