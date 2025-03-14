package com.mojang.minecraft;

import net.lax1dude.eaglercraft.EagRuntime;

public class Timer {
	public Timer(float tps) {
		this.tps = tps;
		lastSysClock = EagRuntime.steadyTimeMillis();
		lastHRClock = EagRuntime.nanoTime() / 1000000L;
	}

	float tps;
	double lastHR;
	public int elapsedTicks;
	public float delta;
	public float speed = 1.0F;
	public float elapsedDelta = 0.0F;
	long lastSysClock;
	long lastHRClock;
	double adjustment = 1.0D;
}
