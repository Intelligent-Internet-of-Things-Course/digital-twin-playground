package it.unimore.dipi.iot.smartobject.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-demo-smarthome
 * @created 11/11/2020 - 15:07
 */
public class SwitchRawActuator extends SmartObjectResource<Boolean> {

    private static Logger logger = LoggerFactory.getLogger(SwitchRawActuator.class);

    private static final String LOG_DISPLAY_NAME = "SwitchActuator";

    private static final String RESOURCE_TYPE = "iot.actuator.switch";

    private Boolean isActive;

    public SwitchRawActuator() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.isActive = true;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
        notifyUpdate(isActive);
    }

    @Override
    public Boolean loadUpdatedValue() {
        return this.isActive;
    }

    public static void main(String[] args) {

        SwitchRawActuator rawResource = new SwitchRawActuator();
        logger.info("New {} Resource Created with Id: {} ! {} New Value: {}",
                rawResource.getType(),
                rawResource.getId(),
                LOG_DISPLAY_NAME,
                rawResource.loadUpdatedValue());

        new Thread(new Runnable() {
            @Override
            public void run() {
               try{
                   for(int i=0; i<100; i++){
                       rawResource.setActive(!rawResource.loadUpdatedValue());
                       Thread.sleep(1000);
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
            }
        }).start();

        rawResource.addDataListener(new ResourceDataListener<Boolean>() {
            @Override
            public void onDataChanged(SmartObjectResource<Boolean> resource, Boolean updatedValue) {

                if(resource != null && updatedValue != null)
                    logger.info("Device: {} -> New Value Received: {}", resource.getId(), updatedValue);
                else
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
            }
        });

    }

}
