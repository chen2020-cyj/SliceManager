package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.MinioInfo;
import com.fl.entity.TaskManager;
import com.fl.entity.User;
import com.fl.model.DbData;
import com.fl.model.MinioData;
import com.fl.model.Msg;
import com.fl.model.TaskState;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.ResAddSegment;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResTaskList;
import com.fl.service.MinioInfoService;
import com.fl.service.TaskManagerService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "任务管理接口")
@RestController
public class TaskController {
    @Autowired
    private TaskManagerService segmentService;
    @Autowired
    private  UserService userService;
    @Autowired
    private  MinioInfoService minioInfoService;


    private  ResData res = new ResData();
    private  TaskManager segment = new TaskManager();
    private  Map<String,String> map = new HashMap<>();
    private  MinioInfo minioInfo = new MinioInfo();
    private  ResAddSegment resAddSegment = new ResAddSegment();
    private  List<MinioData> listMinio = new ArrayList<>();
    private  ResFilmData resFilmData = new ResFilmData();
    private  List<TaskManager> taskList = new ArrayList<>();
    private  List<DbData> listDbData = new ArrayList<>();
    private  ResFilmData resTaskData = new ResFilmData();
    private  Msg msg = new Msg();

    @ApiOperation("客户端查看所有任务")
    @PostMapping(value = "/findMoreTask",produces = "application/json;charset=UTF-8")
    public String clientFindMoreTask(@RequestBody FindSegmentTask task) {
        Integer page = task.getPage();
        Integer offset = (task.getOffset())*task.getPage();

        IPage<TaskManager> segmentManagerIPage = segmentService.selectSegment(page-1, offset);
        User user = userService.selectUserInfo(task.getUserId());
        List<TaskManager> list = segmentManagerIPage.getRecords();
        System.out.println(list);
        long currentTime = System.currentTimeMillis() / 1000;
        long tokenTime = Long.valueOf(user.getTokenTime());
        System.out.println(currentTime);
        System.out.println(tokenTime);
        Integer userId = JwtUtils.verify(task.getToken());
        if (tokenTime > currentTime) {

            if (list.size() >0) {

                resFilmData.setCode(0);
                resFilmData.setMsg("success");
                resFilmData.setData(list);
                resFilmData.setTotal(segmentService.selectAllTask());
                return GsonUtils.toJson(resFilmData);
            } else {
                res.setCode(2);
                res.setMsg("数据错误");
                res.setData("");

                return GsonUtils.toJson(res);
            }

        } else {

            res.setCode(1);
            res.setMsg("token过期");
            res.setData("403");

            return GsonUtils.toJson(res);
        }

    }
    @ApiOperation("客户端查看单个任务")
    @PostMapping(value = "/findOneTask",produces = "application/json;charset=UTF-8")
    public String findOneTask(@RequestBody FindOneSegment findOneSegment){

        Integer userId = JwtUtils.verify(findOneSegment.getToken());
        User user = userService.selectUserInfo(userId);

        TaskManager taskManager = segmentService.selectSegmentFilmOne(findOneSegment.getFilmId());
        System.out.println(taskManager);
        long currentTime = System.currentTimeMillis()/1000;
        long tokenTime = Long.valueOf(user.getTokenTime());
        if (tokenTime > currentTime){
            if (taskManager != null){

                res.setCode(0);
                res.setMsg("success");
                res.setData(taskManager);

                return GsonUtils.toJson(res);
            }else {
                res.setCode(2);
                res.setMsg("err");
                res.setData("");

                return GsonUtils.toJson(res);
            }

        }else {
            res.setCode(1);
            res.setMsg("token过期");
            res.setData("403");

            return GsonUtils.toJson(res);
        }

    }

