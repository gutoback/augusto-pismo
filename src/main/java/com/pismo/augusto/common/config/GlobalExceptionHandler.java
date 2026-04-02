package com.pismo.augusto.common.config;

import com.pismo.augusto.common.exception.NotFoundException;
import com.pismo.augusto.common.exception.ErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetail> handleNotFoundException(NotFoundException ex, WebRequest request, HttpServletRequest httpRequest) {
        logger.error(ex.getMessage(), ex);
        ErrorDetail message = new ErrorDetail(HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(), httpRequest.getRequestURI(), httpRequest.getSession().getId());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataIntegrityViolationException.class,ConstraintViolationException.class,
            MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDetail> handleBadRequest(Exception ex, WebRequest request, HttpServletRequest httpRequest) {
        logger.error(ex.getMessage(), ex);
        ErrorDetail message = new ErrorDetail(HttpStatus.BAD_REQUEST.value(), new Date(), ex.getMessage(), httpRequest.getRequestURI(), httpRequest.getSession().getId());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> handleGlobalException(Exception ex, WebRequest request, HttpServletRequest httpRequest) {
        logger.error(ex.getMessage(), ex);
        ErrorDetail message = new ErrorDetail(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), ex.getMessage(), httpRequest.getRequestURI(), httpRequest.getSession().getId());
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
