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
 * @since 2022-04-11
 */
@Getter
@Setter
@TableName("tb_teacher")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String teaName;

    private String teaPwd;

    private String identity;

    private String teaId;

    private String email;

    private String faculty;


}
