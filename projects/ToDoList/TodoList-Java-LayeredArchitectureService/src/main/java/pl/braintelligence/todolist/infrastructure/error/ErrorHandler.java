package pl.braintelligence.todolist.infrastructure.error;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.braintelligence.todolist.domain.exceptions.DomainException;
import pl.braintelligence.todolist.domain.exceptions.EntityAlreadyExistsException;
import pl.braintelligence.todolist.domain.exceptions.ErrorCode;
import pl.braintelligence.todolist.domain.exceptions.InvalidEntityException;
import pl.braintelligence.todolist.domain.exceptions.MissingEntityException;
import javax.servlet.http.HttpServletRequest;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.ResponseEntity.status;
import static pl.braintelligence.todolist.domain.exceptions.ErrorCode.UNEXPECTED_ERROR;

@RestControllerAdvice
class ErrorHandler {

    private static final Logger log = getLogger(ErrorHandler.class);

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex, HttpServletRequest request) {
        return handleDomainException(ex, request, UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<ErrorMessage> handleInvalidEntityException(InvalidEntityException ex, HttpServletRequest request) {
        return handleDomainException(ex, request, UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MissingEntityException.class)
    public ResponseEntity<ErrorMessage> handleMissingEntityException(MissingEntityException ex, HttpServletRequest request) {
        return handleDomainException(ex, request, NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception ex, HttpServletRequest request) {
        log.error(createLog(request, INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR, ex.getMessage()), ex);
        return status(INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(UNEXPECTED_ERROR));
    }

    private ResponseEntity<ErrorMessage> handleDomainException(DomainException ex, HttpServletRequest request, HttpStatus status) {
        log.warn(createLog(request, status, ex.getCode(), ex.getMessage()));
        return status(status)
                .body(new ErrorMessage(ex.getCode()));
    }

    private String createLog(HttpServletRequest request, HttpStatus status, ErrorCode code, String message) {
        return request.getMethod() + " " + request.getRequestURI() + " " + status.value() + " | " + code + " | " + message;
    }
}