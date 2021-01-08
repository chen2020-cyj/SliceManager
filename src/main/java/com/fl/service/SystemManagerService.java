package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.SystemManager;
import com.fl.mapper.SystemManagerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemManagerService extends ServiceImpl<SystemManagerMapper, SystemManager> {
    @Autowired
    private SystemManagerMapper systemManagerMapper;

    /**
     * 更新操作
     */
    public void updateSystem(SystemManager systemManager){
        systemManagerMapper.updateById(systemManager);
    }
    /**
     * 系统设置数据只有一条
     */
    public SystemManager selectByOne(Integer id){
        QueryWrapper<SystemManager> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return systemManagerMapper.selectOne(wrapper);
    }
}
