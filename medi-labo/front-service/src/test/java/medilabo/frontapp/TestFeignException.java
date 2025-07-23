package medilabo.frontapp;

import feign.FeignException;

/**
 * This class is used only for testing purposes, instead of FeignException class whose constructor is protected and thus not throwable.
 */
public class TestFeignException extends FeignException {
    public TestFeignException(int status, String message) {
        super(status, message);
    }
}
