package eu.arrowhead.exneg.example;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;

public class Main {
    public static void main(final String[] args) {
        final var service = new ArrowheadService();

        final var req = new ServiceRegistryRequestDTO();
        req.setServiceUri("/test/test");
        final var res = service.registerServiceToServiceRegistry(req);
        System.out.println("Bye!");
    }
}
