package eu.arrowhead.proto.cosys.publisher.service;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.proto.cosys.ContactSystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {

    @Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
    private String mySystemName;

    @Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
    private String mySystemAddress;

    @Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
    private Integer mySystemPort;

    @Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
    private boolean sslEnabled;

    @Autowired
    private ArrowheadService arrowheadService;

    // https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-car-with-events/demo-car-provider-with-publishing/src/main/java/eu/arrowhead/client/skeleton/publisher/service/PublisherService.java
    public void publish() {
        // TODO: implement
    }
}
