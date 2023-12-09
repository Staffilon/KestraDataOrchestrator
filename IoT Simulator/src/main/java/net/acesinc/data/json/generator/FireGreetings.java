package net.acesinc.data.json.generator;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FireGreetings implements Runnable {

	private JsonDataGenerator listener;

	public FireGreetings(JsonDataGenerator listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(2000);
				System.out.println("Check if simulation is running...");
				SimulationRunner simRunner = listener.getSimRunner();
				if (simRunner != null) {
					System.out.println("waiting for next event...");
					int count = 10;
					while (true) {
						count++;
						String lastEvent = simRunner.getQueue().poll(10, TimeUnit.SECONDS);
						if (lastEvent != null) {
							try {
								listener.fireGreeting(lastEvent);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (!listener.isRunning() || count > 10) {
							break;
						}
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
