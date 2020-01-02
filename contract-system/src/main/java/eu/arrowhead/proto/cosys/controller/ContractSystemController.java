package eu.arrowhead.proto.cosys.controller;

import eu.arrowhead.proto.cosys.ContractSystemApplicationInitListener;
import eu.arrowhead.proto.cosys.ContractSystemConstants;
import eu.arrowhead.proto.cosys.database.DbItem;
import eu.arrowhead.proto.cosys.publisher.event.PresetEventType;
import eu.arrowhead.proto.cosys.publisher.service.PublisherService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping(ContractSystemConstants.CONTRACT_URI)
public class ContractSystemController {

    private final Logger logger = LogManager.getLogger(ContractSystemApplicationInitListener.class);

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private ArrayList<DbItem> db;
   /*
        TODO: Implement the following endpoints to allow the contract system to get messages
            * Offer endpoint
                - Arguments: Random hash, offer amount
            * Reject endpoint
            * Accept endpoint
            * Testing endpoint (To send requests through the eventhandler and see if they are received by the DataProducer)
     */

    @PostMapping(path = ContractSystemConstants.OFFER_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public void offerEndpoint(@RequestParam(name = ContractSystemConstants.OFFER_AMOUNT) Inteportus.arrowhead-ci.tmit.bme.huger offerAmount,
                                            @RequestParam(name = ContractSystemConstants.RANDOM_HASH) String randomHash, HttpServletRequest request) {

        logger.info("Received a offer POST request");
        // save it in a db (and who made the offer)
        db.add(new DbItem(randomHash, offerAmount, request));
        // publish the offer through the eventhandler

        // arguments: eventType, metadata, payload
        HashMap<String, String> tempMap = new HashMap<String, String>();

        tempMap.put("randomHash", randomHash);
        tempMap.put("offer", Integer.toString(offerAmount));

        // payload hardcoded should probably be something
        publisherService.publish(PresetEventType.REQUEST_RECEIVED, tempMap, "1");
        // return some ok or not
    }

    @PostMapping(path = ContractSystemConstants.REJECT_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public String rejectEndpoint(@RequestParam(name = ContractSystemConstants.RANDOM_HASH) String randomHash) {
        logger.info("Received a reject POST request");
        // find the request
        DbItem tempItem = getItem(randomHash);
        if (tempItem != null) {
            return "Hash could be found";
        } else {
            return "Hash could not be found";
        }
    }

    @PostMapping(path = ContractSystemConstants.ACCEPT_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public void acceptEndpoint(@RequestParam(name = ContractSystemConstants.RANDOM_HASH) String randomHash, @RequestParam(name = ContractSystemConstants.DATA) String data) {
        // find the request
        logger.info("Received a accept POST request");
        DbItem tempItem = getItem(randomHash);

        if (tempItem != null) {
            // somehow relay it to the requester
        } else {
            // hash could not be found
        }

    }

    @GetMapping(path = "/echo")
    @ResponseBody public String echo() {
        return "echo";
    }

    public DbItem getItem(String randomHash) {
        for (int i = 0; i < db.size(); i++) {
            if (db.get(i).getRandomHash().equals(randomHash)) {
                return db.get(i);
            }
        }
        return null;
    }

}
