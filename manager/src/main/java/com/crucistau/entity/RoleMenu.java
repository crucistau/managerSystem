package com.crucistau.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_role_menu")
public class RoleMenu {
    private Integer roleId;
    private Integer menuId;
}
