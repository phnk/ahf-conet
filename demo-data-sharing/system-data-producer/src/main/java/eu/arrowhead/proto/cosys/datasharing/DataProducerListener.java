package eu.arrowhead.proto.cosys.datasharing;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.proto.cosys.datasharing.database.InMemoryDb;
import eu.arrowhead.proto.cosys.datasharing.security.ProducerSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


@Component
public class DataProducerListener extends ApplicationInitListener {

    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    private ProducerSecurityConfig producerSecurityConfig;

    @Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
    private boolean tokenSecurityFilterEnabled;

    @Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
    private boolean sslEnabled;

    @Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
    private String mySystemName;

    @Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
    private String mySystemAddress;

    @Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
    private Integer mySystemPort;

    private final Logger logger = LogManager.getLogger(DataProducerListener.class);

    @Bean( DataProviderConstants.NOTIFICATION_QUEUE )
    public ConcurrentLinkedQueue<EventDTO> getNotificationQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    @Bean( DataProviderConstants.IN_MEMORY_DB )
    public InMemoryDb getInMemoryDb() {
        return new InMemoryDb();
    }

    @Override
    protected void customInit(final ContextRefreshedEvent event) {
        checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);

        // register in the service reg
        final ServiceRegistryRequestDTO createProviderServiceRequest =
                createServiceRegistryRequest(DataProviderConstants.CREATE_PRODUCER_SERVICE_DEFINITION,
                        DataProviderConstants.PROVIDER_URI,
                        HttpMethod.POST);
        arrowheadService.forceRegisterServiceToServiceRegistry(createProviderServiceRequest);

        ServiceRegistryRequestDTO getProviderServiceRequest =
                createServiceRegistryRequest(DataProviderConstants.GET_PRODUCER_SERVICE_DEFINITION,
                        DataProviderConstants.PROVIDER_URI,
                        HttpMethod.GET);

        // meta data for our specific context
        //getProviderServiceRequest.getMetadata().put(DataProviderConstants.REQUEST_PARAM_KEY_BRAND,
        //        DataProviderConstants.REQUEST_PARAM_KEY_BRAND);
        //getProviderServiceRequest.getMetadata().put(DataProviderConstants.REQUEST_PARAM_KEY_COLOR,
        //        DataProviderConstants.REQUEST_PARAM_KEY_COLOR);
    }

    @Override
    protected void customDestroy() {
        arrowheadService.unregisterServiceFromServiceRegistry(DataProviderConstants.CREATE_PRODUCER_SERVICE_DEFINITION);
    }

    private void setTokenSecurityFilter() {
        final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();

        if (authorizationPublicKey == null) {
            throw new ArrowheadException("Authorization public key is null");
        }

        KeyStore keystore;

        try {
            keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
            keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
            throw new ArrowheadException(ex.getMessage());
        }

        final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

        producerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
        producerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);

   }

   private ServiceRegistryRequestDTO createServiceRegistryRequest(final String serviceDefinition, final String serviceUri, HttpMethod httpMethod) {
        final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
        serviceRegistryRequest.setServiceDefinition(serviceDefinition);

        final SystemRequestDTO systemRequest = new SystemRequestDTO();
        systemRequest.setSystemName(mySystemName);
        systemRequest.setAddress(mySystemAddress);
        systemRequest.setPort(mySystemPort);


        serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE);
        serviceRegistryRequest.setInterfaces(List.of(DataProviderConstants.INTERFACE_INSECURE));

        serviceRegistryRequest.setProviderSystem(systemRequest);
        serviceRegistryRequest.setServiceUri(serviceUri);
        //serviceRegistryRequest.setMetadata(new HashMap<>());
        //serviceRegistryRequest.getMetadata().put(DataProviderConstants.HTTP_METHOD, httpMethod.name());
        return serviceRegistryRequest;

   }
}