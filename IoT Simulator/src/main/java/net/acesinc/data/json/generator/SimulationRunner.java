/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.acesinc.data.json.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.acesinc.data.json.generator.config.SimulationConfig;
import net.acesinc.data.json.generator.config.WorkflowConfig;
import net.acesinc.data.json.generator.config.JSONConfigReader;
import net.acesinc.data.json.generator.log.EventLogger;
import net.acesinc.data.json.generator.workflow.Workflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author andrewserff
 */
public class SimulationRunner {

	private static final Logger log = LogManager.getLogger(SimulationRunner.class);
	private SimulationConfig config;
	private List<EventGenerator> eventGenerators;
	private List<Thread> eventGenThreads;
	private boolean running;
	private List<EventLogger> eventLoggers;
	private String basePath;
	private LinkedBlockingQueue<String> queue;

	public SimulationRunner(SimulationConfig config, List<EventLogger> loggers, String basePath,
			LinkedBlockingQueue<String> queue) {
		this.config = config;
		this.eventLoggers = loggers;
		eventGenerators = new ArrayList<EventGenerator>();
		eventGenThreads = new ArrayList<Thread>();
		this.basePath = basePath;
		this.queue = queue;

		setupSimulation();
	}

	private void setupSimulation() {
		running = false;
		for (WorkflowConfig workflowConfig : config.getWorkflows()) {
			try {
				// Workflow w =
				// JSONConfigReader.readConfig(this.getClass().getClassLoader().getResourceAsStream(workflowConfig.getWorkflowFilename()),
				// Workflow.class);
				String wfConfigPath = this.basePath + "/" + workflowConfig.getWorkflowFilename();
				Workflow w = JSONConfigReader.readConfig(new File(wfConfigPath), Workflow.class);
				final EventGenerator gen = new EventGenerator(w, workflowConfig, eventLoggers);
				log.info("Adding EventGenerator for [ " + workflowConfig.getWorkflowName() + ","
						+ workflowConfig.getWorkflowFilename() + " ]");
				eventGenerators.add(gen);
				eventGenThreads.add(new Thread(gen));
			} catch (IOException ex) {
				log.error("Error reading config: " + workflowConfig.getWorkflowName(), ex);
			}
		}
	}

	public void startSimulation() {
		log.info("Starting Simulation");

		if (eventGenThreads.size() > 0) {
			for (Thread t : eventGenThreads) {
				t.start();
			}
			running = true;
		}
		queue.offer("Simulation started!");
	}

	public void stopSimulation() {
		log.info("Stopping Simulation");
		for (Thread t : eventGenThreads) {
			t.interrupt();
		}
		for (EventLogger l : eventLoggers) {
			l.shutdown();
		}
		queue.offer("Simulation stopped!");
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void traceEvent(String event) {
		queue.offer(event);
	}

	public LinkedBlockingQueue<String> getQueue() {
		return queue;
	}

}
