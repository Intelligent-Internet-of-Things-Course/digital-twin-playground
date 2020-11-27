package it.unimore.dipi.iot.smartobject.process;

import it.unimore.dipi.iot.smartobject.model.ThermostatConfigurationModel;
import it.unimore.dipi.iot.smartobject.resource.*;
import it.unimore.dipi.iot.smartobject.resource.coap.CoapSwitchActuatorResource;
import it.unimore.dipi.iot.smartobject.resource.coap.CoapTemperatureResource;
import it.unimore.dipi.iot.smartobject.resource.coap.CoapThermostatConfigurationParameterResource;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-demo-smarthome
 * @created 11/11/2020 - 16:26
 */
public class ThermostatCoapSmartObjectProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(ThermostatCoapSmartObjectProcess.class);

    public ThermostatCoapSmartObjectProcess() {
        super();

        String deviceId = String.format("dipi:iot:%s", UUID.randomUUID().toString());

        //INIT Emulated Physical Sensors and Actuators
        TemperatureRawSensor temperatureRawSensor = new TemperatureRawSensor();
        SwitchRawActuator switchRawActuator = new SwitchRawActuator();
        ThermostatRawConfigurationParameter configurationRawParameter = new ThermostatRawConfigurationParameter(new ThermostatConfigurationModel());

        CoapTemperatureResource temperatureResource = new CoapTemperatureResource(deviceId, "temperature", temperatureRawSensor);
        CoapSwitchActuatorResource switchResource = new CoapSwitchActuatorResource(deviceId, "switch", switchRawActuator);
        CoapThermostatConfigurationParameterResource configurationResource = new CoapThermostatConfigurationParameterResource(deviceId, "configuration", configurationRawParameter);

        this.add(temperatureResource);
        this.add(switchResource);
        this.add(configurationResource);

        //Observe Internal Temperature
        temperatureRawSensor.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChanged(SmartObjectResource<Double> resource, Double updatedValue) {

                logger.info("[THERMOSTAT-BEHAVIOUR] -> Updated Temperature Value: {}", updatedValue);

                //TODO Update Check Method
                if(switchRawActuator.getActive() && isHvacCommunicationRequired(configurationRawParameter.loadUpdatedValue(), updatedValue))
                    logger.info("[THERMOSTAT-BEHAVIOUR] -> Sending PUT Request to HVAC Unit: {}", configurationRawParameter.loadUpdatedValue().getHvacUnitResourceUri());
            }
        });

    }

    private static boolean isHvacCommunicationRequired(ThermostatConfigurationModel thermostatConfigurationModel, double currentTemperatureValue){
        return true;
    }

    public static void main(String[] args) {

        ThermostatCoapSmartObjectProcess smartObjectProcess = new ThermostatCoapSmartObjectProcess();
        smartObjectProcess.start();

        logger.info("Coap Server Started ! Available resources: ");

        smartObjectProcess.getRoot().getChildren().stream().forEach(resource -> {
            logger.info("Resource {} -> URI: {} (Observable: {})", resource.getName(), resource.getURI(), resource.isObservable());
            if(!resource.getURI().equals("/.well-known")){
                resource.getChildren().stream().forEach(childResource -> {
                    logger.info("\t Resource {} -> URI: {} (Observable: {})", childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });

    }


}
