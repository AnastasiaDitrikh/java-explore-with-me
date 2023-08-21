package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.model.ApiError;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.debug("Получен статус 404 NOT_FOUND {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .message(e.getMessage())
                .reason("The required object was not found.")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlerEmailConflictException(final ConflictException e) {
        log.debug("Получен статус 409 CONFLICT {}", e.getMessage(), e);

        return ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerIncorrectParametersException(UncorrectedParametersException e) {
        log.debug("Получен статус 400 BAD_REQUEST {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message(e.getMessage())
                .reason("Incorrect parameters")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlerSQLException(PSQLException e) {
        log.debug("Получен статус 409 CONFLICT {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .message(e.getMessage())
                .reason("Request is CONFLICT")
                .build();
    }
}