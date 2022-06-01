package com.crucistau.controller.dto;


import lombok.Data;


@Data
public class FileDto {
    //上传学生信息
    private String studentName;
    private String studentId;
    //上传文件信息
    private String name;
    private String type;
}
