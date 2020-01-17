package eu.arrowhead.proto.cosys.datasharing.dto;

import java.io.Serializable;

public class AcceptDTO implements Serializable {
    private String randomHash;
    private String providerName;
    private String providerAddress;
    private Integer providerPort;

    public AcceptDTO(String randomHash, String providerName, String providerAddress, Integer providerPort) {
        this.randomHash = randomHash;
        this.providerName = providerName;
        this.providerAddress = providerAddress;
        this.providerPort = providerPort;
    }

    public String getRandomHash() {
        return randomHash;
    }

    public void setRandomHash(String randomHash) {
        this.randomHash = randomHash;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    public Integer getProviderPort() {
        return providerPort;
    }

    public void setProviderPort(Integer providerPort) {
        this.providerPort = providerPort;
    }
}
