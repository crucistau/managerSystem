package com.crucistau.commons;

import lombok.Data;
import org.springframework.stereotype.Controller;

/**
 * 设计表现层返回的数据，用于前后端数据的统一，也称前后端数据协议
 */
@Data
@Controller
public class R {
    private Boolean flag;//请求是否成功
    private Object data;//返回的数据
    private String msg;//消息提示
    private String code; //状态码
    public R() {
    }

    public R(Boolean flag) {
        this.flag = flag;
    }

    public R(Boolean flag, Object data) {
        this.flag = flag;
        this.data = data;
    }
    public R(Boolean flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }

    public R(Boolean flag, Object data, String msg){
        this.flag = flag;
        this.data = data;
        this.msg = msg;
    }

    public R(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    public R(Object data, String msg, String code) {
        this.data = data;
        this.msg = msg;
        this.code = code;
    }

    public R(Boolean flag, Object data, String msg, String code) {
        this.flag = flag;
        this.data = data;
        this.msg = msg;
        this.code = code;
    }
}
