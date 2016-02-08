package net.glease.industrialcore.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.base.Throwables;

import net.glease.industrialdynasty.util.ASMMethodHandles;

/**
 * To simplify, I composed most features into 3 method, and 5 tests
 * @author glease
 *
 */
public class ASMMethodHandlesTest {
	
	@Test
	public void testInvokeNormal() {
		try {
			assertTrue("ddd".equals(ASMMethodHandles.INSTANCE.invoke(this.getClass().getMethod("s", new Class<?>[0]), this, new Object[0])));
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	@Test
	public void testInvokeStaticNonArg() {
		try {
			ASMMethodHandles.INSTANCE.invoke(this.getClass().getMethod("bbbb", new Class<?>[0]), null, new Object[0]);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	@Test
	public void testInvokePrimitiveVararg() {
		try {
			ASMMethodHandles.INSTANCE.invoke(this.getClass().getMethod("b", int[].class), this, new int[] {3,5,7});
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	@Test
	public void testInvokeVararg() {
		try {
			assertEquals(Boolean.TRUE, ASMMethodHandles.INSTANCE.invoke(this.getClass().getMethod("b", Object[].class), this, new Object[] {"aa","bb"}));
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	@Test
	public void testInvokePrimitiveVarargExpandedWithReturnBoxing() {
		try {
			assertEquals(6, ASMMethodHandles.INSTANCE.invoke(this.getClass().getMethod("mean", int[].class), this, new int[] {1,3,5,7,9,11}));
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	@Test(expected = ClassCastException.class)
	public void testInvokeVarargWithReturnBoxingWrongParam() {
		try {
			assertEquals(Boolean.TRUE, ASMMethodHandles.INSTANCE.invoke(this.getClass().getMethod("b", Object[].class), this, new int[] {3,5,7}));
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	public String s() {
		System.out.println("ASMMethodHandlesTest.s()");
		return "ddd";
	}
	
	public static void bbbb() {
		System.out.println("ASMMethodHandlesTest.bbbb()");
	}
	
	public void b(int...is) {
		System.out.println("ASMMethodHandlesTest.b(int...)");
		System.out.println(Arrays.toString(is));
	}
	
	public int mean(int...is) {
		System.out.println("ASMMethodHandlesTest.mean(int...)");
		int sum = 0;
		for (int i = 0; i < is.length; i++) {
			sum += is[i];
		}
		return sum / is.length;
	}
	
	public boolean b(Object...is) {
		System.out.println("ASMMethodHandlesTest.b(Object...)");
		System.out.println(Arrays.toString(is));
		return true;
	}
}
