package it.unimore.dipi.iot.smartobject.model;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-demo-smarthome
 * @created 11/11/2020 - 16:32
 */
public class ThermostatConfigurationModel {

    public static String HEATING_OPERATIONAL_MODE = "thermostat_heating_mode";

    public static String AIR_CONDITIONING_OPERATIONAL_MODE = "thermostat_airconditioning_mode";

    private double maxTemperature = 16.0;

    private double minTemperature = 10.0;

    //Example of the target HVAC internal unit associated to the thermostat room
    private String hvacUnitResourceUri = "coap://127.0.0.1:5683/living-room/switch";

    private String operationalMode = HEATING_OPERATIONAL_MODE;

    public ThermostatConfigurationModel() {
    }

    public ThermostatConfigurationModel(double maxTemperature, double minTemperature, String hvacUnitResourceUri, String operationalMode) {
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.hvacUnitResourceUri = hvacUnitResourceUri;
        this.operationalMode = operationalMode;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getHvacUnitResourceUri() {
        return hvacUnitResourceUri;
    }

    public void setHvacUnitResourceUri(String hvacUnitResourceUri) {
        this.hvacUnitResourceUri = hvacUnitResourceUri;
    }

    public String getOperationalMode() {
        return operationalMode;
    }

    public void setOperationalMode(String operationalMode) {
        this.operationalMode = operationalMode;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ThermostatConfigurationModel{");
        sb.append("maxTemperature=").append(maxTemperature);
        sb.append(", minTemperature=").append(minTemperature);
        sb.append(", hvacUnitResourceUri='").append(hvacUnitResourceUri).append('\'');
        sb.append(", operationalMode='").append(operationalMode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
