package it.unimore.dipi.iot.smartobject.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-demo-smarthome
 * @created 11/11/2020 - 14:43
 */
public class EnergyRawSensor extends SmartObjectResource<Double> {

    private static Logger logger = LoggerFactory.getLogger(EnergyRawSensor.class);

    //kWh - kilowatt-hour
    private static final double MIN_ENERGY_VALUE = 0.5;

    //kWh - kilowatt-hour
    private static final double MAX_ENERGY_VALUE = 1.0;

    //kWh - kilowatt-hour
    private static final double MIN_ENERGY_VARIATION = 0.1;

    //kWh - kilowatt-hour
    private static final double MAX_ENERGY_VARIATION = 0.5;

    private static final String LOG_DISPLAY_NAME = "EnergySensor";

    //Ms associated to data update
    private static final long UPDATE_PERIOD = 5000;

    private static final long TASK_DELAY_TIME = 5000;

    private static final String RESOURCE_TYPE = "iot.sensor.energy";

    private Double updatedValue;

    private Random random;

    private Timer updateTimer = null;

    private boolean isActive = true;

    public EnergyRawSensor() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        init();
    }

    private void init(){

        try{

            this.random = new Random(System.currentTimeMillis());
            this.updatedValue = MIN_ENERGY_VALUE + this.random.nextDouble()*(MAX_ENERGY_VALUE - MIN_ENERGY_VALUE);

            startPeriodicEventValueUpdateTask();

        }catch (Exception e){
            logger.error("Error initializing the IoT Resource ! Msg: {}", e.getLocalizedMessage());
        }

    }

    private void startPeriodicEventValueUpdateTask(){

        try{

            logger.info("Starting periodic Update Task with Period: {} ms", UPDATE_PERIOD);

            this.updateTimer = new Timer();
            this.updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    if(isActive){
                        double variation = (MIN_ENERGY_VARIATION + MAX_ENERGY_VARIATION *random.nextDouble()) * (random.nextDouble() > 0.5 ? 1.0 : -1.0);
                        updatedValue = updatedValue + variation;
                    }
                    else
                        updatedValue = 0.0;

                    notifyUpdate(updatedValue);

                }
            }, TASK_DELAY_TIME, UPDATE_PERIOD);

        }catch (Exception e){
            logger.error("Error executing periodic resource value ! Msg: {}", e.getLocalizedMessage());
        }

    }

    @Override
    public Double loadUpdatedValue() {
        return this.updatedValue;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public static void main(String[] args) {

        EnergyRawSensor rawResource = new EnergyRawSensor();
        rawResource.setActive(false);
        logger.info("New {} Resource Created with Id: {} ! {} New Value: {}",
                rawResource.getType(),
                rawResource.getId(),
                LOG_DISPLAY_NAME,
                rawResource.loadUpdatedValue());

        rawResource.addDataListener(new ResourceDataListener<Double>() {
            @Override
            public void onDataChanged(SmartObjectResource<Double> resource, Double updatedValue) {

                if(resource != null && updatedValue != null)
                    logger.info("Device: {} -> New Value Received: {}", resource.getId(), updatedValue);
                else
                    logger.error("onDataChanged Callback -> Null Resource or Updated Value !");
            }
        });

    }

}
