package medilabo.risksapp.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

/**
 * Configuration for Feign Client
 *
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    @Value("${medilabo.user.username}")
    private String username;

    @Value("${medilabo.user.password}")
    private String password;

    /**
     * Override of the apply method from RequestInterceptor, adding Authorization header to requests
     *
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        requestTemplate.header("Authorization", authHeader);
    }
}
