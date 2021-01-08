package com.fl.service;

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
}
