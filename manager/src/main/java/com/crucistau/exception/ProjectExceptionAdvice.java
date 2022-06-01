package com.crucistau.exception;

import com.crucistau.commons.Constants;
import com.crucistau.commons.R;
import com.crucistau.commons.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 作为SpringMVC的异常处理器
 * 1、先定义@ControllerAdvice  @RestControllerAdvice   后者有ResponseBody  看是否需要必须处理ResponseBody
 */
@ControllerAdvice
@RestControllerAdvice
public class ProjectExceptionAdvice {
    //表明拦截运行时的异常信息
    /**
     * 如果抛出的的是ServiceException，则调用该方法
     * @param ex 业务异常
     * @return Result
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result doServiceException(ServiceException ex){
        //记录日志  通知运维   通知开发
        ex.printStackTrace();//打印异常在控制台
        return Result.error(ex.getCode(), ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result doException(Exception ex){
        ex.printStackTrace();//打印异常在控制台
        return Result.error(Constants.CODE_600, "服务器错误");
    }


}
