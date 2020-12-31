package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.NginxManager;
import com.fl.mapper.NginxManagerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NginxManagerService extends ServiceImpl<NginxManagerMapper, NginxManager> {

    @Autowired
    private NginxManagerMapper nginxManagerMapper;


    /**
     * 插入新的数据
     */
    public void insertNginxUrl(NginxManager nginxManager){
        nginxManagerMapper.insert(nginxManager);
    }
    /**
     * 根据minio_id查找
     */
    public NginxManager selectByMinioId(Integer id){
        QueryWrapper<NginxManager> wrapper = new QueryWrapper<>();
        wrapper.eq("minio_id",id);

        return nginxManagerMapper.selectOne(wrapper);
    }
}
