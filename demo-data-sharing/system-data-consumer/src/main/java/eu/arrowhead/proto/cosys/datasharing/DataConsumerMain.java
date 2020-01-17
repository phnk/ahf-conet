package eu.arrowhead.proto.cosys.datasharing;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, DataConsumerConstants.BASE_PACKAGE})
public class DataConsumerMain implements ApplicationRunner {

    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    protected SSLProperties sslProperties;

    private final Logger logger = LogManager.getLogger(DataConsumerMain.class);

    public static void main (final String[] args) {
        SpringApplication.run(DataConsumerMain.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
