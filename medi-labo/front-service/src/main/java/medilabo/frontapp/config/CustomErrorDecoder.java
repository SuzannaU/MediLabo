package medilabo.frontapp.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 400 || response.status() == 404) {
            return null;
        }
        return new Default().decode(s, response);
    }
}
