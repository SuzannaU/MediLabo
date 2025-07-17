package medilabo.frontapp.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

/**
 * This custom decoder prevents the automatic generation of Feign Exceptions for specified status codes
 * @see FeignConfig
 */
@Component
public class CustomErrorDecoder implements ErrorDecoder {

    /**
     * This method only generates Feign Exceptions for responses with status codes different from 400 and 404.
     *
     * @param s the methodKey of the exception
     * @param response
     * @return the feign exception IF response code is not 400 or 404, otherwise returns null
     */
    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 400 || response.status() == 404) {
            return null;
        }
        return new Default().decode(s, response);
    }
}
