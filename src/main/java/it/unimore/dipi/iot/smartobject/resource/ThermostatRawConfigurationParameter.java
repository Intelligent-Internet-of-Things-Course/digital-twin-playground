package it.unimore.dipi.iot.smartobject.resource;

import it.unimore.dipi.iot.smartobject.model.ThermostatConfigurationModel;

import java.util.UUID;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-demo-smarthome
 * @created 11/11/2020 - 16:32
 */
public class ThermostatRawConfigurationParameter extends SmartObjectResource<ThermostatConfigurationModel>{

    private ThermostatConfigurationModel thermostatConfigurationModel;

    private static final String RESOURCE_TYPE = "iot.config.thermostat";

    public ThermostatRawConfigurationParameter(ThermostatConfigurationModel thermostatConfigurationModel) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.thermostatConfigurationModel = thermostatConfigurationModel;
    }

    @Override
    public ThermostatConfigurationModel loadUpdatedValue() {
        return this.thermostatConfigurationModel;
    }

    public ThermostatConfigurationModel getThermostatConfigurationModel() {
        return thermostatConfigurationModel;
    }

    public void setThermostatConfigurationModel(ThermostatConfigurationModel thermostatConfigurationModel) {
        this.thermostatConfigurationModel = thermostatConfigurationModel;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ThermostatRawConfigurationParameter{");
        sb.append("thermostatConfigurationModel=").append(thermostatConfigurationModel);
        sb.append('}');
        return sb.toString();
    }
}
