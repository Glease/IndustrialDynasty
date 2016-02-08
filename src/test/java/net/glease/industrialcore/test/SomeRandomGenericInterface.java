package net.glease.industrialcore.test;

public interface SomeRandomGenericInterface<T> {
	<E> T foo(E... args);
}
