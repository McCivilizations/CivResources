package com.mccivilizations.resources;

@FunctionalInterface
public interface ThrowingFunction<T, U, E extends Exception> {
    U apply(T value) throws E;
}
