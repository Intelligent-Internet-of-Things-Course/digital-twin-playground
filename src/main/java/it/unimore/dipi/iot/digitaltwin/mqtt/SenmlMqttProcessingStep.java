package it.unimore.dipi.iot.digitaltwin.mqtt;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.smartobject.message.TelemetryMessage;
import it.unimore.dipi.iot.smartobject.model.GpsLocationDescriptor;
import it.unimore.dipi.iot.utils.SenMLPack;
import it.unimore.dipi.iot.utils.SenMLRecord;
import it.unimore.dipi.iot.wldt.metrics.WldtMetricsManager;
import it.unimore.dipi.iot.wldt.processing.PipelineData;
import it.unimore.dipi.iot.wldt.processing.ProcessingStep;
import it.unimore.dipi.iot.wldt.processing.ProcessingStepListener;
import it.unimore.dipi.iot.wldt.processing.cache.PipelineCache;
import it.unimore.dipi.iot.wldt.worker.mqtt.MqttPipelineData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project edt-sdn-experiments
 * @created 10/11/2020 - 13:26
 */
public class SenmlMqttProcessingStep implements ProcessingStep {

    private static final Logger logger = LoggerFactory.getLogger(SenmlMqttProcessingStep.class);

    private String deviceId;

    private ObjectMapper objectMapper;

    private static final String SENML_DATA_TYPE = "string_resource";

    private static final String DEMO_APP_NAME = "assembly";

    private static final long DELAYABLE_VALUE = 100;

    private static final int DROPPABLE_VALUE = 0;

    private static final String METRIC_BASE_IDENTIFIER = "mqtt_pp_senml";

    private static final String PROCESSING_PIPELINE_EXECUTION_TIME_METRICS_FIELD = "execution_time";

