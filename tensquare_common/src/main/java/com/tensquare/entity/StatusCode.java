package com.tensquare.entity;

/**
 * 返回状态码常量
 */
public interface StatusCode {
    static final int OK = 20000;//成功
    static final int ERROR = 20001;//失败
    static final int LOGINERROR = 20002;//用户名或密码错误
    static final int ACCESSERROR = 20003;//权限不足
    static final int REMOTEERROR = 20004;//远程调用失败
    static final int REPERROR = 20005;//重复操作
}
