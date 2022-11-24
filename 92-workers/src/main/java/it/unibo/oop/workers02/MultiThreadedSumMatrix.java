package it.unibo.oop.workers02;

import java.util.stream.DoubleStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Performs a multithread sum of a matrix.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThreads;

    /**
     * 
     * @param nThreads number of threads to use
     */
    public MultiThreadedSumMatrix(final int nThreads) {
        this.nThreads = nThreads;
    }

    private static class Worker extends Thread {
        private final double[] array;
        private final long startpos;
        private final long nelem;
        private double res;

        /**
         * 
         * @param array
         * @param startpos
         * @param nelem
         */
        Worker(final double[] array, final long startpos, final long nelem) {
            super();
            this.array = array.clone();
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos // NOPMD - suppressed as it is an exercise
                    + " to position " + (startpos + nelem - 1));
            this.res = DoubleStream.of(array)
                    .skip(startpos)
                    .limit(nelem)
                    .sum();
        }

        /**
         * 
         * @return
         */
        public double getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(final double[][] matrix) {
        final var array = Stream.of(matrix)
                .filter(arr -> arr.length > 0)
                .flatMapToDouble(arr -> DoubleStream.of(arr))
                .toArray();
        final long size = array.length % this.nThreads + array.length / this.nThreads;
        return LongStream
                .iterate(0, start -> start + size)
                .limit(nThreads)
                .mapToObj(start -> new Worker(array, start, size))
                .peek(Thread::start)
                .peek(MultiThreadedSumMatrix::joinThread)
                .mapToDouble(Worker::getResult)
                .sum();
        // System.out.println("Result: " + res);
        return res;
    }

    private static void joinThread(final Thread thread) {
        var joined = false;
        while (!joined) {
            try {
                thread.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace(); // NOPMD - suppressed as it is an exercise
            }
        }
    }
}
