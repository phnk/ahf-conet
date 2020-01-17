package eu.arrowhead.proto.cosys.datasharing.dto;

import java.io.Serializable;

public class RejectRequestDTO implements Serializable {
    private String randomHash;

    public RejectRequestDTO(String randomHash) {
        this.randomHash = randomHash;
    }

    public String getRandomHash() {
        return randomHash;
    }

    public void setRandomHash(String randomHash) {
        this.randomHash = randomHash;
    }
}
