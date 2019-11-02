package com.tensquare.article.exception;

/**
 * 终止已经不符合业务逻辑操作的继续执行
 */
public class MyException extends RuntimeException {
    public MyException(String message){
        super(message);
    }
}
