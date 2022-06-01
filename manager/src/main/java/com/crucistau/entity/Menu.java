package com.crucistau.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-04
 */
@Getter
@Setter
@TableName("tb_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String path;
    private String icon;
    private String description;
    private Integer pid;
    private String pagePath;

    @TableField(exist = false)
    private List<Menu> children;
}
