package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.Files;
import com.jessie.campusmutualassist.mapper.FilesMapper;
import com.jessie.campusmutualassist.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service("filesService")
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files>
    implements FilesService{
    @Autowired
    FilesMapper filesMapper;
    @Override
    public void newFile(Files files) {
        filesMapper.newFile(files);
    }

    @Override
    @Cacheable(value = "filesByFid",key="#fid")
    public Files getFile(long fid) {
        return filesMapper.getFile(fid);
    }

    @Override
    @Cacheable(value = "classFiles",key="#classID")
    public List<Files> getClassFiles(String classID) {
        return filesMapper.getClassFiles(classID);
    }

    @Override
    @Cacheable(value = "filesByHash",key="#hash")//可以设置短些时间
    public Files getFilesByHash(String hash) {
        return filesMapper.getFilesByHash(hash);
    }


}



