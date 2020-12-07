package eu.venthe.combined.configuration.mvc.interceptor;

import eu.venthe.combined.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class RestDomainExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messages;

    // TODO: Handle parameters
    @ExceptionHandler(value = {DomainException.class})
    protected ResponseEntity<Object> handleConflict(DomainException ex, WebRequest request) {
        String bodyOfResponse = messages.getMessage(ex.getCode(), new Object[]{}, request.getLocale());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