    public SenmlMqttProcessingStep(String deviceId) {

        this.deviceId = deviceId;

        //Jackson Object Mapper + Ignore Null Fields in order to properly generate the SenML Payload
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    @Override
    public void execute(PipelineCache pipelineCache, PipelineData pipelineData, ProcessingStepListener processingStepListener) {

        Timer.Context metricsContext = WldtMetricsManager.getInstance().getTimer(String.format("%s.%s", METRIC_BASE_IDENTIFIER, this.deviceId), PROCESSING_PIPELINE_EXECUTION_TIME_METRICS_FIELD);

        try{

            if(pipelineData instanceof MqttPipelineData){

                MqttPipelineData mqttPipelineData = (MqttPipelineData)pipelineData;

                logger.debug("Executing SenmlMqttProcessingStep Step with data: {}", new String(mqttPipelineData.getPayload()));

                //Update payload with Senml
                Optional<String> newPayloadOptional = Optional.empty();

                //Handle GPS and Battery Telemetry Messages and generate the associated SenML Payload
                if(mqttPipelineData.getTopic().contains("gps")){
                    Optional<TelemetryMessage<GpsLocationDescriptor>> optionalTelemetryMessage = parseGpsTelemetryMessage(mqttPipelineData.getPayload());
                    if(optionalTelemetryMessage.isPresent())
                        newPayloadOptional = buildGpsSenmlPayload(optionalTelemetryMessage.get());
                } else if(mqttPipelineData.getTopic().contains("battery")){
                    Optional<TelemetryMessage<Double>> optionalTelemetryMessage = parseBatteryTelemetryMessage(mqttPipelineData.getPayload());
                    if(optionalTelemetryMessage.isPresent())
                        newPayloadOptional = buildBatterySenmlPayload(optionalTelemetryMessage.get());
                }

                if(newPayloadOptional.isPresent()){
                    mqttPipelineData.setPayload(newPayloadOptional.get().getBytes());
                    processingStepListener.onStepDone(this, Optional.of(mqttPipelineData));
                }
                else{
                    String errorMessage = "PipelineData Error ! Error creating the updated SenML Payload !";
                    logger.error(errorMessage);
                    processingStepListener.onStepError(this, pipelineData, errorMessage);
                }
            }
            else {

                if(processingStepListener != null) {
                    String errorMessage = "PipelineData Error !";
                    logger.error(errorMessage);
                    processingStepListener.onStepError(this, pipelineData, errorMessage);
                }
                else
                    logger.error("Processing Step Listener = Null ! Skipping processing step");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(metricsContext != null)
                metricsContext.stop();
        }
    }

    private Optional<String> buildBatterySenmlPayload(TelemetryMessage<Double> telemetryMessage) {

        try {

            long originalTimestamp = telemetryMessage.getTimestamp();

            SenMLRecord senmlRecord = new SenMLRecord();
            senmlRecord.setT(originalTimestamp);
            senmlRecord.setN(telemetryMessage.getType());
            senmlRecord.setV(telemetryMessage.getDataValue());
            senmlRecord.setU("%EL"); //IANA Percentage (remaining battery energy level)

            SenMLPack senMLPack = new SenMLPack(){{ add(senmlRecord);}};

            return Optional.of(this.objectMapper.writeValueAsString(senMLPack));

        }catch (Exception e){
            logger.error("Error serializing Senml Packet ! Msg: {}", e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    private Optional<String> buildGpsSenmlPayload(TelemetryMessage<GpsLocationDescriptor> telemetryMessage) {

        try {

            long originalTimestamp = telemetryMessage.getTimestamp();

            SenMLRecord latitudeSenmlRecord = new SenMLRecord();
            latitudeSenmlRecord.setT(originalTimestamp);
            latitudeSenmlRecord.setN(String.format("%s:%s", telemetryMessage.getType(), "latitude"));
            latitudeSenmlRecord.setV(telemetryMessage.getDataValue().getLatitude());
            latitudeSenmlRecord.setU("lat"); //IANA Latitude Identifier

            SenMLRecord longitudeSenmlRecord = new SenMLRecord();
            longitudeSenmlRecord.setT(originalTimestamp);
            longitudeSenmlRecord.setN(String.format("%s:%s", telemetryMessage.getType(), "longitude"));
            longitudeSenmlRecord.setV(telemetryMessage.getDataValue().getLongitude());
            longitudeSenmlRecord.setU("lon"); //IANA Latitude Identifier

            SenMLRecord altitudeSenmlRecord = new SenMLRecord();
            altitudeSenmlRecord.setT(originalTimestamp);
            altitudeSenmlRecord.setN(String.format("%s:%s", telemetryMessage.getType(), "elevation"));
            altitudeSenmlRecord.setV(telemetryMessage.getDataValue().getElevation());
            altitudeSenmlRecord.setU("elv"); //Custom id since IANA does not provide currently a standard identifier

            SenMLPack senMLPack = new SenMLPack(){
                {
                    add(latitudeSenmlRecord);
                    add(longitudeSenmlRecord);
                    add(altitudeSenmlRecord);
                }
            };

            return Optional.of(this.objectMapper.writeValueAsString(senMLPack));

        }catch (Exception e){
            logger.error("Error serializing Senml Packet ! Msg: {}", e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    private Optional<TelemetryMessage<Double>> parseBatteryTelemetryMessage(byte[] payload){

        try{

            if(payload == null || payload.length == 0)
                return Optional.empty();

            return Optional.ofNullable(this.objectMapper.readValue(new String(payload), new TypeReference<TelemetryMessage<Double>>() {}));

        }catch (Exception e){
            return Optional.empty();
        }
    }

    private Optional<TelemetryMessage<GpsLocationDescriptor>> parseGpsTelemetryMessage(byte[] payload){

        try{

            if(payload == null || payload.length == 0)
                return Optional.empty();

            return Optional.ofNullable(this.objectMapper.readValue(new String(payload), new TypeReference<TelemetryMessage<GpsLocationDescriptor>>() {}));

        }catch (Exception e){
            return Optional.empty();
        }
    }

}
