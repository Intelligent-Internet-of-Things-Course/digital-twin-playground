package it.unimore.dipi.iot.digitaltwin.mqtt;

/**
 * Author: Marco Picone, Ph.D. (picone.m@gmail.com)
 * Date: 24/04/2020
 * Project: MQTT BOT Smart Object (mqtt-bot-smartobject)
 */
public class DigitalTwinConfiguration {

    private String mqttBrokerAddress;

    private int mqttBrokerPort;

    private String destinationMqttBrokerAddress;

    private int destinationMqttBrokerPort;

    public DigitalTwinConfiguration() {
    }

    public DigitalTwinConfiguration(String mqttBrokerAddress, int mqttBrokerPort, String destinationMqttBrokerAddress, int destinationMqttBrokerPort) {
        this.mqttBrokerAddress = mqttBrokerAddress;
        this.mqttBrokerPort = mqttBrokerPort;
        this.destinationMqttBrokerAddress = destinationMqttBrokerAddress;
        this.destinationMqttBrokerPort = destinationMqttBrokerPort;
    }

    public String getMqttBrokerAddress() {
        return mqttBrokerAddress;
    }

    public void setMqttBrokerAddress(String mqttBrokerAddress) {
        this.mqttBrokerAddress = mqttBrokerAddress;
    }

    public int getMqttBrokerPort() {
        return mqttBrokerPort;
    }

    public void setMqttBrokerPort(int mqttBrokerPort) {
        this.mqttBrokerPort = mqttBrokerPort;
    }

    public String getDestinationMqttBrokerAddress() {
        return destinationMqttBrokerAddress;
    }

    public void setDestinationMqttBrokerAddress(String destinationMqttBrokerAddress) {
        this.destinationMqttBrokerAddress = destinationMqttBrokerAddress;
    }

    public int getDestinationMqttBrokerPort() {
        return destinationMqttBrokerPort;
    }

    public void setDestinationMqttBrokerPort(int destinationMqttBrokerPort) {
        this.destinationMqttBrokerPort = destinationMqttBrokerPort;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DigitalTwinConfiguration{");
        sb.append("mqttBrokerAddress='").append(mqttBrokerAddress).append('\'');
        sb.append(", mqttBrokerPort=").append(mqttBrokerPort);
        sb.append(", destinationMqttBrokerAddress='").append(destinationMqttBrokerAddress).append('\'');
        sb.append(", destinationMqttBrokerPort=").append(destinationMqttBrokerPort);
        sb.append('}');
        return sb.toString();
    }
}
