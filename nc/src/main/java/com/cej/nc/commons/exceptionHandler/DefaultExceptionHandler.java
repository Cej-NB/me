package com.cej.nc.commons.exceptionHandler;

import com.cej.base.commons.exception.ExceptionCodeEnums;
import com.cej.base.commons.vo.ResResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.cej.nc.controller")
public class DefaultExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResResult handlerException(Exception e){
        log.error("全局异常" + e.getMessage());
        return new ResResult(ExceptionCodeEnums.DEFAULT_EXCEPTION.getCode(),"系统发生未知异常");
    }
}
