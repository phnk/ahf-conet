package eu.arrowhead.proto.cosys.controller;

import eu.arrowhead.proto.cosys.ContactSystemConstants;
import eu.arrowhead.proto.cosys.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ContactSystemConstants.CONTACT_URI)
public class ContactSystemController {

    // TODO: implement inmemorydb or database objects depending on what the objects contains.

    @Autowired
    private PublisherService publisherService;

    /*
        TODO: Implement the following endpoints to allow the contract system to get messages
            * Offer endpoint
                - Arguments: Random hash, offer amount
            * Reject endpoint
            * Accept endpoint
            * Testing endpoint (To send requests through the eventhandler and see if they are received by the DataProducer)
     */

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public String offerEndpoint(@RequestParam(name = ContactSystemConstants.OFFER_AMOUNT) Integer offerAmount,
                                              @RequestParam(name = ContactSystemConstants.RANDOM_HASH) String randomHash) {

        // save it in a db (and who made the offer)

        // publish the offer through the eventhandler

        // return some ok or not
    }
}
