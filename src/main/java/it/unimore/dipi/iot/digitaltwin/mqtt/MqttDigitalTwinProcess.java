package it.unimore.dipi.iot.digitaltwin.mqtt;

import it.unimore.dipi.iot.wldt.engine.WldtConfiguration;
import it.unimore.dipi.iot.wldt.engine.WldtEngine;
import it.unimore.dipi.iot.wldt.exception.WldtConfigurationException;
import it.unimore.dipi.iot.wldt.processing.ProcessingPipeline;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttConfiguration;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 *
 * Mvn Command: mvn exec:java -Dexec.mainClass="it.unimore.dipi.edt.experiment.digitaltwin.MqttDigitalTwin"
 *
 * @project edt-sdn-experiments
 * @created 09/11/2020 - 17:49
 */
public class MqttDigitalTwinProcess {

    private static final String TAG = "[WLDT-MQTT-Process]";

    private static final Logger logger = LoggerFactory.getLogger(MqttDigitalTwinProcess.class);

    private static final String DT_DESTINATION_MQTT_ADDRESS = "127.0.0.1";

    private static final int DT_DESTINATION_MQTT_PORT = 1884;

    private static final String DT_SOURCE_MQTT_ADDRESS = "127.0.0.1";

    private static final int DT_SOURCE_MQTT_PORT = 1883;


    public static void main(String[] args)  {

        try{

            logger.info("{} Initializing WLDT-Engine ... ", TAG);

            //Manual creation of the WldtConfiguration
            WldtConfiguration wldtConfiguration = new WldtConfiguration();
            wldtConfiguration.setDeviceNameSpace("it.unimore.dipi.things");
            wldtConfiguration.setWldtBaseIdentifier("wldt");
            wldtConfiguration.setWldtStartupTimeSeconds(10);
            wldtConfiguration.setApplicationMetricsEnabled(false);
            wldtConfiguration.setApplicationMetricsReportingPeriodSeconds(10);
            wldtConfiguration.setMetricsReporterList(Collections.singletonList("csv"));

            WldtEngine wldtEngine = new WldtEngine(wldtConfiguration);

            Mqtt2MqttWorker mqtt2MqttWorker = new Mqtt2MqttWorker(wldtEngine.getWldtId(), getMqttExampleConfiguration());

            //Setup Processing Pipeline
            mqtt2MqttWorker.addProcessingPipeline(Mqtt2MqttWorker.DEFAULT_RESOURCE_TELEMETRY_PROCESSING_PIPELINE,
                    new ProcessingPipeline(new SenmlMqttProcessingStep(wldtEngine.getWldtId())));

            wldtEngine.addNewWorker(mqtt2MqttWorker);
            wldtEngine.startWorkers();

        }catch (Exception | WldtConfigurationException e){
            e.printStackTrace();
        }
    }

    /**
     * Example configuration for the MQTT-to-MQTT WLDT Worker
     * @return
     */
    private static Mqtt2MqttConfiguration getMqttExampleConfiguration(){

        Mqtt2MqttConfiguration mqtt2MqttConfiguration = new Mqtt2MqttConfiguration();

        mqtt2MqttConfiguration.setOutgoingClientQoS(0);
        mqtt2MqttConfiguration.setDestinationBrokerAddress(DT_DESTINATION_MQTT_ADDRESS);
        mqtt2MqttConfiguration.setDestinationBrokerPort(DT_DESTINATION_MQTT_PORT);
        mqtt2MqttConfiguration.setDestinationBrokerBaseTopic("wldt");
        mqtt2MqttConfiguration.setDeviceId("vehicle001");
        mqtt2MqttConfiguration.setResourceIdList(Arrays.asList("gps", "battery"));
        mqtt2MqttConfiguration.setDeviceTelemetryTopic("fleet/vehicle/{{device_id}}/telemetry");
        mqtt2MqttConfiguration.setResourceTelemetryTopic("fleet/vehicle/{{device_id}}/telemetry/{{resource_id}}");
        mqtt2MqttConfiguration.setEventTopic("fleet/vehicle/{{device_id}}/event");
        mqtt2MqttConfiguration.setBrokerAddress(DT_SOURCE_MQTT_ADDRESS);
        mqtt2MqttConfiguration.setBrokerPort(DT_SOURCE_MQTT_PORT);

        return mqtt2MqttConfiguration;
    }

}
