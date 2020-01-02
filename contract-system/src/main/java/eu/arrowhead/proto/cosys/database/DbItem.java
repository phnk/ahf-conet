package eu.arrowhead.proto.cosys.database;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class DbItem {

    private String randomHash;
    private Integer offerAmount;
    private HttpServletRequest request; // should be some type of object

    public DbItem(String randomHash, Integer offerAmount, HttpServletRequest request) {
        this.randomHash = randomHash;
        this.offerAmount = offerAmount;
        this.request = request;
    }

    public Integer getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(Integer offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getRandomHash() {
        return randomHash;
    }

    public void setRandomHash(String randomHash) {
        this.randomHash = randomHash;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
