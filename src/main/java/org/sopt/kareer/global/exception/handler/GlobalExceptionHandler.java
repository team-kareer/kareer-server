package org.sopt.kareer.global.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.global.exception.customexception.CustomException;
import org.sopt.kareer.global.response.BaseErrorResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

import static org.sopt.kareer.global.exception.errorcode.GlobalErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseErrorResponse handleInternalServerError(Exception e) {
        log.error(e.getMessage(), e);
        return BaseErrorResponse.of(INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handlerMethodValidationException(HandlerMethodValidationException e){
        String message = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining());

        return BaseErrorResponse.of(INVALID_REQUEST, message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseErrorResponse handleNoHandlerFoundException(NoHandlerFoundException e) {
        return BaseErrorResponse.of(NOT_FOUND_PATH);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseErrorResponse handleMethodNotAllowedException(MethodNotAllowedException e) {
        return BaseErrorResponse.of(METHOD_NOT_ALLOWED);
    }

    //커스텀 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseErrorResponse> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(BaseErrorResponse.of(
                        e.getErrorCode(),
                        e.getMessageDetail()));
    }

    //입력값 검증할 때 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElseGet(() -> ex.getBindingResult().getGlobalErrors()
                        .stream()
                        .findFirst()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElse("입력값이 잘못되었습니다.")
                );

        return BaseErrorResponse.of(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    // JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return BaseErrorResponse.of(INVALID_JSON);
    }

    // 요청 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        return BaseErrorResponse.of(MISSING_REQUEST_PARAMETER);
    }

    // 타입 불일치
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handleTypeMismatch(TypeMismatchException e) {
        return BaseErrorResponse.of(TYPE_MISMATCH);
    }

    // 경로 변수 누락
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handleMissingPathVariable(MissingPathVariableException e) {
        return BaseErrorResponse.of(MISSING_PATH_VARIABLE);
    }

}
