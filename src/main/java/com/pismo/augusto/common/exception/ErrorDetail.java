package com.pismo.augusto.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetail {
    private int status;
    private Date timestamp;
    private String message;
    private String path;
    private String detail;
}