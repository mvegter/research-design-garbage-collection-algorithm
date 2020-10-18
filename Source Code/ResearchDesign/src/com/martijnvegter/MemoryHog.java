package com.martijnvegter;

import java.util.ArrayList;
import java.util.List;

public class MemoryHog implements Runnable {
	private static final int NUM_LONGS = 500_000;
	private static final int NUM_LOOPS = 10_000;

	@Override
	public void run() {
		for (int j = 0; j < NUM_LOOPS; j++) {
			List<Long> longs = new ArrayList<>(NUM_LONGS);
			for (int i = 0; i < NUM_LONGS; i++) {
				longs.add(new Long(i));
			}
		}
	}

}
