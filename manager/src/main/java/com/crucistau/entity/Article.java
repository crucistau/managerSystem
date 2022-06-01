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
 * @since 2022-04-20
 */
@Getter
@Setter
@TableName("tb_article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布时间
     */
    private String time;


}
