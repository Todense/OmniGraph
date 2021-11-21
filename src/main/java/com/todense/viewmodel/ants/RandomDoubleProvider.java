package com.todense.viewmodel.ants;

import org.apache.commons.rng.UniformRandomProvider;

import java.util.Random;

public class RandomDoubleProvider implements UniformRandomProvider {

    final Random random = new Random();

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public void nextBytes(byte[] bytes) {

    }

    @Override
    public void nextBytes(byte[] bytes, int i, int i1) {

    }

    @Override
    public int nextInt() {
        return 0;
    }

    @Override
    public int nextInt(int i) {
        return 0;
    }

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public long nextLong(long l) {
        return 0;
    }

    @Override
    public boolean nextBoolean() {
        return false;
    }

    @Override
    public float nextFloat() {
        return 0;
    }
}
