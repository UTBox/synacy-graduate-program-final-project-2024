package com.synacy.graduate.program.leaveapp.leave_management.web.apierror;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFoundException.class})
    public ApiErrorResponse handleResourceNotFoundException() {
        return new ApiErrorResponse("RESOURCE_NOT_FOUND", "The target resource does not exist");
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidOperationException.class)
    public ApiErrorResponse handleInvalidOperationException(InvalidOperationException e) {
        return new ApiErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestException.class)
    public ApiErrorResponse handleInvalidRequestException(InvalidRequestException e) {
        return new ApiErrorResponse("INVALID_REQUEST", e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ApiErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException e) {

        StringBuilder message = new StringBuilder();

        for( Object error : Objects.requireNonNull(e.getDetailMessageArguments())) {
            message.append(error.toString()).append(";");
        }

        return new ApiErrorResponse("INVALID_REQUEST_PARAMETER", message.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();

        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();

        for( ObjectError error : allErrors ) {
            message.append(error.getDefaultMessage()).append(";");
        }

        return new ApiErrorResponse("INCOMPLETE_REQUEST", message.toString());
    }
}