    @ApiOperation("客户端添加任务")
    @PostMapping(value="/addFilmSource",produces = "application/json;charset=UTF-8")
    public String add(@RequestBody AddSegmentQueue queue) {

        String filmId = getRandomString(10);
        Integer userInfo = JwtUtils.verify(queue.getToken());

        User user = userService.selectUserInfo(queue.getUserId());

        long currentTime = System.currentTimeMillis() / 1000;
        long tokenTime = Long.valueOf(user.getTokenTime());
        double filmSize= 0.0;
        if (queue.getFilmSize().contains("MB")){
            String mb = queue.getFilmSize().replace("MB", "");
            filmSize = Double.valueOf(mb)/1024;

        }else if (queue.getFilmSize().contains("GB")){
            String gb = queue.getFilmSize().replace("GB", "");
            filmSize = Double.valueOf(gb);
        }

        String resolvingPower = queue.getResolvingPower();
        double totalSize = 0.0;
        double availableSize = 0.0;
        if (tokenTime > currentTime) {

            map.put("film_name",queue.getFilmName());
            map.put("resolving_power",queue.getResolvingPower());
            List<TaskManager> taskManagers = segmentService.selectSegmentOne(map);

            if (taskManagers.size() == 0){
                //没有这个任务，进行添加
                segment.setFilmId(getRandomString(10));
                segment.setFilmName(queue.getFilmName());
                segment.setDownloadState("0");
                segment.setResolvingPower(queue.getResolvingPower());
                segment.setBtUrl(queue.getBtUrl());
                segment.setSubtitleUrl(queue.getSubtitleUrl());
                segment.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
                segment.setUploadState("");
                segment.setLinkState("");
                segment.setUploadState("");
                segment.setSegmentState("");
                segment.setSubtitleSuffix(queue.getSubtitleSuffix());
//                segment.setData();

                if (!queue.getResolvingPower().contains(",")){
                    List<MinioInfo> minioInfos = minioInfoService.selectMoreMinio(resolvingPower);
                    for (int i=0;i<minioInfos.size();i++){
                       totalSize = minioInfos.get(i).getTotalCapacity();
                       availableSize = minioInfos.get(i).getAvailableCapacity();

                       if (filmSize < availableSize){
                           segment.setMinioId(String.valueOf(minioInfos.get(i).getId()));



                           minioInfo.setResolvingPower(queue.getResolvingPower());
                           minioInfo.setArea(minioInfos.get(i).getArea());
                           minioInfo.setTotalCapacity(totalSize);
                           minioInfo.setAvailableCapacity(availableSize-filmSize);
                           minioInfo.setMsg(minioInfos.get(i).getMsg());
                           minioInfo.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));


                           res.setCode(0);
                           res.setMsg("success");
                           res.setData("");
                           break;
                       }else {
                           resAddSegment.setResolvingPower(queue.getResolvingPower());
                           res.setCode(1);
                           res.setMsg("success");
                           res.setData(resAddSegment);
                       }
                    }
                    segmentService.insertSegment(segment);
                    minioInfoService.updateMinio(minioInfo,queue.getResolvingPower());
                    listMinio.clear();
                    map.clear();
                    return GsonUtils.toJson(res);
                }else {

                    String[] split = queue.getResolvingPower().split(",");

                    String MinioId = "";
                    String notMinioId = "";
                    String resolving = "";

                    for (int i = 0; i < split.length; i++) {
                        List<MinioInfo> minio = minioInfoService.findMinio(split[i]);
                        List<DbData> listDb = new ArrayList<>();
                        msg = GsonUtils.fromJson(minio.get(0).getMsg(), Msg.class);


                            totalSize = minio.get(0).getTotalCapacity();
                            availableSize = minio.get(0).getAvailableCapacity();


                            if (filmSize < availableSize){
                                if (MinioId.equals("")){
                                    MinioId = String.valueOf(minio.get(0).getId());
                                }else{
                                    MinioId = MinioId+","+String.valueOf(minio.get(0).getId());
                                }

                                minioInfo.setResolvingPower(minio.get(0).getResolvingPower());
                                minioInfo.setArea(minio.get(0).getArea());
                                minioInfo.setTotalCapacity(totalSize);
                                minioInfo.setAvailableCapacity(availableSize-filmSize);
                                minioInfo.setMsg(minio.get(0).getMsg());
                                minioInfo.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));

                                minioInfoService.updateMinio(minioInfo,split[i]);
                                minio.clear();
                            }else {
                                if (notMinioId.equals("")){
                                    notMinioId = split[i];
                                }else {
                                    notMinioId = notMinioId+","+split[i];
                                }
                                resAddSegment.setResolvingPower(notMinioId);

                                minio.clear();
                            }
                    }
                    segment.setMinioId(MinioId);
                    segment.setFilmId(getRandomString(10));
                    segment.setFilmName(queue.getFilmName());
                    segment.setDownloadState("0");
                    segment.setResolvingPower(queue.getResolvingPower());
                    segment.setBtUrl(queue.getBtUrl());
                    segment.setSubtitleUrl(queue.getSubtitleUrl());
                    segment.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
                    segment.setUploadState("");
                    segment.setLinkState("");
                    segment.setUploadState("");
                    segment.setSegmentState("");
                    segment.setSubtitleSuffix(queue.getSubtitleSuffix());

