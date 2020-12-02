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


        listTask = taskManagerIPage.getRecords();
        System.out.println(listTask);
        if (listTask.size()>0){
            if (!listTask.get(0).getResolvingPower().contains(",")){

                MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(listTask.get(0).getMinioId()));

                Msg msg = GsonUtils.fromJson(minio.getMsg(), Msg.class);
                System.out.println(msg);
                serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));

                taskManagerService.updateIdTask(listTask.get(0).getFilmId(),"0");

//                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setSubtitleUrl("");
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
                taskManagerService.updateIdTask(listTask.get(0).getFilmId(),"0");

                String[] split2 = listTask.get(0).getResolvingPower().split(",");
                String str = "";
                for (int i=0;i<split2.length;i++){
                    if (str.equals("")){
                        str = split2[i]+"P";
                    }else {
                        str =str + "," +split2[i]+"P";
                    }
                }

//                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setSubtitleUrl("");
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
        System.out.println(reqSliceServer);
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        String filmId = taskManager.getFilmId();
        String state = String.valueOf(reqSliceServer.getCode());
        switch (id){
            case 2:
                //正在下载bt种子


                taskManagerService.updateIdTask(filmId,state);
                break;
            case 3:
                //bt种子下载完成
                taskManagerService.updateIdTask(filmId,state);

                break;
            case 1001:

                taskManagerService.updateIdSegmentState(filmId,state);
                //种子链接无效
                break;
            case 1002:
                //字幕链接无效
                taskManagerService.updateIdSegmentState(filmId,state);
                break;
            case 2011:

                //720切片完成
                break;
            case 2012:
                //480切片完成
                break;
            case 2013:
                //320切片完成
                break;
            case 2021:
                //720切片失败
                break;
            case 2022:
                //480切片失败
                break;
            case 2023:
                //320切片失败
                break;
            case 6003:
                //720上传状态
                break;
            case 6004:
                //480上传状态
                break;
            case 6005:
                //320上传状态
                break;
        }

        res.setCode(0);
        res.setMsg("ok");
        res.setData("");

        return res;
    }


}
