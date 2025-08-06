package medilabo.frontapp.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class FeignConfig implements RequestInterceptor {

    @Value("${medilabo.user.username}")
    private String username;

    @Value("${medilabo.user.password}")
    private String password;

    /**
     * Override of the apply method from RequestInterceptor.
     * Adds Authorization header to requests for Http Basic authentication
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
