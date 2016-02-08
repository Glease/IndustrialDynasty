package net.glease.industrialcore.test;

public class SomeRandomClass implements SomeRandomGenericInterface<String> {

	@Override
	public <T> String foo(T... args) {
		Object[] b = new Object[8];
		System.arraycopy(args, 1, b, 0, args.length-1);
		
		return null;
	}

	public static void main(String[] args) {
		
	}
}
