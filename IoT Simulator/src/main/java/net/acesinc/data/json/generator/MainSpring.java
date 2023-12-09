package net.acesinc.data.json.generator;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

@SpringBootApplication
public class MainSpring {
	public static void main(String[] args)
			throws JsonGenerationException, JsonMappingException, IOException, MqttException {
		SpringApplication.run(MainSpring.class, args);

	}

}
