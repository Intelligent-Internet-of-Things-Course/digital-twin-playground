package it.unimore.dipi.iot.digitaltwin.mqtt;

import it.unimore.dipi.iot.wldt.engine.WldtConfiguration;
import it.unimore.dipi.iot.wldt.engine.WldtEngine;
import it.unimore.dipi.iot.wldt.exception.WldtConfigurationException;
import it.unimore.dipi.iot.wldt.processing.ProcessingPipeline;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttConfiguration;
import it.unimore.dipi.iot.wldt.worker.mqtt.Mqtt2MqttWorker;
import it.unimore.dipi.iot.wldt.worker.mqtt.MqttTopicDescriptor;
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

    private static final String GPS_TOPIC_ID = "gps_topic";
    private static final String GPS_RESOURCE_ID = "gps";

    private static final String BATTERY_TOPIC_ID = "battery_topic";
    private static final String BATTERY_RESOURCE_ID = "battery";

    private static final String COMMAND_TOPIC_ID = "command_topic";
    private static final String COMMAND_RESOURCE_ID = "default_command_channel";

    private static final String SOURCE_BROKER_ADDRESS = "127.0.0.1";
    private static final int SOURCE_BROKER_PORT = 1883;

    private static final String DESTINATION_BROKER_ADDRESS = "127.0.0.1";
    private static final int DESTINATION_BROKER_PORT = 1884;

    private static final String DEVICE_ID = "vehicle001";

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

            Mqtt2MqttWorker mqtt2MqttWorker = new Mqtt2MqttWorker(wldtEngine.getWldtId(),
                    getMqttProtocolConfiguration());

            //Add Processing Pipeline for target topics
            mqtt2MqttWorker.addTopicProcessingPipeline(GPS_TOPIC_ID,
                    new ProcessingPipeline(new SenmlMqttProcessingStep(wldtEngine.getWldtId()))
            );

            mqtt2MqttWorker.addTopicProcessingPipeline(BATTERY_TOPIC_ID,
                    new ProcessingPipeline(new SenmlMqttProcessingStep(wldtEngine.getWldtId()))
            );

            wldtEngine.addNewWorker(mqtt2MqttWorker);
            wldtEngine.startWorkers();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Example configuration for the MQTT-to-MQTT WLDT Worker
     * @return
     */
    private static Mqtt2MqttConfiguration getMqttProtocolConfiguration(){

        //Configuration associated to the MQTT experimental use-case available in the dedicated project
        //Demo Telemetry topic -> telemetry/com:iot:dummy:dummyMqttDevice001/resource/dummy_string_resource

        Mqtt2MqttConfiguration mqtt2MqttConfiguration = new Mqtt2MqttConfiguration();

        mqtt2MqttConfiguration.setDtPublishingQoS(0);
        mqtt2MqttConfiguration.setBrokerAddress(SOURCE_BROKER_ADDRESS);
        mqtt2MqttConfiguration.setBrokerPort(SOURCE_BROKER_PORT);
        mqtt2MqttConfiguration.setDestinationBrokerAddress(DESTINATION_BROKER_ADDRESS);
        mqtt2MqttConfiguration.setDestinationBrokerPort(DESTINATION_BROKER_PORT);
        mqtt2MqttConfiguration.setDeviceId(DEVICE_ID);

        //If Required Specify the ClientId
        mqtt2MqttConfiguration.setBrokerClientId("dt-physicalBrokerTestClientId");
        mqtt2MqttConfiguration.setDestinationBrokerClientId("dt-digitalBrokerTestClientId");

        //Specify Topic List Configuration
        mqtt2MqttConfiguration.setTopicList(
                Arrays.asList(
                        new MqttTopicDescriptor(GPS_TOPIC_ID,
                                GPS_RESOURCE_ID,
                                "fleet/vehicle/{{device_id}}/telemetry/{{resource_id}}",
                                MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_OUTGOING),
                        new MqttTopicDescriptor(BATTERY_TOPIC_ID,
                                BATTERY_RESOURCE_ID,
                                "fleet/vehicle/{{device_id}}/telemetry/{{resource_id}}",
                                MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_OUTGOING),
                        new MqttTopicDescriptor(COMMAND_TOPIC_ID,
                                COMMAND_RESOURCE_ID,
                                "fleet/vehicle/{{device_id}}/command",
                                MqttTopicDescriptor.MQTT_TOPIC_TYPE_DEVICE_INCOMING)
                )
        );

        return mqtt2MqttConfiguration;
    }

}
