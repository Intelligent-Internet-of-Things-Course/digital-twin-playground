package it.unimore.dipi.iot.smartobject.process;

import it.unimore.dipi.iot.smartobject.resource.*;
import it.unimore.dipi.iot.smartobject.resource.coap.CoapEnergyConsumptionResource;
import it.unimore.dipi.iot.smartobject.resource.coap.CoapSwitchActuatorResource;
import it.unimore.dipi.iot.smartobject.resource.coap.CoapTemperatureResource;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-demo-smarthome
 * @created 11/11/2020 - 15:39
 */
public class HvacCoapSmartObjectProcess extends CoapServer {

    private final static Logger logger = LoggerFactory.getLogger(HvacCoapSmartObjectProcess.class);

    public HvacCoapSmartObjectProcess() {

        super();
        String deviceId = String.format("dipi:iot:%s", UUID.randomUUID().toString());
        this.add(createCompressorResource(deviceId));
        this.add(createLivingRoomResource(deviceId));
    }

    private CoapResource createCompressorResource(String deviceId){

        CoapResource compressorRootResource = new CoapResource("compressor");

        //INIT Emulated Physical Sensors and Actuators
        TemperatureRawSensor compressorTemperatureRawSensor = new TemperatureRawSensor();
        EnergyRawSensor compressorEnergyRawSensor = new EnergyRawSensor();
        SwitchRawActuator compressorSwitchRawActuator = new SwitchRawActuator();

        //Compressor Resource
        CoapTemperatureResource compressorTemperatureResource = new CoapTemperatureResource(deviceId, "temperature", compressorTemperatureRawSensor);
        CoapEnergyConsumptionResource compressorEnergyResource = new CoapEnergyConsumptionResource(deviceId, "energy", compressorEnergyRawSensor);
        CoapSwitchActuatorResource compressorSwitchResource = new CoapSwitchActuatorResource(deviceId, "switch", compressorSwitchRawActuator);

        compressorRootResource.add(compressorTemperatureResource);
        compressorRootResource.add(compressorEnergyResource);
        compressorRootResource.add(compressorSwitchResource);

        //Handle Emulated Resource notification
        compressorSwitchRawActuator.addDataListener(new ResourceDataListener<Boolean>() {
            @Override
            public void onDataChanged(SmartObjectResource<Boolean> resource, Boolean updatedValue) {
                logger.info("[COMPRESSOR-BEHAVIOUR] -> Updated Switch Value: {}", updatedValue);
                logger.info("[COMPRESSOR-BEHAVIOUR] -> Updating energy sensor configuration ...");
                compressorEnergyRawSensor.setActive(updatedValue);
            }
        });

        return compressorRootResource;
    }

    private CoapResource createLivingRoomResource(String deviceId){

        CoapResource livingRoomRootResource = new CoapResource("living-room");

        EnergyRawSensor livingRoomEnergyRawSensor = new EnergyRawSensor();
        SwitchRawActuator livingRoomSwitchRawActuator = new SwitchRawActuator();

        CoapEnergyConsumptionResource livingEnergyResource = new CoapEnergyConsumptionResource(deviceId, "energy", livingRoomEnergyRawSensor);
        CoapSwitchActuatorResource livingSwitchResource = new CoapSwitchActuatorResource(deviceId, "switch", livingRoomSwitchRawActuator);

        livingRoomRootResource.add(livingEnergyResource);
        livingRoomRootResource.add(livingSwitchResource);

        return livingRoomRootResource;
    }

    public static void main(String[] args) {

        HvacCoapSmartObjectProcess hvacCoapSmartObjectProcess = new HvacCoapSmartObjectProcess();
        hvacCoapSmartObjectProcess.start();

        logger.info("Coap Server Started ! Available resources: ");

        hvacCoapSmartObjectProcess.getRoot().getChildren().stream().forEach(resource -> {
            logger.info("Resource {} -> URI: {} (Observable: {})", resource.getName(), resource.getURI(), resource.isObservable());
            if(!resource.getURI().equals("/.well-known")){
                resource.getChildren().stream().forEach(childResource -> {
                    logger.info("\t Resource {} -> URI: {} (Observable: {})", childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });

    }

}
