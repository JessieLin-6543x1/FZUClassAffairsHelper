package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.entity.StuSelection;
import com.jessie.campusmutualassist.mapper.StuSelectionMapper;
import com.jessie.campusmutualassist.service.StuSelectionService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("stuSelectionService")
public class StuSelectionServiceImpl implements StuSelectionService {
    @Autowired
    StuSelectionMapper stuSelectionMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    @Cacheable(value = "StuSelections", key = "#stuName")
    public List<StuSelection> getStuSelections(String stuName) {
        return stuSelectionMapper.getStuSelections(stuName);
    }

    @Override
    @Async
    @CacheEvict(value = "StuSelections", key = "#stuSelection.stuName")
    public void newSelections(StuSelection stuSelection) {
        stuSelectionMapper.newSelections(stuSelection);
    }

    @Override
    public List<StuSelection> getClassSelections(String className) {
        return stuSelectionMapper.getClassSelections(className);
    }

    @Override
    @Cacheable(value = "classSelectors", key = "#classID")
    public List<String> getClassSelectStuName(String classID) {
        return stuSelectionMapper.getClassSelectStuName(classID);
    }

    @Override
    public void quitClass(String classID, String username) {
        redisUtil.sRemove("class:" + classID + ":" + "type:" + "members", username);
        stuSelectionMapper.quitClass(classID, username);
    }
}
