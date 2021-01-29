package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.OperationLog;
import com.fl.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {
    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 插入数据
     */
    public void logInsert(OperationLog operationLog){
        operationLogMapper.insert(operationLog);
    }

    public IPage<OperationLog> selectMore(Integer offset,Integer page){

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("''","");

        IPage<OperationLog> iPage = new Page<>(page,offset);
        return operationLogMapper.selectPage(iPage,wrapper);

    }
    public Integer logCount(){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("''","");

        return operationLogMapper.selectCount(wrapper);

    }
}
