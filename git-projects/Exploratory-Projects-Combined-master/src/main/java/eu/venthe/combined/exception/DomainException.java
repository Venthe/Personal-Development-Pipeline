package eu.venthe.combined.exception;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
@Value
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class DomainException extends RuntimeException {
    String code;
}
