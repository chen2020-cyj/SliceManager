package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.aop.annotation.Log;
import com.fl.entity.*;
import com.fl.model.*;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.ResAddSegment;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResTaskList;

import com.fl.service.*;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
import com.fl.utils.OkHttpUtils;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "任务管理接口")
@RestController
public class TaskController {
    @Autowired
    private TaskManagerService segmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private MinioInfoService minioInfoService;
    @Autowired
    private FilmInfoService filmInfoService;
    @Autowired
    private LanguageInfoService languageInfoService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private BtDownLoadService btDownLoadService;



    private ResData res = new ResData();
    private Gson gson = new Gson();
    private MinioInfo minioInfo = new MinioInfo();
//    private  ResAddSegment resAddSegment = new ResAddSegment();

    private ResFilmData resFilmData = new ResFilmData();

    private ResFilmData resTaskData = new ResFilmData();
    private Msg msg = new Msg();
    @Log("user:findMoreTask")
    @ApiOperation("客户端查看所有任务")
    @PostMapping(value = "/findMoreTask", produces = "application/json;charset=UTF-8")
    public String clientFindMoreTask(@RequestBody FindSegmentTask task) {
//        Integer page = (task.getPage() - 1) * task.getOffset();
//        Integer offset = task.getOffset();
        Integer id = 0;

        if (!task.getFilmId().equals("")){

            TaskManager taskManager = segmentService.selectByFilmId(task.getFilmId());

            if (!taskManager.getSegmentState().equals("")){
                SegmentState segmentState = gson.fromJson(String.valueOf(taskManager.getSegmentState()), SegmentState.class);
                taskManager.setSegmentState(segmentState);
            }
            if (!taskManager.getUploadState().equals("")){
                SegmentUploadState segmentUploadState = gson.fromJson(String.valueOf(taskManager.getUploadState()), SegmentUploadState.class);
                taskManager.setUploadState(segmentUploadState);
            }

            if (taskManager.getDownloadState().equals("2")){
                BtDownLoad bt = btDownLoadService.selectByFilmId(taskManager.getFilmId());
                taskManager.setDownloadState(bt.getBtState());
            }else if (taskManager.getDownloadState().equals("0")){
                taskManager.setDownloadState("待分配");
            }else if (taskManager.getDownloadState().equals("1")){
                taskManager.setDownloadState("已分配");
            }else if (taskManager.getDownloadState().equals("3")){
                taskManager.setDownloadState("下载完成");
            }

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(taskManager);
            resFilmData.setTotal(1);

            return  GsonUtils.toJson(resFilmData);
        }else {
            if (task.getSegmentState().equals("0")) {
                //查看所有的任务
                Integer integer = idChange(id, task);
                ResTaskInfoMapper resTaskInfoMapper = segmentService.selectAllTaskManager(integer, task);
                List<TaskManager> managerList = resTaskInfoMapper.getIPage().getRecords();

                List<TaskManager> managerList1 = changeTaskManager(managerList);

                for (int i = 0; i < managerList1.size(); i++) {
                    if (managerList1.get(i).getDownloadState().equals("2")){
                        BtDownLoad bt = btDownLoadService.selectByFilmId(managerList1.get(i).getFilmId());
                        managerList1.get(i).setDownloadState(bt.getBtState());
                    }else if (managerList1.get(i).getDownloadState().equals("0")){
                        managerList1.get(i).setDownloadState("待分配");
                    }else if (managerList1.get(i).getDownloadState().equals("1")){
                        managerList1.get(i).setDownloadState("已分配");
                    }else if (managerList1.get(i).getDownloadState().equals("3")){
                        managerList1.get(i).setDownloadState("下载完成");
                    }
                }

                resFilmData.setCode(0);
                resFilmData.setMsg("success");
                resFilmData.setData(managerList1);
                resFilmData.setTotal(resTaskInfoMapper.getCount());

                return GsonUtils.toJson(resFilmData);
            } else {
                //查看失败的任务
                List<TaskManager> taskManagerList = new ArrayList<>();
                List<TaskManager> managerList = segmentService.selectAllSegment();
                Integer integer = idChange(id, task);
                for (int i=0;i<managerList.size();i++){
                    SegmentState segmentState = GsonUtils.fromJson(String.valueOf(managerList.get(i).getSegmentState()), SegmentState.class);
                    if (segmentState != null){
                        if (!segmentState.getSegmentFail().equals("")){
                            taskManagerList.add(managerList.get(i));
//                            if (task.getDownloadState().equals("")){
//
//                            }else {
//                                if (managerList.get(i).getDownloadState().equals(task.getDownloadState())){
//                                    taskManagerList.add(managerList.get(i));
//                                }
//                            }

                        }
                    }
                }
                List<TaskManager> list = selectAllFailTask(integer, taskManagerList, task);
                List<TaskManager> allFailTask = changeTaskManager(list);

                for (int i = 0; i < allFailTask.size(); i++) {
                    if (allFailTask.get(i).getDownloadState().equals("3")){
                        allFailTask.get(i).setDownloadState("下载完成");
                    }
                }

                Integer indexPage = (task.getPage()-1)*task.getOffset();
                Integer offset = task.getOffset()*task.getPage();

                resFilmData.setCode(0);
                resFilmData.setMsg("success");
                if (indexPage > allFailTask.size()){
                    resFilmData.setData(allFailTask.subList(allFailTask.size(),allFailTask.size()));
                }else if (offset > allFailTask.size()){
                    resFilmData.setData(allFailTask.subList(indexPage,allFailTask.size()));
                }else {
                    resFilmData.setData(allFailTask.subList(indexPage,offset));
                }

                resFilmData.setTotal(taskManagerList.size());
                return  GsonUtils.toJson(resFilmData);
            }

        }


    }

