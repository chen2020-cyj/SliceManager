package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.MinioInfo;
import com.fl.entity.User;
import com.fl.model.Msg;
import com.fl.model.clientReq.AddMinio;
import com.fl.model.clientReq.FindAllMinio;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResMinio;
import com.fl.service.MinioInfoService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "存储桶管理接口")
@RestController
public class MinioController {


    @Autowired
    private MinioInfoService minioInfoService;

    @Autowired
    private UserService userService;

    private MinioInfo minioInfo = new MinioInfo();
    private ResData res = new ResData();
    private List<MinioInfo> listMinioInfo = new ArrayList<>();
    private ResMinio resMinio = new ResMinio();
    private ResFilmData resMinioData = new ResFilmData();
    private Msg msg = new Msg();
    @ApiOperation("添加一个存储桶")
    @PostMapping(value = "/addMinio",produces = "application/json;charset=UTF-8")
    public String addMinio(@RequestBody AddMinio addMinio){

        User user = userService.selectUserInfo(addMinio.getUserId());
        long currentTime = System.currentTimeMillis()/1000;
        long tokenTime = Long.valueOf(user.getTokenTime());
        msg.setMsg(addMinio.getData());
        if (user != null){
            if (tokenTime > currentTime){
                minioInfo.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
                minioInfo.setResolvingPower(addMinio.getResolvingPower());
                minioInfo.setMsg(GsonUtils.toJson(msg));
                minioInfo.setTotalCapacity(addMinio.getTotalCapacity());
                minioInfo.setArea(addMinio.getArea());

                minioInfoService.insertMinio(minioInfo);

                res.setCode(0);
                res.setMsg("success");
                res.setData("");

                return GsonUtils.toJson(res);

            }else {
                res.setCode(1);
                res.setMsg("err");
                res.setData("");

                return GsonUtils.toJson(res);
            }
        }else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("");

            return GsonUtils.toJson(res);
        }

    }

    @ApiOperation("查询多个存储桶")
    @PostMapping(value = "/selectAllMinio",produces = "application/json;charset=UTF-8")
    public String selectAllMinio(@RequestBody FindAllMinio findAllMinio){


        Integer page = findAllMinio.getPage();
        Integer offset = findAllMinio.getOffset()*page;
        User user = userService.selectUserInfo(findAllMinio.getUserId());

        long currentTime = System.currentTimeMillis()/1000;
        long tokenTime = Long.valueOf(user.getTokenTime());

        if (user != null){
            if (tokenTime > currentTime){
                IPage<MinioInfo> minioInfoIPage = minioInfoService.selectAllMinio(findAllMinio.getResolvingPower(), page-1, offset);
                listMinioInfo = minioInfoIPage.getRecords();
                resMinio.setList(listMinioInfo);
                resMinio.setTotal(minioInfoService.selectCount());


                resMinioData.setCode(0);
                resMinioData.setMsg("success");
                resMinioData.setData(listMinioInfo);
                resMinioData.setTotal(minioInfoService.selectCount());

                return GsonUtils.toJson(res);

            }else {
                res.setCode(1);
                res.setMsg("token过期");
                res.setData("403");

                return GsonUtils.toJson(res);
            }
        }else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("");

            return GsonUtils.toJson(res);
        }
    }
}
