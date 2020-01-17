package eu.arrowhead.proto.cosys.datasharing.controller;


import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.*;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.proto.cosys.datasharing.DataProducerListener;
import eu.arrowhead.proto.cosys.datasharing.DataProviderConstants;
import eu.arrowhead.proto.cosys.datasharing.database.InMemoryDb;
import eu.arrowhead.proto.cosys.datasharing.dto.EmptyDTO;
import org.apache.http.client.HttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataProducerSubThread extends Thread {
    private boolean interrupted = false;

    @Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
    private String mySystemName;

    @Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
    private String mySystemAddress;

    @Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
    private Integer mySystemPort;

    @Autowired
    protected SSLProperties sslProperties;

    @Autowired
    protected ArrowheadService arrowheadService;

    @Resource(name = DataProviderConstants.NOTIFICATION_QUEUE)
    private ConcurrentLinkedQueue<EventDTO> notificationQueue;

    @Value(DataProviderConstants.THRESHOLD)
    private Integer threshold;

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
                                rejectContract(event);
                            } else if (Integer.parseInt(event.getMetaData().get("offer")) >= this.threshold) {
                                acceptContract(event);
                            } else {
                                logger.info("Rejected an offer");
                                rejectContract(event);
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



    public void acceptContract(EventDTO event) {
        // save the offer
        String randomIdentifier = event.getMetaData().get("randomHash");
        String hash = event.getMetaData().get("hash");
        logger.info("Added " + randomIdentifier + " with hash " + hash + " into the db");
        inMemoryDb.getOfferMap().put(randomIdentifier, hash);

        OrchestrationResultDTO orchestrationResult = getService("accept-contract");
        validateOrchestrationResult(orchestrationResult, "accept-contract");

        final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());

        // send it
        arrowheadService.consumeServiceHTTP(EmptyDTO.class,
                HttpMethod.valueOf(orchestrationResult.getMetadata().get(DataProviderConstants.HTTP_METHOD)),
                orchestrationResult.getProvider().getAddress(),
                orchestrationResult.getProvider().getPort(),
                orchestrationResult.getServiceUri(),
                getInterface(),
                token,
                "",
                "random-hash", randomIdentifier,
                "provider-name", mySystemName,
                "provider-address", mySystemAddress,
                "provider-port", Integer.toString(mySystemPort));
    }

    public void rejectContract(EventDTO event) {
        String randomIdentifier = event.getMetaData().get("randomHash");
        OrchestrationResultDTO orchestrationResult = getService("reject-contract");

        validateOrchestrationResult(orchestrationResult, "reject-contract");

        final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());

        // send it
        arrowheadService.consumeServiceHTTP(EmptyDTO.class,
                HttpMethod.valueOf(orchestrationResult.getMetadata().get(DataProviderConstants.HTTP_METHOD)),
                orchestrationResult.getProvider().getAddress(),
                orchestrationResult.getProvider().getPort(),
                orchestrationResult.getServiceUri(),
                getInterface(),
                token,
                "",
                "random-hash", randomIdentifier);
    }


    private String getInterface() {
        return sslProperties.isSslEnabled() ? DataProviderConstants.INTERFACE_SECURE : DataProviderConstants.INTERFACE_INSECURE;
    }

    private void printOut(final Object object) {
        System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }

    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition) {
        if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
            throw new InvalidParameterException("Requested and orchestrated service definition do not match");
        }

        boolean hasValidInterface = false;
        for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
            if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
                hasValidInterface = true;
                break;
            }
        }
        if (!hasValidInterface) {
            throw new InvalidParameterException("Requested and orchestrated interface do not match");
        }
    }

    public OrchestrationResultDTO getService(String serviceDefinition) {

        // find the endpoint
        final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(serviceDefinition)
                .interfaces(getInterface())
                .build();

        final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
        final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
                .flag(Flag.MATCHMAKING, true)
                .flag(Flag.OVERRIDE_STORE, true)
                .build();

        printOut(orchestrationFormRequest);

        final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);

        logger.info("Orchestration response:");
        printOut(orchestrationResponse);

        if (orchestrationResponse == null) {
            logger.info("No orchestration response received");
            // TODO: throw error
        } else if (orchestrationResponse.getResponse().isEmpty()) {
            logger.info("No provider found during the orchestration");
            // TODO: throw error
        }

        return orchestrationResponse.getResponse().get(0);
    }
}

