/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.acesinc.data.json.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pulsar.client.api.PulsarClientException;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 *
 * @author andrewserff
 */
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import net.acesinc.data.json.generator.config.JSONConfigReader;
import net.acesinc.data.json.generator.config.SimulationConfig;
import net.acesinc.data.json.generator.log.AzureIoTHubLogger;
import net.acesinc.data.json.generator.log.EventLogger;
import net.acesinc.data.json.generator.log.FileLogger;
import net.acesinc.data.json.generator.log.HttpPostLogger;
import net.acesinc.data.json.generator.log.KafkaLogger;
import net.acesinc.data.json.generator.log.KinesisLogger;
import net.acesinc.data.json.generator.log.Log4JLogger;
import net.acesinc.data.json.generator.log.MqttLogger;
import net.acesinc.data.json.generator.log.NatsLogger;
import net.acesinc.data.json.generator.log.PulsarLogger;
import net.acesinc.data.json.generator.log.TranquilityLogger;
import net.acesinc.data.json.generator.log.WampLogger;

@RestController
public class JsonDataGenerator {
	@Autowired
	Environment environment;

	private static final Logger log = LogManager.getLogger(JsonDataGenerator.class);

	private SimulationRunner simRunner;
	private String simConfigFile;

	public JsonDataGenerator() {
	}

	public String getFilePath(String file) {
		String filePath = getSimulationContentPath() + "/" + file;
		return filePath;
	}

	public String getSimulationContentPath() {
		String folder = null;
		if (environment != null)
			environment.getProperty("myApp.folder", "conf");
		else
			folder = System.getProperty("myApp.folder", "conf");
		return folder;
	}

	public JsonDataGenerator setUpSimulation(String simConfigString) {
		
		simConfigFile = simConfigString;
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
		try {
			log.debug("Creating Simulation Runner using Simulation Config [ " + simConfigString + " ]");
			SimulationConfig simConfig = getSimConfig();
			List<EventLogger> loggers = new ArrayList<>();
			for (Map<String, Object> elProps : simConfig.getProducers()) {
				String elType = (String) elProps.get("type");
				switch (elType) {
				case "logger": {
					log.info("Adding Log4JLogger Producer");
					loggers.add(new Log4JLogger());
					break;
				}
				case "file": {
					log.info("Adding File Logger with properties: " + elProps);
					loggers.add(new FileLogger(queue, elProps));
					break;
				}
				case "kafka": {
					log.info("Adding Kafka Producer with properties: " + elProps);
					loggers.add(new KafkaLogger(queue, elProps));
					break;
				}
				case "tranquility": {
					log.info("Adding Tranqulity Logger with properties: " + elProps);
					loggers.add(new TranquilityLogger(queue, elProps));
					break;
				}
				case "nats": {
					log.info("Adding NATS Logger with properties: " + elProps);
					loggers.add(new NatsLogger(queue, elProps));
					break;
				}
				case "http-post": {
					log.info("Adding HTTP Post Logger with properties: " + elProps);
					try {
						loggers.add(new HttpPostLogger(queue, elProps));
					} catch (NoSuchAlgorithmException ex) {
						log.error("http-post Logger unable to initialize", ex);
					}
					break;
				}
				case "mqtt": {
					log.info("Adding MQTT Logger with properties: " + elProps);
					try {
						loggers.add(new MqttLogger(queue, elProps));
					} catch (MqttException ex) {
						log.error("mqtt Logger unable to initialize", ex);
					}
					break;
				}
				case "iothub": {
					log.info("Adding Azure IoT Hub Logger with properties: " + elProps);
					try {
						loggers.add(new AzureIoTHubLogger(queue, elProps));
					} catch (URISyntaxException ex) {
						log.error("Azure IoT Hub Logger unable to initialize", ex);
					}
					break;
				}
				case "kinesis": {
					log.info("Adding Kinesis Logger with properties: " + elProps);
					try {
						loggers.add(new KinesisLogger(queue, elProps));
					} catch (Exception ex) {
						log.error("Kinesis Logger unable to initialize", ex);
					}
					break;
				}
				case "pulsar": {
					log.info("Adding Pulsar Logger with properties: " + elProps);
					try {
						loggers.add(new PulsarLogger(elProps));
					} catch (final PulsarClientException ex) {
						log.error("Pulsar Logger unable to initialize", ex);
					}
					break;
				}
				case "wamp": {
					log.info("Adding Wamp Logger with properties: " + elProps);
					try {
						loggers.add(new WampLogger(queue, elProps));
					} catch (final Exception ex) {
						log.error("Wamp Logger unable to initialize", ex);
					}
					break;
				}
				}
			}
			if (loggers.isEmpty()) {
				throw new IllegalArgumentException("You must configure at least one Producer in the Simulation Config");
			}
			simRunner = new SimulationRunner(simConfig, loggers, getSimulationContentPath(), queue);
		} catch (IOException ex) {
			log.error("Error getting Simulation Config [ " + simConfigString + " ]", ex);
		}
		return this;
	}

