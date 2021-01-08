package com.fl.control;

import com.fl.aop.annotation.Log;
import com.fl.entity.SystemManager;
import com.fl.model.clientReq.ReqUpdateSystem;
import com.fl.model.clientRes.ResData;
import com.fl.service.SystemManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "系统设置")
@RestController
public class SystemManagerController {

    @Autowired
    private SystemManagerService systemManagerService;


    @Log("user:updateSystem")
    @ApiOperation("更新系统值")
    @PostMapping(value = "/updateSystem",produces = "application/json;charset=UTF-8")
    public ResData updateSystem(@RequestBody ReqUpdateSystem reqUpdateSystem){
        ResData resData = new ResData();
        SystemManager systemManager = systemManagerService.selectByOne(1);
//        systemManagerService
        systemManager.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
        systemManager.setReadyMinioCapacity(reqUpdateSystem.getReadyMinioCapacity());

        systemManagerService.updateSystem(systemManager);

        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");
        return resData;
    }
    @ApiOperation("查询系统数据")
    @PostMapping("/selectSystem")
    public ResData selectSystem(){
        ResData resData = new ResData();

        SystemManager systemManager = systemManagerService.selectByOne(1);
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");

        return resData;
    }

}
