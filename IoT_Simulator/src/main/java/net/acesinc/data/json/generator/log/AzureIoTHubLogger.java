/**
 * 
 */
package net.acesinc.data.json.generator.log;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;

/**
 * Un produttore di hub IoT di Azure invia eventi JSON all'hub IoT di Azure specificato nel file config. 
 * Scegli un protocollo tra HTTPS, AMQPS o MQTT
	{
    "type": "iothub",
    "connectionString": "<- Get from Azure portal or Device Explorer ->",
    "protocol": "HTTPS",
	}
 * @author jurgenma
 *
 */
public final class AzureIoTHubLogger extends AbstractEventLogger {

	private final DeviceClient deviceClient;
	
	/**
	 * @param props
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public AzureIoTHubLogger(Queue<String> queue,final Map<String, Object> props) throws URISyntaxException, IOException {
		
		super(queue);
		
		this.deviceClient = new DeviceClient((String)props.get("connectionString"), 
				IotHubClientProtocol.valueOf((String)props.get("protocol")));
		this.deviceClient.open();
	}
	
	/**
	 * @param deviceClient
	 */
	public AzureIoTHubLogger(Queue<String> queue,final DeviceClient deviceClient) {
		super(queue);
		this.deviceClient = deviceClient;
	}

	/* (non-Javadoc)
	 * @see net.acesinc.data.json.generator.log.EventLogger#logEvent(java.lang.String, java.util.Map)
	 */
	@Override
	public void logEvent(String event, Map<String, Object> producerConfig) {
		
		this.deviceClient.sendEventAsync(new Message(event), null, null);
		addEventTrace(String.format("%s %s: published %s",  LocalDateTime.now(),"azureIot", deviceClient ));
	}

	/* (non-Javadoc)
	 * @see net.acesinc.data.json.generator.log.EventLogger#shutdown()
	 */
	@Override
	public void shutdown() {
		
		if (this.deviceClient != null) {
			try {
				this.deviceClient.closeNow();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
