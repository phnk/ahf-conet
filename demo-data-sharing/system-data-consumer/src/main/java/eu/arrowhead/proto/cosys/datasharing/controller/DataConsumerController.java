package eu.arrowhead.proto.cosys.datasharing.controller;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.*;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.proto.cosys.datasharing.DataConsumerConstants;
import eu.arrowhead.proto.cosys.datasharing.database.AcceptedItem;
import eu.arrowhead.proto.cosys.datasharing.dto.EmptyDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@RestController
public class DataConsumerController {

    @Autowired
    protected SSLProperties sslProperties;

    @Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
    private String mySystemAddress;

    @Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
    private Integer mySystemPort;

    @Autowired
    private ArrayList<AcceptedItem> db;

    @Autowired
    protected ArrowheadService arrowheadService;

    private final Logger logger = LogManager.getLogger(DataConsumerController.class);

    @PostMapping(path = DataConsumerConstants.REJECT_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public void rejectEndpoint(@RequestParam(name = DataConsumerConstants.IDENTIFYING_HASH) String hash) {
        logger.info("The offer with identifying hash: " + hash +  " was rejected.");
    }

    @PostMapping(path = DataConsumerConstants.ACCEPT_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public void acceptEndpoint(@RequestParam(name = DataConsumerConstants.IDENTIFYING_HASH) String hash,
                                                @RequestParam(name = DataConsumerConstants.PRODUCER_NAME) String producerName,
                                                @RequestParam(name = DataConsumerConstants.PRODUCER_ADDRESS) String producerAddress,
                                                @RequestParam(name = DataConsumerConstants.PRODUCER_PORT) String producerPort,
                                                @RequestParam(name = DataConsumerConstants.SERVICE_URI) String serviceUri) {
        logger.info("The offer with identifying hash: " + hash + " was accepted by: " + producerName + " with address: " + producerAddress + ":" + producerPort);
        db.add(new AcceptedItem(hash, producerName, producerAddress, producerPort, serviceUri));
    }

    // EVERYTHING BELOW IS FOR DEMO PURPOSES ONLY
    // TODO: add proper way to find the correct contract system
    @PostMapping(path = "/request-data", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public void requestData(@RequestParam(name = "data-hash") String dataHash,
                                          @RequestParam(name = "offer-amount") Integer offerAmount) {

        String randomIdentifier = getRandomIdentifier();

        OrchestrationResultDTO orchestrationResult = getService("offer-contract");
        validateOrchestrationResult(orchestrationResult, "offer-contract");

        final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());

        arrowheadService.consumeServiceHTTP(EmptyDTO.class,
                HttpMethod.valueOf(orchestrationResult.getMetadata().get(DataConsumerConstants.HTTP_METHOD)),
                orchestrationResult.getProvider().getAddress(),
                orchestrationResult.getProvider().getPort(),
                orchestrationResult.getServiceUri(),
                getInterface(),
                token,
                "",
                "ident-hash", randomIdentifier,
                "offer-amount", Integer.toString(offerAmount),
                "data-hash", dataHash,
                "request-address", mySystemAddress,
                "request-port", Integer.toString(mySystemPort)
                );
    }

    // TODO: add proper way to connect to the producer which has the data and accepted us
    @PostMapping(path = "/get-data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public String getData(@RequestParam(name = "identifying-hash") String identifyingHash) {
        for (int i = 0; i < db.size(); i++) {
            if (db.get(i).getIdentifyingHash().equals(identifyingHash)) {
                logger.info("Trying to send a request to: " + db.get(i).getProducerAddress() + ":" + db.get(i).getProducerPort());

                OrchestrationResultDTO orchestrationResult = getService(db.get(i).getServiceUri());

                final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());

                JSONObject a = arrowheadService.consumeServiceHTTP(JSONObject.class,
                        HttpMethod.POST,
                        db.get(i).getProducerAddress(),
                        Integer.parseInt(db.get(i).getProducerPort()),
                        "/get-data",
                        getInterface(),
                        token,
                        "",
                        "identifying-hash", identifyingHash);

                if (a.containsKey("data") && a.containsKey("status")) {
                    if (a.get("status").equals("ERROR")) {
                        return "The hash: " + identifyingHash + " was not accepted";
                    } else {
                        return a.get("data").toString();
                    }
                }
            }
        }
        return "The hash: " + identifyingHash + " is not used";
    }

    // HELPERS
    public String getRandomIdentifier() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    private String getInterface() {
        return sslProperties.isSslEnabled() ? DataConsumerConstants.INTERFACE_SECURE : DataConsumerConstants.INTERFACE_INSECURE;
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

        final OrchestrationFormRequestDTO.Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
        final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
                .flag(OrchestrationFlags.Flag.MATCHMAKING, true)
                .flag(OrchestrationFlags.Flag.OVERRIDE_STORE, true)
                .build();

        final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);

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