                    segmentService.insertSegment(segment);
                    res.setCode(6);
                    res.setMsg("返回分辨率，如有有数据那么就表示该分辨率存储桶已满");
                    res.setData(resAddSegment);
                    map.clear();
                    listMinio.clear();
                    listDbData.clear();
                    return  GsonUtils.toJson(res);
                }
            }else {
                res.setCode(3);
                res.setMsg("任务存在");
                res.setData("");

                return GsonUtils.toJson(res);
            }

        } else {

            res.setCode(2);
            res.setMsg("token过期");
            res.setData("403");

            return GsonUtils.toJson(res);
        }
    }

    @ApiOperation("客户端查看状态，传入不同的状态获得不同状态的数据")
    @PostMapping(value = "/findQueueAnnounceState",produces = "application/json;charset=UTF-8")
    public String findQueueState(@RequestBody FindAnnounce announce){

        Integer page = announce.getPage();
        Integer offset = (announce.getOffset())*announce.getPage();

        IPage<TaskManager> segmentManager = segmentService.selectState(page-1,offset,announce.getState());
        List<TaskManager> list = segmentManager.getRecords();

        User user = userService.selectUserInfo(announce.getUserId());
        Long currentTime = System.currentTimeMillis()/1000;
        Long tokenTime =Long.valueOf(user.getTokenTime());

        if (tokenTime > currentTime){
            if (list.size() > 0){

                resFilmData.setCode(0);
                resFilmData.setMsg("err");
                resFilmData.setData(list);
                resFilmData.setTotal(segmentService.selectStateCount(announce.getState()));
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
            res.setData("403");

            return GsonUtils.toJson(res);
        }
    }
    @ApiOperation("客户端删除任务")
    @PostMapping(value = "/delTask",produces = "application/json;charset=UTF-8")
    public ResData delTask(@RequestBody FindOneSegment findOneSegment){

        User user = userService.selectUserInfo(findOneSegment.getUserId());
        long currentTime = System.currentTimeMillis()/1000;
        long tokenTime = Long.valueOf(user.getTokenTime());
        if (tokenTime > currentTime){
            segmentService.delTask(findOneSegment.getFilmId());
            res.setCode(0);
            res.setMsg("success");
            res.setData("");

            return res;
        }else {
            res.setCode(1);
            res.setMsg("token过期");
            res.setData("");

            return  res;
        }
    }
    @ApiOperation("客户端更新失败的任务")
    @PostMapping(value = "/updateTask",produces = "application/json;charset=UTF-8")
    public ResData updateTask(@RequestBody UpdateTask updateTask){

        User user = userService.selectUserInfo(updateTask.getUserId());
        long currentTime = System.currentTimeMillis()/1000;
        long tokenTime = Long.valueOf(user.getTokenTime());
        if (tokenTime > currentTime){
            String s = segmentService.updateFailUrl(updateTask.getFilmId(), updateTask.getBtUrl(), updateTask.getSubtitleUrl());
            if (s.equals("success")){
                segmentService.updateIdTask(updateTask.getFilmId(),"0");

                res.setCode(0);
                res.setMsg("success");
                res.setData("");
                return res;
            }else {
                res.setCode(2);
                res.setMsg("不能输入空数据");
                res.setData("");
                return res;
            }
        }else {
            res.setCode(1);
            res.setMsg("token过期");
            res.setData("");

            return res;
        }
    }
    @ApiOperation("客户端查看切片失败的任务")
    @PostMapping(value = "/findFailTask",produces = "application/json;charset=UTF-8")
    public ResFilmData findFailTask(@RequestBody FindFailTask findFailTask){

        User user = userService.selectUserInfo(findFailTask.getUserId());
        long currentTime = System.currentTimeMillis()/1000;
        long tokenTime = Long.valueOf(user.getTokenTime());

        Integer page = findFailTask.getPage();
        Integer offset = (findFailTask.getOffset())*findFailTask.getPage();
        IPage<TaskManager> taskManagerIPage = segmentService.selectSegment(page - 1, offset);

        List<TaskManager> list = taskManagerIPage.getRecords();
        List<String> fail = new ArrayList<>();

        String str = "";
        boolean state = false;
        if (tokenTime > currentTime){
            for (int i=0;i<list.size();i++){
                TaskManager task = new TaskManager();
                String segmentState = list.get(i).getSegmentState();
                if (!segmentState.equals("")){
                    if (!segmentState.contains(",")){
                        if (segmentState.equals("2021")){
                            state = true;
                        }else if (segmentState.equals("2022")){
                            state = true;
                        }else if (segmentState.equals("2023")){
                            state = true;
                        }else {

                        }
                        if (state == true){
                            taskList.add(list.get(i));
                        }
                        state = false;
                    }else {
                        String[] split = segmentState.split(",");
                        for (int j=0;j<split.length;j++){
                            if (split[j].equals("2021")){
                                state = true;
                            }else if (split[j].equals("2022")){
                                state = true;
                            }else if (split[j].equals("2023")){
                                state = true;
                            }else {

                            }
                        }
                        if (state == true){
                            taskList.add(list.get(i));
                        }
                        state = false;
                    }
                }else {
                    continue;
                }
            }
            resTaskData.setCode(0);
            resTaskData.setMsg("返回所有切片失败的任务");
            resTaskData.setData(taskList);
            resTaskData.setTotal(taskList.size());
            return resTaskData;
        }else {
            resTaskData.setCode(1);
            resTaskData.setMsg("token过期");
            resTaskData.setData("");
            resTaskData.setTotal(0);
            return resTaskData;
        }
    }
    @ApiOperation("客户端任务测试")
    @PostMapping("/clientTaskTest")
    public ResData taskTest(String str){

        System.out.println(str);
        res.setCode(0);
        res.setMsg("success");
        res.setData("");
        return res;
    }

    @PostMapping("/test")
    public ResData taskTest(@RequestBody ResData res){

        System.out.println(res);

        return null;
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
