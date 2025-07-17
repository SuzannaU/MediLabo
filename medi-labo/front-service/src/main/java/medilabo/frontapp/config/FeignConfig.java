package medilabo.frontapp.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

/**
 * Configuration for Feign Client.
 *
 * @see CustomErrorDecoder
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    /**
     * Custom ErrorDecoder used to recover status codes received with responses instead of automatic Feign Exceptions.
     *
     * @return the error decoder
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * Override of the apply method from RequestInterceptor, adding Authorization header to requests
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String auth = "user:password";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        requestTemplate.header("Authorization", authHeader);
    }
}
