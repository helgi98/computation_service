package ua.edu.lnu.dcservice.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.edu.lnu.dcservice.controllers.dto.ExceptionMessageDto;
import ua.edu.lnu.dcservice.exceptions.ServiceException;
import ua.edu.lnu.dcservice.exceptions.ValidationException;

import java.io.IOException;
import java.security.SignatureException;

@Slf4j
@ControllerAdvice
public class BaseController {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,
            reason = "IO exception")
    @ExceptionHandler({IOException.class})
    public ExceptionMessageDto ioExceptionHandle(Exception ex) {
        log.debug("IO exception: {}", ex);
        return new ExceptionMessageDto("IO error occurred");
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public ExceptionMessageDto serviceException(ServiceException ex) {
        log.debug("Service exception: {}", ex);
        return new ExceptionMessageDto(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ExceptionMessageDto validationException(ValidationException ex) {
        log.debug("Validation exception: {}", ex);
        return new ExceptionMessageDto(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED,
            reason = "Unauthorized access")
    @ExceptionHandler({SignatureException.class, ExpiredJwtException.class})
    public ExceptionMessageDto authorizationException(Exception ex) {
        log.debug("Authorization exception: {}", ex);
        return new ExceptionMessageDto("Unauthorized access occurred");
    }
}
