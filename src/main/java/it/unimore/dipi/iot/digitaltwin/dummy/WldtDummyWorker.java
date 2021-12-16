package it.unimore.dipi.iot.digitaltwin.dummy;

import it.unimore.dipi.iot.wldt.exception.WldtConfigurationException;
import it.unimore.dipi.iot.wldt.exception.WldtRuntimeException;
import it.unimore.dipi.iot.wldt.processing.PipelineData;
import it.unimore.dipi.iot.wldt.processing.ProcessingPipelineListener;
import it.unimore.dipi.iot.wldt.worker.WldtWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.Random;

/**
 * Author: Marco Picone, Ph.D. (marco.picone@unimore.it)
 * Date: 30/07/2020
 * Project: Dummy Example - White Label Digital Twin - Java Framework
 */
public class WldtDummyWorker extends WldtWorker<DummyWorkerConfiguration, String, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(WldtDummyWorker.class);

    public static final String DEFAULT_PROCESSING_PIPELINE = "default_processing_pipeline";

    private Random random = null;

    private String wldtId = null;

    private int RUN_COUNT_LIMIT = 10000;

    public WldtDummyWorker(String wldtId, DummyWorkerConfiguration dummyWorkerConfiguration) {
        super(dummyWorkerConfiguration);
        this.random = new Random();
        this.wldtId = wldtId;
    }

    @Override
    public void startWorkerJob() throws WldtConfigurationException, WldtRuntimeException {

        try{
            for(int i = 0; i < RUN_COUNT_LIMIT; i++)
                emulateExternalGetRequest(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void emulateExternalGetRequest(int roundIndex) {

        try{

            logger.info("WLDT: {} Round [{}]: Dummy Worker Incoming Get Request .... ", this.wldtId, roundIndex);

            int physicalObjectValue = 0;

            physicalObjectValue = retrieveValueFromPhysicalObject();
            logger.info("Round [{}]: Physical Object Value: {} ", roundIndex, physicalObjectValue);

            //Check Processing Pipeline
            if(this.hasProcessingPipeline(WldtDummyWorker.DEFAULT_PROCESSING_PIPELINE)) {
                this.executeProcessingPipeline(DEFAULT_PROCESSING_PIPELINE,
                        new DummyPipelineData(physicalObjectValue),
                        new ProcessingPipelineListener() {
                               @Override
                               public void onPipelineDone(Optional<PipelineData> result) {
                                   if(result != null && result.isPresent())
                                       logger.info("Processing Pipeline Executed ! Result: {}", ((DummyPipelineData)result.get()).getValue());
                                   else
                                       logger.error("Processing pipeline result = null !");
                               }

                               @Override
                               public void onPipelineError() {
                                   logger.error("Processing pipeline Error !");
                               }
                         });
            }

            Thread.sleep(random.nextInt(3000) + 1000);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int retrieveValueFromPhysicalObject(){
        try{
            Thread.sleep(random.nextInt(1000) + 100);
            return random.nextInt(3000);
        }catch (Exception e){
            logger.error("Error getting random mockup value ! Error: {}", e.getLocalizedMessage());
            return 0;
        }
    }

}
