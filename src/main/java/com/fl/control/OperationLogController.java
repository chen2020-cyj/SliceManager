package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.aop.annotation.Log;
import com.fl.entity.OperationLog;
import com.fl.entity.User;
import com.fl.model.clientReq.ReqLog;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResLog;
import com.fl.service.OperationLogService;
import com.fl.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "操作日志")
@RestController
public class OperationLogController {


    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private UserService userService;


    @PreAuthorize("@zz.check('user:log')")
    @Log("user:selectLog")
    @ApiOperation("查询操作日志")
    @PostMapping(value = "/selectLog",produces = "application/json;charset=UTF-8")
    public ResFilmData selectLog(@RequestBody ReqLog reqLog){
        ResFilmData resData = new ResFilmData();

        IPage<OperationLog> iPage = operationLogService.selectMore(reqLog.getOffset(), reqLog.getPage());
        List<OperationLog> list = iPage.getRecords();
        List<ResLog> logList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            ResLog resLog = new ResLog();
            User user = userService.selectUserInfo(Integer.valueOf(String.valueOf(list.get(i).getUserId())));

            resLog.setUserId(user.getId());
            resLog.setMsg(list.get(i).getMsg());
            resLog.setName(user.getName());
            resLog.setCreateTime(list.get(i).getCreateTime());
            logList.add(resLog);
        }
        Integer logCount = operationLogService.logCount();
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData(logList);
        resData.setTotal(logCount);

        return resData;
    }
}
