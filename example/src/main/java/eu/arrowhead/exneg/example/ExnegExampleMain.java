package eu.arrowhead.exneg.example;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, ExnegExampleConstants.BASE_PACKAGE})
public class ExnegExampleMain implements ApplicationRunner {
    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    protected SSLProperties sslProperties;

    public static void main(final String[] args) {
        SpringApplication.run(ExnegExampleMain.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        final var req = new ServiceRegistryRequestDTO();
        req.setServiceUri("/test/test");
        final var res = arrowheadService.registerServiceToServiceRegistry(req);
        System.out.println("Bye!");
    }
}
