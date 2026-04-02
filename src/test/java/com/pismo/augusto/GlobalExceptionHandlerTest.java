package com.pismo.augusto;

import com.pismo.augusto.common.config.GlobalExceptionHandler;
import com.pismo.augusto.common.exception.ErrorDetail;
import com.pismo.augusto.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@Tag("test-unit")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private WebRequest webRequest;
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        httpRequest = mock(HttpServletRequest.class);
    }

    @Test
    void testHandleNotFoundExceptionReturnsNotFound() {
        UUID test = UUID.randomUUID();
        NotFoundException exception = new NotFoundException(test);

        ResponseEntity<?> response = globalExceptionHandler.handleNotFoundException(exception, webRequest, httpRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorDetail.class, response.getBody());
        ErrorDetail errorDetail = (ErrorDetail) response.getBody();
        assertNotNull(errorDetail);
        assertEquals("Entity %s not found".formatted(test), errorDetail.getMessage());
    }

    @Test
    void testHandleNotFoundExceptionWithCustomMessage() {
        NotFoundException exception = new NotFoundException("Resource with ID 123 not found");

        ResponseEntity<?> response = globalExceptionHandler.handleNotFoundException(exception, webRequest, httpRequest);

        ErrorDetail errorDetail = (ErrorDetail) response.getBody();
        assertNotNull(errorDetail);
        assertTrue(errorDetail.getMessage().contains("123"));
    }

    @Test
    void testHandleGenericExceptionReturnsInternalServerError() {
        Exception exception = new Exception("Internal server error");

        ResponseEntity<?> response = globalExceptionHandler.handleGlobalException(exception, webRequest, httpRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(ErrorDetail.class, response.getBody());
        ErrorDetail errorDetail = (ErrorDetail) response.getBody();
        assertNotNull(errorDetail);
    }

    @Test
    void testHandleGenericExceptionWithStackTrace() {
        RuntimeException exception = new RuntimeException("Database connection failed");

        ResponseEntity<?> response = globalExceptionHandler.handleGlobalException(exception, webRequest, httpRequest);

        ErrorDetail errorDetail = (ErrorDetail) response.getBody();
        assertNotNull(errorDetail);
        assertNotNull(errorDetail.getTimestamp());
    }

    @Test
    void testErrorDetailContainsTimestamp() {
        NotFoundException exception = new NotFoundException("Not found");

        ResponseEntity<?> response = globalExceptionHandler.handleNotFoundException(exception, webRequest, httpRequest);
        ErrorDetail errorDetail = (ErrorDetail) response.getBody();

        assertNotNull(errorDetail);
        assertNotNull(errorDetail.getTimestamp());
    }

    @Test
    void testErrorDetailContainsStatus() {
        NotFoundException exception = new NotFoundException("Not found");

        ResponseEntity<?> response = globalExceptionHandler.handleNotFoundException(exception, webRequest, httpRequest);
        ErrorDetail errorDetail = (ErrorDetail) response.getBody();

        assertNotNull(errorDetail);
        assertEquals(404, errorDetail.getStatus());
    }
}
