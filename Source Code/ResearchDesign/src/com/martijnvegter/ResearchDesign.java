package com.martijnvegter;

import java.util.ArrayList;
import java.util.List;

class ResearchDesign {
	private static final int NUM_THREADS = 1;

	public static void main(String[] arg) {
		try {
			Thread.sleep(5000);
			
			List<Thread> threads = new ArrayList<Thread>(NUM_THREADS);

			for (int i = 0; i < NUM_THREADS; i++) {
				threads.add(new Thread(new MemoryHog()));
			}

			for (Thread thread : threads) {
				thread.start();
			}

			for (Thread thread : threads) {
				thread.join();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
