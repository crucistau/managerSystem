package com.crucistau.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-17
 */
@Getter
@Setter
@TableName("tb_manage")
public class Manage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String manName;

    /**
     * 密码
     */
    private String manPwd;

    /**
     *  身份
     */
    private String identify;

    /**
     *  学院
     */
    private String faculty;

    /**
     * 账号
     */
    private String manId;

    /**
     * 邮箱
     */
    private String email;


}
