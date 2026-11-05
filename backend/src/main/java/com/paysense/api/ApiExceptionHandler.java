package com.paysense.api;

import com.paysense.api.ApiModels.ErrorResponse;
import com.paysense.domain.InsufficientBalanceException;
import com.paysense.service.RiskConfirmationRequiredException;
import com.paysense.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
 private ResponseEntity<ErrorResponse> error(HttpStatus status,String message,HttpServletRequest request){return ResponseEntity.status(status).body(new ErrorResponse(status.getReasonPhrase(),message,Instant.now(),request.getRequestURI()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e,HttpServletRequest r){String message=e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).distinct().collect(Collectors.joining(" "));return error(HttpStatus.BAD_REQUEST,message,r);}
 @ExceptionHandler({ConstraintViolationException.class,MethodArgumentTypeMismatchException.class,HttpMessageNotReadableException.class}) ResponseEntity<ErrorResponse> malformed(Exception e,HttpServletRequest r){return error(HttpStatus.BAD_REQUEST,"Request parameters or JSON body are invalid.",r);}
 @ExceptionHandler(AuthService.UnauthorizedException.class) ResponseEntity<ErrorResponse> unauthorized(RuntimeException e,HttpServletRequest r){return error(HttpStatus.UNAUTHORIZED,e.getMessage(),r);}
 @ExceptionHandler(InsufficientBalanceException.class) ResponseEntity<ErrorResponse> insufficient(InsufficientBalanceException e,HttpServletRequest r){return error(HttpStatus.UNPROCESSABLE_ENTITY,e.getMessage(),r);}
 @ExceptionHandler(RiskConfirmationRequiredException.class) ResponseEntity<ErrorResponse> riskConfirmation(RiskConfirmationRequiredException e,HttpServletRequest r){return error(HttpStatus.CONFLICT,e.getMessage(),r);}
 @ExceptionHandler(IllegalStateException.class) ResponseEntity<ErrorResponse> conflictState(IllegalStateException e,HttpServletRequest r){return error(HttpStatus.CONFLICT,e.getMessage(),r);}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException e,HttpServletRequest r){return error(HttpStatus.BAD_REQUEST,e.getMessage(),r);}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ErrorResponse> conflict(DataIntegrityViolationException e,HttpServletRequest r){return error(HttpStatus.CONFLICT,"This request conflicts with existing account or transaction data.",r);}
 @ExceptionHandler(Exception.class) ResponseEntity<ErrorResponse> internal(Exception e,HttpServletRequest r){return error(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred.",r);}
}
