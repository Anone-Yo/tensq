package com.tensquare.article.controller;

import com.tensquare.article.exception.MyException;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 拦截Controller抛出的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ExceptionHandler 声明要捕获的异常，只有controller抛出这个类型的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return new Result(false,StatusCode.ERROR,"发生异常了，请联系管理员");
    }

    @ExceptionHandler(MyException.class)
    public Result handleMyException(MyException e){
        e.printStackTrace();
        return new Result(false,StatusCode.ERROR,e.getLocalizedMessage());
    }
}
