package eu.arrowhead.proto.cosys.datasharing.controller;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.proto.cosys.datasharing.DataProviderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping(DataProviderConstants.)
public class DataProducerController {


}

/**
package eu.arrowhead.proto.cosys.datasharing;

        import eu.arrowhead.client.library.config.ApplicationInitListener;

// Java imports
        import java.nio.charset.StandardCharsets;
        import java.util.Dictionary;
        import java.util.Hashtable;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;

// Spring framework imports
        import org.apache.logging.log4j.spi.Provider;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.context.event.ContextRefreshedEvent;
        import org.springframework.http.HttpMethod;
        import org.springframework.stereotype.Component;

// Arrowhead imports
        import eu.arrowhead.client.library.ArrowheadService;
        import eu.arrowhead.client.library.util.ClientCommonConstants;
        import eu.arrowhead.common.CommonConstants;


@Component
public class DataProducer extends ApplicationInitListener {
    private int threshold;
    private Dictionary<String, String> inMemoryMap;

    @Autowired
    private ArrowheadService arrowheadService;

    //@Autowired
    //private ProviderSecurityConfig providerSecurityConfig;

    @Value(eu.arrowhead.client.library.util.ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
    private boolean tokenSecurityFilterEnabled;

    @Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
    private boolean sslEnabled;

    @Value(eu.arrowhead.client.library.util.ClientCommonConstants.$CLIENT_SYSTEM_NAME)
    private String thisSystemName;

    @Value(eu.arrowhead.client.library.util.ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
    private String thisSystemAddress;

    @Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
    private int thisSystemPort;

    private DataProducer() {
        threshold = 1000;
        inMemoryMap = new Hashtable();

        // loading random data into the hashtable for testing
        String[] fruits = new String[]{"Orange", "Apple", "Pear", "Strawberry"};

        for (String fruit : fruits) {
            try {
                this.putIntoHashTable(this.hashKey(fruit), fruit);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void customInit(ContextRefreshedEvent event) {
        super.customInit(event);
    }

    @Override
    protected void customDestroy() {
        super.customDestroy();
    }

    // Easier way to change hash function
    private String hashKey(String key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        String s = sb.toString();
        return s;
    }

    public int getTreshold() {
        return threshold;
    }

    public void setThreshold(int newThreshold) {
        threshold = newThreshold;
    }

    public String getValueFromKey(String key) {
        return inMemoryMap.get(key);
    }

    public void putIntoHashTable(Stringpackage eu.arrowhead.proto.cosys.datasharing;

            import eu.arrowhead.client.library.config.ApplicationInitListener;

// Java imports
            import java.nio.charset.StandardCharsets;
            import java.util.Dictionary;
            import java.util.Hashtable;
            import java.security.MessageDigest;
            import java.security.NoSuchAlgorithmException;

// Spring framework imports
            import org.apache.logging.log4j.spi.Provider;
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.beans.factory.annotation.Value;
            import org.springframework.context.event.ContextRefreshedEvent;
            import org.springframework.http.HttpMethod;
            import org.springframework.stereotype.Component;

// Arrowhead imports
            import eu.arrowhead.client.library.ArrowheadService;
            import eu.arrowhead.client.library.util.ClientCommonConstants;
            import eu.arrowhead.common.CommonConstants;


            @Component
            public class DataProducer extends ApplicationInitListener {
        private int threshold;
        private Dictionary<String, String> inMemoryMap;

        @Autowired
        private ArrowheadService arrowheadService;

        //@Autowired
        //private ProviderSecurityConfig providerSecurityConfig;

        @Value(eu.arrowhead.client.library.util.ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
        private boolean tokenSecurityFilterEnabled;

        @Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
        private boolean sslEnabled;

        @Value(eu.arrowhead.client.library.util.ClientCommonConstants.$CLIENT_SYSTEM_NAME)
        private String thisSystemName;

        @Value(eu.arrowhead.client.library.util.ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
        private String thisSystemAddress;

        @Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
        private int thisSystemPort;

    private DataProducer() {
            threshold = 1000;
            inMemoryMap = new Hashtable();

            // loading random data into the hashtable for testing
            String[] fruits = new String[]{"Orange", "Apple", "Pear", "Strawberry"};

            for (String fruit : fruits) {
                try {
                    this.putIntoHashTable(this.hashKey(fruit), fruit);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void customInit(ContextRefreshedEvent event) {
            super.customInit(event);
        }

        @Override
        protected void customDestroy() {
            super.customDestroy();
        }

        // Easier way to change hash function
        private String hashKey(String key) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashInBytes.length; i++) {
                sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            String s = sb.toString();
            return s;
        }

        public int getTreshold() {
            return threshold;
        }

        public void setThreshold(int newThreshold) {
            threshold = newThreshold;
        }

        public String getValueFromKey(String key) {
            return inMemoryMap.get(key);
        }

        public void putIntoHashTable(String key, String value) {
            inMemoryMap.put(key, value);
        }

        public void registerInServiceRegistry() {

        }
    } key, String value) {
        inMemoryMap.put(key, value);
    }

    public void registerInServiceRegistry() {

    }
}

 **/
