package com.crucistau.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.FileDto;
import com.crucistau.entity.Document;


public interface IDocService extends IService<Document> {

    Result getDocWithSubId(String subId);
}
