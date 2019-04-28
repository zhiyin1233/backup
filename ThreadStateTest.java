package smartbi.mining.util;

import spire.random.rng.Lcg32;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * @author HuangHuai
 * 2018-06-26 17:29
 */
public class ThreadStateTest {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		Object lock = new Object();
		Object lock2 = new Object();
		Object lock3 = new Object();

		Thread t1 = new Thread(() -> {
		}, "1.thread.new.新建");

		Thread t2 = new Thread(() -> {
			synchronized (lock2) {
				while (true) {

				}
			}
		}, "2.thread.running.运行中");
		t2.start();

		Thread t3 = new Thread(() -> {

			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}, "3.thread.wait.wait方法等待");
		t3.start();


		Thread t4 = new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (lock2) {

			}

		}, "4.thread.blocked.阻塞Synchronized同步锁");
		t4.start();


		Thread t5 = new Thread(() -> {

			synchronized (lock3) {
				try {
					lock3.wait(1000000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}, "5.thread.timed-wait.超时等待wait(long)");
		t5.start();




		Thread t6 = new Thread(() -> {

			LockSupport.parkNanos(100000000000L);
			System.out.println(111);

		}, "6.thread.timed-wait.超时等待parkNanos(long)");
		t6.start();

		Thread t7 = new Thread(() -> {

			LockSupport.park();
			System.out.println(111);

		}, "7.thread.park");
		t7.start();



		Thread.sleep(1000);

		println1(t1,t2,t3,t4,t5,t6);

	}

	private static void println1(Thread... t) {
		for (Thread thread : t) {
			System.out.println(thread+"-->" + thread.getState());
		}
	}


}

