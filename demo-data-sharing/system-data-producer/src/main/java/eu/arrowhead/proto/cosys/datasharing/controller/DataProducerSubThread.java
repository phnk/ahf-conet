package eu.arrowhead.proto.cosys.datasharing.controller;


import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.proto.cosys.datasharing.DataProducerListener;
import eu.arrowhead.proto.cosys.datasharing.DataProviderConstants;
import eu.arrowhead.proto.cosys.datasharing.database.InMemoryDb;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataProducerSubThread extends Thread {
    private boolean interrupted = false;

    @Resource(name = DataProviderConstants.NOTIFICATION_QUEUE)
    private ConcurrentLinkedQueue<EventDTO> notificationQueue;

    //@Resource(name = DataProviderConstants.THRESHOLD)
    private Integer threshold = 1000;

    @Resource(name = DataProviderConstants.IN_MEMORY_DB)
    private InMemoryDb inMemoryDb;

    private final Logger logger = LogManager.getLogger(DataProducerListener.class);

    @Override
    public void run() {
        while(!interrupted) {
            try {
                if (notificationQueue.peek() != null) {
                    for (final EventDTO event : notificationQueue) {
                        if (event.getEventType().equals(DataProviderConstants.REQUEST_RECEIVED)) {
                            logger.info(event.getMetaData().toString());
                            if (event.getMetaData().get("offer") == null) {
                                notificationQueue.clear();
                            }

                            else if (Integer.parseInt(event.getMetaData().get("offer")) >= this.threshold) {
                                // save the offer
                                String randomIdentifier = event.getMetaData().get("randomhash");
                                String hash = event.getMetaData().get("hash");
                                logger.info("Added " + randomIdentifier + " with hash " + hash + " into the db");
                                inMemoryDb.getOfferMap().put(randomIdentifier, hash);
                            }
                        } else if (event.getEventType().equals(DataProviderConstants.REQUEST_STOP)) {
                            interrupted = true;
                        }
                    }
                    notificationQueue.clear();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}

