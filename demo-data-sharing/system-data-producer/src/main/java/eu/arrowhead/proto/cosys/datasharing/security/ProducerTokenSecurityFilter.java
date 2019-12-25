package eu.arrowhead.proto.cosys.datasharing.security;

import java.security.PrivateKey;
import java.security.PublicKey;

import eu.arrowhead.common.token.TokenSecurityFilter;


public class ProducerTokenSecurityFilter extends TokenSecurityFilter {
    private PrivateKey myPrivateKey;
    private PublicKey authorizationPublicKey;

    @Override
    protected PrivateKey getMyPrivateKey() {
        return myPrivateKey;
    }

    @Override
    protected PublicKey getAuthorizationPublicKey() {
        return authorizationPublicKey;
    }

    public void setMyPrivateKey(final PrivateKey myPrivateKey) {
        this.myPrivateKey = myPrivateKey;
    }

    public void setAuthorizationPublicKey(final PublicKey authorizationPublicKey) {
        this.authorizationPublicKey = authorizationPublicKey;
    }
}