	private FireGreetings r = new FireGreetings(this);
	private Thread t;

	@MessageMapping("/output")
	@SendTo("/topic/output")
	public String validate(String message) {// @Payload
		if (t != null && t.isAlive()) {
			t.interrupt(); // interupt vedere
		}
		t = new Thread(r);
		t.start();
		return message;
	}

	@Autowired
	private SimpMessagingTemplate template;

	public void fireGreeting(String lastEvent) {
		System.out.println();
		this.template.convertAndSend("/topic/output", lastEvent);
	}

	public void startRunning() {
		simRunner.startSimulation();
	}

	public void stopRunning() {
		simRunner.stopSimulation();
	}

	private SimulationConfig getSimConfig() throws IOException {
		File f = new File(getFilePath(simConfigFile));
		return JSONConfigReader.readConfig(f, SimulationConfig.class);
	}

	public boolean isRunning() {
		return simRunner.isRunning();
	}

	@PostMapping("/create")
	public void create(@RequestBody FileModel prova) {
		try {
			Path path = Paths.get("C:/tmp");
			Files.createDirectories(path);
			System.out.println("Directory is created!");
		} 
		catch(FileAlreadyExistsException e) {
			System.out.println("Directory already exists!");
		}
		catch (IOException e) {
			  System.err.println("Failed to create directory!" + e.getMessage());
		}
		try {
			File myObj = new File(getFilePath(prova.getName()));
			FileWriter myWriter = new FileWriter(myObj);
			myWriter.write(prova.getData());
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	private static JsonDataGenerator gen;

	@PostMapping("/run")
	public void run(@RequestBody FileContenitor file) throws IOException, ParseException {

		String simConfig = file.getName();
		gen = setUpSimulation(simConfig);

		final Thread mainThread = Thread.currentThread();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Shutdown Hook Invoked.  Shutting Down Loggers");
				gen.stopRunning();
				try {
					mainThread.join();

				} catch (InterruptedException ex) {
					// oh well
				}
			}
		});
		gen.startRunning();

		while (gen.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				// wakie wakie!
			}
		}

	}

	@GetMapping("/read/{file}")
	public String Read(@PathVariable("file") String file) throws IOException {
		String folder = environment.getProperty("myApp.folder");
		String path = folder + "/" + file;
		FileInputStream inputStream = new FileInputStream(path);
		try {
			String everything = IOUtils.toString(inputStream);
			return everything;
		} finally {
			inputStream.close();
		}
	}

	@PostMapping("/stop")
	public void Stop() {
		if (gen != null && gen.isRunning()) {
			gen.stopRunning();
			t.interrupt();
		}

	}

	@DeleteMapping("/delete/{file}")
	public void DeleteFile(@PathVariable("file") String file) {
		File f = new File(getFilePath(file));
		if (f != null && f.exists()) {
			FileUtils.deleteQuietly(f);
		}
	}

	/**
	 * @return the simConfigFile
	 */
	public String getSimConfigFile() {
		return simConfigFile;
	}

	/**
	 * @param simConfigFile the simConfigFile to set
	 */
	public void setSimConfigFile(String simConfigFile) {
		this.simConfigFile = simConfigFile;
	}

	public SimulationRunner getSimRunner() {
		return simRunner;
	}

	public static void main(String[] args) {
		String simConfig = "defaultSimConfig.json";
		if (args.length > 0) {
			simConfig = args[0];
			log.info("Overriding Simulation Config file from command line to use [ " + simConfig + " ]");
		}

		final JsonDataGenerator gen = new JsonDataGenerator();
		gen.setUpSimulation(simConfig);


		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Shutdown Hook Invoked.  Shutting Down Loggers");
				gen.stopRunning();
				try {
					mainThread.join();
				} catch (InterruptedException ex) {
					//oh well
				}
			}
		});

		gen.startRunning();
		while (gen.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				//wakie wakie!
			}
		}
	}

}
