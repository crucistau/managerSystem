package com.crucistau.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-05
 */
@Data
@TableName("tb_dict")
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;
    private String type;

}