    /**
     * 判断传入的参数
     * @param id
     * @param task
     * @return
     */
    private Integer idChange(Integer id,FindSegmentTask task){
        if (task.getDownloadState().equals("")){
            if (task.getLinkState().equals("")){
                //downloadState 不传 linkState 不传
                id = 1;
            }else {
                // downloadState 不传 linkState 传
                id = 2;
            }
        }else {
            if (task.getLinkState().equals("")){
                //downloadState 传 linkState 不传
                id = 3;
            }else {
                // downloadState 传 linkState 传
                id = 4;
            }
        }
        return  id;
    }
    /**
     * 查询所有失败的任务
     */
    private List<TaskManager> selectAllFailTask(Integer id,List<TaskManager> managerList,FindSegmentTask task){
        List<TaskManager> list = new ArrayList<>();
        switch (id){
            case 1:
                //downloadState不传 linkState不传 id = 1
                for (int i=0;i<managerList.size();i++){
                    if (list.get(i).getDeleteFlag().equals("0")){
                        list.add(managerList.get(i));
                    }
                }
                break;
            case 2:
                //downloadState不传 linkState传 id = 2
                for (int i=0;i<managerList.size();i++){
                    if(managerList.get(i).getLinkState().equals(task.getLinkState())){
                        if (list.get(i).getDeleteFlag().equals("0")){
                            list.add(managerList.get(i));
                        }
                    }
                }
                break;
            case 3:
                //downloadState传 linkState不传 id = 3
                for (int i=0;i<managerList.size();i++){
                    if(managerList.get(i).getDownloadState().equals(task.getDownloadState())){
                        if (list.get(i).getDeleteFlag().equals("0")){
                            list.add(managerList.get(i));
                        }
                    }
                }
                break;
            case 4:
                //downloadState传 linkState传 id = 4
                for (int i=0;i<managerList.size();i++){
                    if(managerList.get(i).getDownloadState().equals(task.getDownloadState()) && managerList.get(i).getLinkState().equals(task.getLinkState())){
                        if (list.get(i).getDeleteFlag().equals("0")){
                            list.add(managerList.get(i));
                        }
                    }
                }
                break;
        }
        return list;
    }
    /**
     * 转换segmentState 和 uploadState
     * @param
     * @return
     */
    private List<TaskManager> changeTaskManager(List<TaskManager> managerList){

        for (int i=0;i<managerList.size();i++){
            if (!managerList.get(i).getSegmentState().equals("")){
                SegmentState segmentState = gson.fromJson(String.valueOf(managerList.get(i).getSegmentState()), SegmentState.class);
                managerList.get(i).setSegmentState(segmentState);
            }
            if (!managerList.get(i).getUploadState().equals("")){
                SegmentUploadState segmentUploadState = gson.fromJson(String.valueOf(managerList.get(i).getUploadState()), SegmentUploadState.class);
                managerList.get(i).setUploadState(segmentUploadState);
            }
        }
        return managerList;
    }
//    @ApiOperation("客户端查看单个任务")
//    @PostMapping(value = "/findOneTask", produces = "application/json;charset=UTF-8")
//    public String findOneTask(@RequestBody FindOneSegment findOneSegment) {
//
//        TaskManager taskManager = segmentService.selectSegmentFilmOne(findOneSegment.getFilmId());
//        System.out.println(taskManager);
//
//        if (taskManager != null) {
//
//            SegmentUploadState segmentUploadState = GsonUtils.fromJson(String.valueOf(taskManager.getUploadState()), SegmentUploadState.class);
//
//            taskManager.setUploadState(segmentUploadState);
//
//            SegmentState segmentState = GsonUtils.fromJson(String.valueOf(taskManager.getSegmentState()), SegmentState.class);
//            taskManager.setSegmentState(segmentState);
//
//            resFilmData.setCode(0);
//            resFilmData.setMsg("success");
//            resFilmData.setData(taskManager);
//            resFilmData.setTotal(1);
//
//            return GsonUtils.toJson(resFilmData);
//        } else {
//            res.setCode(1);
//            res.setMsg("err");
//            res.setData("");
//
//            return GsonUtils.toJson(res);
//        }
//
//
//    }
    @PreAuthorize("@zz.check('user:addTask')")
    @Log("user:addFilmSource")
    @ApiOperation("客户端添加任务")
    @PostMapping(value = "/addFilmSource", produces = "application/json;charset=UTF-8")
    public ResData add(@RequestBody AddSegmentQueue queue) {

        ResData data = new ResData();
        LanguageInfo languageInfo = languageInfoService.selectByLanguage(queue.getLanguage());
        if (languageInfo == null){

            data.setCode(7);
            data.setMsg("没有这个语言");
            data.setData("");

            return data;
        }
        String newResolving = "";
        if (queue.getResolvingPower().equals("")){
            for (int i = 0; i < queue.getMinioInfo().size(); i++) {
                if (newResolving.equals("")){
                    newResolving = queue.getMinioInfo().get(i).getResolvingPower();
                }else {
                    newResolving = newResolving +","+queue.getMinioInfo().get(i).getResolvingPower();;
                }
            }
        }else {
            newResolving = queue.getResolvingPower();
        }

        String resolvingPower = "";
        String minioId = "";
        double filmSize = 0.0;
        Integer languagerId = languageInfo.getId();
        if (queue.getFilmSize().contains("MB")) {
            filmSize = Double.valueOf(queue.getFilmSize().replace("MB", "")) / 1024;
        } else if (queue.getFilmSize().contains("GB")) {
            filmSize = Double.valueOf(queue.getFilmSize().replace("GB", ""));
        }
        List<AddTaskMinio> list = queue.getMinioInfo();

//        if (queue.getMinioInfo().equals("")) {
//
//        } else {
//            System.out.println(queue.getMinioInfo());
//            list = gson.fromJson(String.valueOf(queue.getMinioInfo()), new TypeToken<List<AddTaskMinio>>() {
//            }.getType());
//        }
//        System.out.println(list);
        List<TaskManager> taskManagerList = segmentService.selectSegmentList(newResolving, String.valueOf(languageInfo.getId()), queue.getDoubanId());
        System.out.println(taskManagerList);
        //先判断任务是否存在
        if (taskManagerList.size() == 0) {
            if (!queue.getResolvingPower().equals("")) {
                if (!queue.getResolvingPower().contains(",")) {

                    ResData oneMinio = findOneMinio(queue.getResolvingPower(), filmSize, queue, languagerId);
                    return oneMinio;
                } else {
                    ResData moreMinio = findMoreMinio(queue.getResolvingPower(), queue, filmSize, languagerId);
                    return moreMinio;
                }

            } else {

                for (int i = 0; i < list.size(); i++) {
                    if (resolvingPower.equals("")) {
                        resolvingPower = list.get(i).getResolvingPower();
                    } else {
                        resolvingPower = resolvingPower + "," + list.get(i).getResolvingPower();
                    }
                    if (minioId.equals("")) {
                        minioId = String.valueOf(list.get(i).getId());
                    } else {
                        minioId = minioId + "," + list.get(i).getId();
                    }
                }
//                VisitUrl visitUrl = resVisitUrl(queue);
//
//                visitService.insertVisitUrl(visitUrl);

                ResData resData = addTaskResData(list, minioId, filmSize, queue);

                return resData;
            }
        } else {
            res.setCode(3);
            res.setMsg("任务已存在");
            res.setData("");
            return res;
        }
    }
    /**
     * 封装返回消息 , 将传入数据放入 调用该方法
     */
    public ResData addTaskResData(List<AddTaskMinio> list,String minioId,double filmSize,AddSegmentQueue queue){
        if (list.size() == 1){
            ResData resData = onlyMinio(Integer.valueOf(minioId), filmSize, queue);

            return  resData;
        }else {
            ResData resData = moreMinio(minioId, filmSize, queue);

            return  resData;
        }
    }
    /**
     * 如果只传入一个桶
     */
    public ResData onlyMinio(Integer minioId,double filmSize,AddSegmentQueue queue){
        ResData resData = new ResData();
        List<Integer> list = new ArrayList<>();
        MinioInfo minio = minioInfoService.findMinio(minioId);
        LanguageInfo languageInfo = languageInfoService.selectByLanguage(queue.getLanguage());
        double availableCapacity = minio.getAvailableCapacity();
        if (availableCapacity > filmSize){
            minio.setAvailableCapacity(minio.getAvailableCapacity()-filmSize);
            minio.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));

