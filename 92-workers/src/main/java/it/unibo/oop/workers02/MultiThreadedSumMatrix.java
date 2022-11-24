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
        private final double[] matrix1d;
        private final long startpos;
        private final long nelem;
        private double res;

        /**
         * 
         * @param matrix1d
         * @param startpos
         * @param nelem
         */
        Worker(final double[] matrix1d, final long startpos, final long nelem) {
            super();
            this.matrix1d = matrix1d.clone();
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos // NOPMD - suppressed as it is an exercise
                    + " to position " + (startpos + nelem - 1));
            this.res = DoubleStream.of(matrix1d)
                    .skip(startpos)
                    .limit(nelem)
                    .sum();
        }

        /**
         * 
         * @return the result of the sum
         */
        public double getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(final double[][] matrix) {
        final var matrix1d = Stream.of(matrix)
                .filter(arr -> arr.length > 0)
                .flatMapToDouble(arr -> DoubleStream.of(arr))
                .toArray();
        final long size = matrix1d.length % this.nThreads + matrix1d.length / this.nThreads;
        return LongStream
                .iterate(0, start -> start + size)
                .limit(nThreads)
                .mapToObj(start -> new Worker(matrix1d, start, size))
                .peek(Thread::start)
                .peek(MultiThreadedSumMatrix::joinThread)
                .mapToDouble(Worker::getResult)
                .sum();
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
