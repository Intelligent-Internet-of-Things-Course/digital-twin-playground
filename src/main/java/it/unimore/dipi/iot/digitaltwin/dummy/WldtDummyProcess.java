package it.unimore.dipi.iot.digitaltwin.dummy;

import it.unimore.dipi.iot.wldt.engine.WldtConfiguration;
import it.unimore.dipi.iot.wldt.engine.WldtEngine;
import it.unimore.dipi.iot.wldt.exception.WldtConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

/**
 * Author: Marco Picone, Ph.D. (marco.picone@unimore.it)
 * Date: 30/07/2020
 * Project: Dummy Example - White Label Digital Twin - Java Framework
 */
public class WldtDummyProcess {

    private static final String TAG = "[WLDT-Dummy-Process]";

    private static final Logger logger = LoggerFactory.getLogger(WldtDummyProcess.class);

    public static void main(String[] args)  {

        try{

            logger.info("{} Initializing WLDT-Engine ... ", TAG);

            //Example loading everything from the configuration file
            //WldtEngine wldtEngine = new WldtEngine();
            //wldtEngine.startWorkers();

            //Manual creation of the WldtConfiguration
            WldtConfiguration wldtConfiguration = new WldtConfiguration();
            wldtConfiguration.setDeviceNameSpace("it.unimore.dipi.things");
            wldtConfiguration.setWldtBaseIdentifier("it.unimore.dipi.iot.wldt.example.dummy");
            wldtConfiguration.setWldtStartupTimeSeconds(10);
            wldtConfiguration.setApplicationMetricsEnabled(false);
            wldtConfiguration.setApplicationMetricsReportingPeriodSeconds(10);
            wldtConfiguration.setMetricsReporterList(Collections.singletonList("csv"));

            //Init the Engine
            WldtEngine wldtEngine = new WldtEngine(wldtConfiguration);

            //Init Dummy Worker without Cache
            WldtDummyWorker wldtDummyCachedWorker = new WldtDummyWorker(
                    wldtEngine.getWldtId(),
                    new DummyWorkerConfiguration());

            //Init Dummy Worker with Cache
            /*
            WldtDummyCachedWorker wldtDummyCachedWorker = new WldtDummyCachedWorker(
                    wldtEngine.getWldtId(),
                    new DummyWorkerConfiguration(),
                    new WldtCache<>(5, TimeUnit.SECONDS));
            */

            //Set a Processing Pipeline
            //wldtDummyWorker.addProcessingPipeline(WldtDummyWorker.DEFAULT_PROCESSING_PIPELINE, new ProcessingPipeline(new DummyProcessingStep()));

            //Init WLDT Engine with Worker Cache
            wldtEngine.addNewWorker(wldtDummyCachedWorker);

            wldtEngine.startWorkers();

        }catch (Exception | WldtConfigurationException e){
            e.printStackTrace();
        }
    }

}
