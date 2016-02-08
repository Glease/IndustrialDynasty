package net.glease.industrialcore.test;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import net.glease.industrialdynasty.util.CyclicCounter;

public class TestCyclicCounter {
	ThreadPoolExecutor pool;
	CyclicCounter counter;
	AtomicInteger i = new AtomicInteger();
	@Before
	public void setUp() throws Exception {
		pool = new ThreadPoolExecutor(50, 50, 0, TimeUnit.SECONDS, Queues.newLinkedBlockingDeque());
		pool.prestartAllCoreThreads();
		counter = new CyclicCounter(10);
	}
	
	@After
	public void tearDown() throws Exception {
		pool.shutdownNow();
	}
	
	@Test
	public void testCountDown() {
		List<TestRunnable> tasks = Lists.newArrayList();
		TestRunnable r = new TestRunnable(counter);
		for (int i = 0; i < 10000; i++) {
			tasks.add(r);
		}
		try {
			pool.invokeAll(tasks);
		} catch (InterruptedException e) {
			throw Throwables.propagate(e);
		}
	}
	
	public class TestRunnable implements Callable<Void>{
		private final CyclicCounter counter;

		public TestRunnable(CyclicCounter counter) {
			this.counter = counter;
		}

		@Override
		public Void call() throws InterruptedException {
			Thread.sleep(5);
			if(counter.countDown()){
				assertTrue(i.getAndIncrement()%10==0);
			}
			return null;
		}
	}
	
}
