package com.crucistau.controller.dto;

import com.crucistau.entity.Menu;
import com.crucistau.entity.Student;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Student student;
    private String password;
    private String userName;//用户名
    private String userId;//用户ID（学号）
    private String faculty;//用户对应的学院
    private String identity;//身份标识符
    private String mail;//email
    private List<Menu> menus;//所拥有的菜单
    private String token;//自带token
}
