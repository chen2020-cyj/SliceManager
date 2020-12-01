package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.MinioInfo;
import com.fl.entity.TaskManager;
import com.fl.model.Msg;
import com.fl.model.clientRes.ReqSliceServer;
import com.fl.model.clientRes.ResData;
import com.fl.model.sliceServerRes.ResSegmentManager;
import com.fl.service.MinioInfoService;
import com.fl.service.TaskManagerService;
import com.fl.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "切片服务器接口")
@RestController
public class SliceController {


    @Autowired
    private TaskManagerService taskManagerService;
    @Autowired
    private MinioInfoService minioInfoService;


    private List<TaskManager> listTask = new ArrayList<>();
    private ResData res = new ResData();
    private ResSegmentManager resData = new ResSegmentManager();
    private Map<String,String> serverInfo = new HashMap<>();
    private Msg msg = new Msg();
    private ResSegmentManager resSegment = new ResSegmentManager();
    private Integer count = 0;
    @ApiOperation("分配任务")
    @PostMapping(value = "/distributionTask",produces = "application/json;charset=UTF-8")
    public ResSegmentManager distributionTask(){

        IPage<TaskManager> taskManagerIPage = taskManagerService.selectOneTask("0", 0, 1);
        TaskManager taskManager = taskManagerService.selectLinkFail(1002, 1001);
        if (taskManager != null){
            if (!taskManager.getResolvingPower().contains(",")){

                MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(taskManager.getMinioId()));

                Msg msg = GsonUtils.fromJson(minio.getMsg(), Msg.class);
                System.out.println(msg);
                serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));

//                taskManagerService.updateIdTask(taskManager.getId(),"10000");

                resSegment.setSubtitleUrl(taskManager.getSubtitleUrl());
                resSegment.setResolvingPower(taskManager.getResolvingPower()+"P");
                resSegment.setFilmId(taskManager.getFilmId());
                resSegment.setBtUrl(taskManager.getBtUrl());
                resSegment.setMsg(serverInfo);
                resSegment.setSubtitleSuffix(taskManager.getSubtitleSuffix());

                return resSegment;
            }else {
                String[] split = listTask.get(0).getMinioId().split(",");
                for (int i=0;i<split.length;i++){
                    MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(split[i]));
                    Msg msg = GsonUtils.fromJson(minio.getMsg(), Msg.class);
                    serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));
                }
                taskManagerService.updateIdTask(listTask.get(0).getId(),"10000");

                String[] split2 = taskManager.getResolvingPower().split(",");
                String str = "";
                for (int i=0;i<split2.length;i++){
                    if (str.equals("")){
                        str = split2[i]+"P";
                    }else {
                        str =str + "," +split2[i]+"P";
                    }
                }

                resSegment.setSubtitleUrl(taskManager.getSubtitleUrl());
                resSegment.setResolvingPower(str);
                resSegment.setFilmId(taskManager.getFilmId());
                resSegment.setBtUrl(taskManager.getBtUrl());
                resSegment.setMsg(serverInfo);
                resSegment.setSubtitleSuffix(taskManager.getSubtitleSuffix());
                res.setCode(0);
                res.setMsg("success");
                res.setData(resSegment);

                return resSegment;
            }
        }

        listTask = taskManagerIPage.getRecords();
        System.out.println(listTask);
        if (listTask.size()>0){
            if (!listTask.get(0).getResolvingPower().contains(",")){

                MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(listTask.get(0).getMinioId()));

                Msg msg = GsonUtils.fromJson(minio.getMsg(), Msg.class);
                System.out.println(msg);
                serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));

                taskManagerService.updateIdTask(listTask.get(0).getId(),"1");

                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setResolvingPower(listTask.get(0).getResolvingPower()+"P");
                resSegment.setFilmId(listTask.get(0).getFilmId());
                resSegment.setBtUrl(listTask.get(0).getBtUrl());
                resSegment.setSubtitleSuffix(listTask.get(0).getSubtitleSuffix());
                resSegment.setMsg(serverInfo);


                return resSegment;
            }else {
                String[] split = listTask.get(0).getMinioId().split(",");
                for (int i=0;i<split.length;i++){
                    MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(split[i]));
                    Msg msg = GsonUtils.fromJson(minio.getMsg(), Msg.class);
                    serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));
                }
                taskManagerService.updateIdTask(listTask.get(0).getId(),"1");

                String[] split2 = listTask.get(0).getResolvingPower().split(",");
                String str = "";
                for (int i=0;i<split2.length;i++){
                    if (str.equals("")){
                        str = split2[i]+"P";
                    }else {
                        str =str + "," +split2[i]+"P";
                    }
                }

                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setResolvingPower(str);
                resSegment.setFilmId(listTask.get(0).getFilmId());
                resSegment.setBtUrl(listTask.get(0).getBtUrl());
                resSegment.setMsg(serverInfo);
                resSegment.setSubtitleSuffix(listTask.get(0).getSubtitleSuffix());


                return resSegment;
            }
        }else {

            return null;
        }

    }
    @ApiOperation("任务轮询更新数据")
    @PostMapping("/taskState")
    public ResData taskComplete(@RequestBody ReqSliceServer reqSliceServer){

        Integer id = reqSliceServer.getCode();

        switch (id){
            case 2:
                TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());

                taskManagerService.updateIdTask(taskManager.getId(),String.valueOf(reqSliceServer.getCode()));
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
        }

        res.setCode(0);
        res.setMsg("ok");
        res.setData("");

        return res;
    }


}
