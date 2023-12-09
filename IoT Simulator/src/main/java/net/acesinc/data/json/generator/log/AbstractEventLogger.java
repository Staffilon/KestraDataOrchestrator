package net.acesinc.data.json.generator.log;

import java.util.Queue;

public abstract class AbstractEventLogger implements EventLogger {

	private Queue<String> queue;

	public AbstractEventLogger(Queue<String> queue) {
		this.queue = queue;
	}

	public Queue<String> getQueue() {
		return queue;
	}

	public void addEventTrace(String event) {
		System.out.println("Adding new event trace: " + event);
		queue.offer(event);
	};
}
