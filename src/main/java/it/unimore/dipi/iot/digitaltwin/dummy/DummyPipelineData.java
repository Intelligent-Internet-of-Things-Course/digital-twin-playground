package it.unimore.dipi.iot.digitaltwin.dummy;

import it.unimore.dipi.iot.wldt.processing.PipelineData;

/**
 * Author: Marco Picone, Ph.D. (marco.picone@unimore.it)
 * Date: 30/07/2020
 * Project: Dummy Example - White Label Digital Twin - Java Framework
 */
public class DummyPipelineData implements PipelineData {

    private int value = 0;

    public DummyPipelineData(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DummyPipelineData{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
