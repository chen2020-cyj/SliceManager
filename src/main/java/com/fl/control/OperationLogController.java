package com.fl.control;

import com.fl.aop.annotation.Log;
import com.fl.model.clientRes.ResData;
import com.fl.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "操作日志")
@RestController
public class OperationLogController {


    @Autowired
    private OperationLogService operationLogService;


    @PreAuthorize("@zz.check('user:log')")
    @Log("user:selectLog")
    @ApiOperation("查询操作日志")
    @PostMapping(value = "/selectLog",produces = "application/json;charset=UTF-8")
    public ResData selectLog(){
        ResData resData = new ResData();

        return resData;
    }
}
