package com.crucistau.commons;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一返回包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {


    private String code;
    private String msg;
    private Object data;


    public static Result success() {
        return new Result(Constants.CODE_200, "", null);
    }

    public static Result success(Object data) {
        return new Result(Constants.CODE_200, "", data);
    }

    public static Result success(Object data, String msg) {
        return new Result(Constants.CODE_200, msg, data);
    }

    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }


    public static Result info(Boolean flag, Object data, String msg){
        if (flag){
                return new Result(Constants.CODE_200, msg, data);
        }
        return error();
    }


     public static Result info (Boolean flag, String msg){
        if (flag){
            return Result.success(null, msg);
        }
        return  Result.error();
     }

    public static Result error() {
        return new Result(Constants.CODE_500, "系统错误", null);
    }

}