            minioInfoService.updateMinio(minio,minio.getId());

            TaskManager taskManager = resTaskManger(String.valueOf(minio.getId()), minio.getResolvingPower(), queue, languageInfo.getId(), String.valueOf(filmSize));

            segmentService.insertSegment(taskManager);

            VisitUrl visitUrl1 = visitService.selectByDouBanId(queue.getDoubanId());
            if (visitUrl1 == null){
                VisitUrl visitUrl = resVisitUrl(queue);

                visitService.insertVisitUrl(visitUrl);
            }else {
                if (visitUrl1.getLanguage().equals(queue.getLanguage())){

                }else {
                    visitUrl1.setLanguage(visitUrl1+","+queue.getLanguage());
                    visitService.updateByDouBanId(queue.getDoubanId(),visitUrl1);
                }
            }

            resData.setCode(0);
            resData.setMsg("success");
            resData.setData("");

            return resData;
        }else {
            resData.setCode(1);
            resData.setMsg("返回容量不够的存储桶Id");
            resData.setData(minio.getId());
            return resData;
        }
    }

    /**
     * 传入多个存储桶
     */
    public ResData moreMinio(String minioId,double filmSize,AddSegmentQueue queue){
        ResData resData = new ResData();

        String[] split = minioId.split(",");
        String newMinioId = "";
        LanguageInfo languageInfo = languageInfoService.selectByLanguage(queue.getLanguage());

        String successMinioId = "";
        String successResolvingPower = "";
        for (int i = 0; i < split.length; i++) {
            MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(split[i]));
            double availableCapacity = minio.getAvailableCapacity();
            if (availableCapacity > filmSize){
                minio.setAvailableCapacity(minio.getAvailableCapacity()-filmSize);
                minio.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
                minioInfoService.updateMinio(minio,minio.getId());

                if (successMinioId.equals("")){
                    successMinioId = String.valueOf(minio.getId());
                }else {
                    successMinioId = successMinioId + "," + minio.getId();
                }
                if (successResolvingPower.equals("")){
                    successResolvingPower = String.valueOf(minio.getResolvingPower());
                }else {
                    successResolvingPower = successResolvingPower + "," + minio.getResolvingPower();
                }

            }else {
                if (newMinioId.equals("")){
                    newMinioId = String.valueOf(minio.getId());
                }else {
                    newMinioId = newMinioId + "," + minio.getResolvingPower();
                }
            }
        }
        TaskManager taskManager = resTaskManger(successMinioId, successResolvingPower, queue, languageInfo.getId(), String.valueOf(filmSize));
        if (taskManager.getResolvingPower().equals("")){

        }else {
            VisitUrl visitUrl1 = visitService.selectByDouBanId(queue.getDoubanId());
            if (visitUrl1 == null){
                VisitUrl visitUrl = resVisitUrl(queue);

                visitService.insertVisitUrl(visitUrl);
            }else {
                if (visitUrl1.getLanguage().equals(queue.getLanguage())){

                }else {
                    visitUrl1.setLanguage(visitUrl1+","+queue.getLanguage());
                    visitService.updateByDouBanId(queue.getDoubanId(),visitUrl1);
                }
            }

        }
        segmentService.insertSegment(taskManager);
        if (successResolvingPower.equals(queue.getResolvingPower())){
            resData.setCode(0);
            resData.setMsg("success");
            resData.setData("");
        }else if (successResolvingPower.equals("")){
            resData.setCode(4);
            resData.setMsg("error");
            resData.setData("");
        }else {
            resData.setCode(5);
            resData.setMsg("返回容量不够的存储桶Id,其他分辨率继续执行");
            resData.setData(newMinioId);
        }

        return resData;
    }
    /**
     * 返回一个TaskManager类
     */
    public TaskManager resTaskManger(String minioId,String resolvingPower,AddSegmentQueue queue,Integer languageId,String filmSize){
        TaskManager taskManager = new TaskManager();
        taskManager.setMinioId(minioId);
        taskManager.setFilmId(getRandomString(10));
        taskManager.setDownloadState("0");
        taskManager.setUploadState("");
        taskManager.setSegmentState("");
        taskManager.setLinkState("");
        taskManager.setResolvingPower(resolvingPower);
        taskManager.setDoubanId(queue.getDoubanId());
        taskManager.setWhetherClimb(0);
        taskManager.setFilmName(queue.getFilmName());
        taskManager.setLanguageId(String.valueOf(languageId));
        taskManager.setBtUrl(queue.getBtUrl());
        taskManager.setSubtitleSuffix(queue.getSubtitleSuffix());
        taskManager.setSubtitleUrl(queue.getSubtitleUrl());
        taskManager.setFilmSize(filmSize);
        taskManager.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        taskManager.setDeleteFlag("0");
        taskManager.setFilmRandom(queue.getFilmRandom());
        return taskManager;
    }
    public VisitUrl resVisitUrl(AddSegmentQueue queue){

        VisitUrl visitUrl = new VisitUrl();
        visitUrl.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        visitUrl.setMinioUrl("");
//        visitUrl.setNginxUrl("");
//        visitUrl.setCdnUrl("");
        visitUrl.setDoubanId(queue.getDoubanId());

        return visitUrl;
    }

    /**
     * 只传入一个清晰度
     */
    private ResData findOneMinio(String resolving,double filmSize,AddSegmentQueue queue,Integer languageId) {
        List<MinioInfo> minioInfoList = minioInfoService.selectMoreMinio(resolving);
        ResData resData = new ResData();
        double availableCapacity = 0.0;
        String minioId = "";
        String definition = "";
        for (int i = 0; i < minioInfoList.size(); i++) {
            availableCapacity = minioInfoList.get(i).getAvailableCapacity();
            if (availableCapacity > filmSize) {
                minioId = String.valueOf(minioInfoList.get(i).getId());

                minioInfoList.get(i).setAvailableCapacity(minioInfoList.get(i).getAvailableCapacity()-filmSize);
                minioInfoList.get(i).setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
                minioInfoService.updateMinio(minioInfoList.get(i),minioInfoList.get(i).getId());
                break;
            } else {

            }
        }
        TaskManager taskManager = resTaskManger(minioId, queue.getResolvingPower(), queue, languageId, String.valueOf(filmSize));

        if (minioId.equals("")) {
            resData.setCode(6);
            resData.setMsg("该清晰度没有对应的存储桶可以进行存储");
            resData.setData(resolving);
        } else {
            VisitUrl visitUrl1 = visitService.selectByDouBanId(queue.getDoubanId());
            if (visitUrl1 == null){
                VisitUrl visitUrl = resVisitUrl(queue);

                visitService.insertVisitUrl(visitUrl);
            }else {
                if (visitUrl1.getLanguage().equals(queue.getLanguage())){

                }else {
                    visitUrl1.setLanguage(visitUrl1+","+queue.getLanguage());
                    visitService.updateByDouBanId(queue.getDoubanId(),visitUrl1);
                }
            }
            segmentService.insertSegment(taskManager);
            resData.setCode(0);
            resData.setMsg("success");
            resData.setData("");
        }
        return resData;

    }

    /**
     * 传多个清晰度
     * @param resolving
     * @param queue
     * @param filmSize
     * @param languageId
     * @return
     */
    private ResData findMoreMinio(String resolving,AddSegmentQueue queue,double filmSize,Integer languageId) {
        ResData resData = new ResData();
        String[] split = resolving.split(",");

        double availableCapacity = 0.0;
        String minioId = "";
        String successResolving = "";
        String failResolving = "";
        String failMinioId = "";
        for (int i = 0; i < split.length; i++) {
            List<MinioInfo> minioByResolvingPower = minioInfoService.findMinioByResolvingPower(split[i]);

            for (int j = 0; j < minioByResolvingPower.size(); j++) {
                availableCapacity = minioByResolvingPower.get(j).getAvailableCapacity();
                if (availableCapacity > filmSize){
                    minioByResolvingPower.get(j).setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
                    minioByResolvingPower.get(j).setAvailableCapacity(minioByResolvingPower.get(j).getAvailableCapacity()-filmSize);
                    minioInfoService.updateMinio(minioByResolvingPower.get(j),minioByResolvingPower.get(j).getId());
                    if (minioId.equals("")){
                        minioId = String.valueOf(minioByResolvingPower.get(j).getId());
                    }else {
                        minioId = minioId + ","+String.valueOf(minioByResolvingPower.get(j).getId());
                    }
                    if (successResolving.equals("")){
                        successResolving = split[i];
                    }else {
                        successResolving = successResolving + "," + split[i];
                    }

                    break;
                }else {
                    if (failResolving.equals("")){
                        failResolving = split[i];
                    }else {
                        if (failResolving.contains(split[i])){

                        }else {
                            failResolving = failResolving + "," + split[i];
                        }
                    }
                    if (failMinioId.equals("")){
                        failMinioId = String.valueOf(minioByResolvingPower.get(i).getId());
                    }else {
                        failMinioId = failMinioId + minioByResolvingPower.get(i).getId();
                    }
                }
            }

        }
        if (queue.getResolvingPower().length() == successResolving.length()){
            failResolving = "";
        }
        if (minioId.equals("")){
            resData.setCode(1);
            resData.setMsg("error");
            resData.setData("");
        }else {
            VisitUrl visitUrl1 = visitService.selectByDouBanId(queue.getDoubanId());
            if (visitUrl1 == null){
                VisitUrl visitUrl = resVisitUrl(queue);

                visitService.insertVisitUrl(visitUrl);
            }else {
                if (visitUrl1.getLanguage().equals(queue.getLanguage())){

                }else {
                    visitUrl1.setLanguage(visitUrl1+","+queue.getLanguage());
                    visitService.updateByDouBanId(queue.getDoubanId(),visitUrl1);
                }
            }
            TaskManager taskManager = resTaskManger(minioId, successResolving, queue, languageId, String.valueOf(filmSize));
            segmentService.insertSegment(taskManager);
            if (failResolving.equals("")){
                resData.setCode(0);
                resData.setMsg("success");
                resData.setData("");
            }else if (!successResolving.equals("") && !failResolving.equals("")){
                resData.setCode(6);
                resData.setMsg("返回失败的清晰度存储桶，其他任务继续执行");
                resData.setData(failMinioId);
            }
        }
//        if (){
//
//        }
        TaskManager taskManager = resTaskManger(minioId, queue.getResolvingPower(), queue, languageId, String.valueOf(filmSize));
        return resData;
    }

    @Log("user:delTask")
    @ApiOperation("客户端删除任务")
    @PostMapping(value = "/delTask", produces = "application/json;charset=UTF-8")
    public ResData delTask(@RequestBody FindOneSegment findOneSegment) {

//        User user = userService.selectUserInfo(findOneSegment.getUserId());
//        long currentTime = System.currentTimeMillis()/1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());

        TaskManager taskManager = segmentService.selectByFilmId(findOneSegment.getFilmId());
        taskManager.setDeleteFlag("1");
        segmentService.updateUploadState(taskManager.getFilmId(), taskManager);

        res.setCode(0);
        res.setMsg("success");
        res.setData("");

        return res;
    }
    @Log("user:updateTask")
    @ApiOperation("客户端更新失败的任务")
    @PostMapping(value = "/updateTask", produces = "application/json;charset=UTF-8")
    public ResData updateTask(@RequestBody UpdateTask updateTask) {

        String s = segmentService.updateFailUrl(updateTask.getFilmId(), updateTask.getBtUrl(), updateTask.getSubtitleUrl());
        if (s.equals("success")) {
            segmentService.updateIdTask(updateTask.getFilmId(), "0");

            res.setCode(0);
            res.setMsg("success");
            res.setData("");
            return res;
        } else {
            res.setCode(2);
            res.setMsg("不能输入空数据");
            res.setData("");
            return res;
        }

    }


    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
