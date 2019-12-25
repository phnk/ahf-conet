package eu.arrowhead.proto.cosys.datasharing.security;

import eu.arrowhead.client.library.util.ClientCommonConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import eu.arrowhead.client.library.config.DefaultSecurityConfig;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;


@Configuration
@EnableWebSecurity
public class ProducerSecurityConfig extends DefaultSecurityConfig {

    @Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
    private boolean tokenSecurityFilterEnabled;

    private ProducerTokenSecurityFilter tokenSecurityFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        if (tokenSecurityFilterEnabled && false) {
            tokenSecurityFilter = new ProducerTokenSecurityFilter();
            http.addFilterAfter(tokenSecurityFilter, SecurityContextHolderAwareRequestFilter.class);
        }
    }

    public ProducerTokenSecurityFilter getTokenSecurityFilter() {
        return tokenSecurityFilter;
    }
}
