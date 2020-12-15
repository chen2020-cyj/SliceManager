package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;

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

    private ResData res = new ResData();
    private Gson gson = new Gson();
    private MinioInfo minioInfo = new MinioInfo();
//    private  ResAddSegment resAddSegment = new ResAddSegment();

    private ResFilmData resFilmData = new ResFilmData();

    private ResFilmData resTaskData = new ResFilmData();
    private Msg msg = new Msg();

    @ApiOperation("客户端查看所有任务")
    @PostMapping(value = "/findMoreTask", produces = "application/json;charset=UTF-8")
    public String clientFindMoreTask(@RequestBody FindSegmentTask task) {
        Integer page = (task.getPage() - 1) * task.getOffset();
        Integer offset = task.getOffset();

        IPage<TaskManager> segmentManagerIPage = segmentService.selectSegment(page, offset);
//        User user = userService.selectUserInfo(Integer.valueOf(task.getUserId()));
        List<TaskManager> list = segmentManagerIPage.getRecords();
//
//        long currentTime = System.currentTimeMillis() / 1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());


        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {

                SegmentUploadState segmentUploadState = GsonUtils.fromJson(String.valueOf(list.get(i).getUploadState()), SegmentUploadState.class);

                list.get(i).setUploadState(segmentUploadState);

            }
            for (int i = 0; i < list.size(); i++) {

                SegmentState segmentState = GsonUtils.fromJson(String.valueOf(list.get(i).getSegmentState()), SegmentState.class);
                list.get(i).setSegmentState(segmentState);
            }
            for (int i = 0; i < list.size(); i++) {

                LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(list.get(i).getLanguageId()));
                list.get(i).setLanguageId(languageInfo.getLanguage());
            }

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(list);
            resFilmData.setTotal(segmentService.selectTaskCount());
            return GsonUtils.toJson(resFilmData);
        } else {
            res.setCode(2);
            res.setMsg("数据错误");
            res.setData("");

            return GsonUtils.toJson(res);
        }

    }

    @ApiOperation("客户端查看单个任务")
    @PostMapping(value = "/findOneTask", produces = "application/json;charset=UTF-8")
    public String findOneTask(@RequestBody FindOneSegment findOneSegment) {

//        Integer userId = JwtUtils.verify(findOneSegment.getToken());
//        User user = userService.selectUserInfo(userId);
//
        TaskManager taskManager = segmentService.selectSegmentFilmOne(findOneSegment.getFilmId());
        System.out.println(taskManager);
//        long currentTime = System.currentTimeMillis() / 1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());

        if (taskManager != null) {

            SegmentUploadState segmentUploadState = GsonUtils.fromJson(String.valueOf(taskManager.getUploadState()), SegmentUploadState.class);

            taskManager.setUploadState(segmentUploadState);

            SegmentState segmentState = GsonUtils.fromJson(String.valueOf(taskManager.getSegmentState()), SegmentState.class);
            taskManager.setSegmentState(segmentState);

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(taskManager);
            resFilmData.setTotal(1);

            return GsonUtils.toJson(resFilmData);
        } else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("");

            return GsonUtils.toJson(res);
        }


    }

    @ApiOperation("客户端添加任务")
    @PostMapping(value = "/addFilmSource", produces = "application/json;charset=UTF-8")
    public String add(@RequestBody AddSegmentQueue queue) {
        TaskManager segment = new TaskManager();
        segment.setDoubanId("");
        List<MinioData> listMinio = new ArrayList<>();
        List<DbData> listDbData = new ArrayList<>();

        String filmId = getRandomString(10);

        Map<String, String> map = new HashMap<>();
//        User user = userService.selectUserInfo(Integer.valueOf(queue.getUserId()));

        LanguageInfo languageInfo1 = languageInfoService.selectByLanguage(queue.getLanguage());


        List<Integer> arrList = new ArrayList<>();
//        List<String> arrList2 = new ArrayList<>();
//        for (int i=0;i<split.length;i++){
//            arrList1.add(split[i]);
//        }
        if (!queue.getResolvingPower().contains(",")){

        }else {
            String[] split = queue.getResolvingPower().split(",");
            for (int i = 0; i < split.length; i++) {
                arrList.add(Integer.valueOf(split[i]));
                Collections.sort(arrList);
            }
            String newResolvingPower = "";
            for (int i=0;i<arrList.size();i++){
                if (newResolvingPower.equals("")){
                    newResolvingPower = String.valueOf(arrList.get(i));
                }else {
                    newResolvingPower = newResolvingPower+","+String.valueOf(arrList.get(i));
                }
            }
            queue.setResolvingPower(newResolvingPower);
        }

        Map<String, String> filmMap = new HashMap<>();
        filmMap.put("film_name", queue.getFilmName());
        filmMap.put("language_id", String.valueOf(languageInfo1.getId()));
        IPage<FilmInfo> infoIPage = filmInfoService.selectByFilmName(queue.getFilmName());
        List<FilmInfo> filmInfos = infoIPage.getRecords();
//            List<FilmInfo> filmInfoList = new ArrayList<>();
//            filmInfoList = infoIPage.getRecords();

        if (filmInfos.size() > 0) {
            segment.setDoubanId("");
        } else {
            List<DouBanData> list = OkHttpUtils.douBanSearch(queue.getFilmName());
            if (list.size() > 0){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getTitle().contains(queue.getFilmName())){
                        segment.setDoubanId(String.valueOf(list.get(i).getDoubanId()));
                        break;
                    }
                }
                if (segment.getDoubanId().equals("") || segment.getDoubanId() == null ){
                    segment.setDoubanId(String.valueOf(list.get(0).getDoubanId()));
                }
            }else {
                res.setCode(7);
                res.setMsg("豆瓣解析失败");
                res.setData("");
            }

        }


        TaskManager taskManager = segmentService.selectSegmentList(queue.getResolvingPower(), String.valueOf(languageInfo1.getId()), queue.getFilmName());
        System.out.println(taskManager);
        if (taskManager == null) {
            ResData resData = addTask(queue, String.valueOf(languageInfo1.getId()), segment);
            return GsonUtils.toJson(resData);
        } else {
            System.out.println("adadadad");

            res.setCode(3);
            res.setMsg("任务存在");
            res.setData("");

            return GsonUtils.toJson(res);


        }

    }

    /**
     * 添加任务  没有任务添加
     *
     * @param
     * @return
     */
    public ResData addTask(AddSegmentQueue queue, String languageId, TaskManager segment) {
        ResAddSegment resAddSegment = new ResAddSegment();
        double filmSize = 0.0;
        if (queue.getFilmSize().contains("MB")) {
            String mb = queue.getFilmSize().replace("MB", "");
            filmSize = Double.valueOf(mb) / 1024;

        } else if (queue.getFilmSize().contains("GB")) {
            String gb = queue.getFilmSize().replace("GB", "");
            filmSize = Double.valueOf(gb);
        }
        List<ResAddSegment> resList = new ArrayList<>();

        String resolvingPower = queue.getResolvingPower();
        double totalSize = 0.0;
        double availableSize = 0.0;


        if (!queue.getResolvingPower().contains(",")) {
            List<MinioInfo> minioInfos = minioInfoService.selectMoreMinio(resolvingPower);
            String minioId = "";
            String notMinioId = "";
            for (int i = 0; i < minioInfos.size(); i++) {
                totalSize = minioInfos.get(i).getTotalCapacity();
                availableSize = minioInfos.get(i).getAvailableCapacity();

                if (filmSize < availableSize) {
//                    segment.setMinioId(String.valueOf(minioInfos.get(i).getId()));
                    minioId = String.valueOf(minioInfos.get(i).getId());
                    minioInfo.setResolvingPower(queue.getResolvingPower());
                    minioInfo.setArea(minioInfos.get(i).getArea());
                    minioInfo.setTotalCapacity(totalSize);
                    minioInfo.setAvailableCapacity(availableSize - filmSize);
                    minioInfo.setMsg(minioInfos.get(i).getMsg());
                    minioInfo.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
                    minioInfoService.updateMinio(minioInfo,minioInfos.get(i).getId());
                    break;
                } else {
//                    if (notMinioId.equals("")) {
////                        notMinioId = queue.getResolvingPower();
////                    } else {
////                        notMinioId = notMinioId + "," + queue.getResolvingPower();
////                        ;
////                    }
////
                    System.out.println(resList);
                    resAddSegment.setResolvingPower(queue.getResolvingPower());

                }
            }
            resList.add(resAddSegment);
            if (resAddSegment.getResolvingPower() == null) {
                TaskManager taskManager = resTaskManager(segment, minioId, queue, filmSize, languageId);
                minioInfoService.updateMinio(minioInfo, minioInfos.get(0).getId());

                segmentService.insertSegment(taskManager);
            }
            res.setCode(6);
            res.setMsg("分辨率已经存满的存储桶");
            res.setData(resList);

            return res;
        } else {

            String[] split = queue.getResolvingPower().split(",");

            String MinioId = "";
            String notMinioId = "";
            String resolving = "";
            List<MinioInfo> minioList = new ArrayList<>();

            for (int i = 0; i < split.length; i++) {
                List<MinioInfo> minio = minioInfoService.findMinio(split[i]);
                List<DbData> listDb = new ArrayList<>();
                msg = GsonUtils.fromJson(String.valueOf(minio.get(0).getMsg()), Msg.class);


                totalSize = minio.get(0).getTotalCapacity();
                availableSize = minio.get(0).getAvailableCapacity();


                if (filmSize < availableSize) {
                    if (MinioId.equals("")) {
                        MinioId = String.valueOf(minio.get(0).getId());
                    } else {
                        MinioId = MinioId + "," + String.valueOf(minio.get(0).getId());
                    }

                    minioInfo.setResolvingPower(minio.get(0).getResolvingPower());
                    minioInfo.setArea(minio.get(0).getArea());
                    minioInfo.setTotalCapacity(totalSize);
                    minioInfo.setAvailableCapacity(availableSize - filmSize);
                    minioInfo.setMsg(minio.get(0).getMsg());
                    minioInfo.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
                    minioList.add(minioInfo);

                    minioInfoService.updateMinio(minioInfo,minioInfo.getId());

                    continue;
//

                } else {
                    if (notMinioId.equals("")) {
                        notMinioId = split[i];
                    } else {
                        notMinioId = notMinioId + "," + split[i];
                    }
                    resAddSegment.setResolvingPower(notMinioId);
                    resList.add(resAddSegment);
                }
            }
            TaskManager taskManager = resTaskManager(segment, MinioId, queue, filmSize, languageId);
            String str = "";
//            if (resList.size() == 0) {

            if (resList.size() > 0){
                List<String> task1 = new ArrayList<>();
                List<String> task2 = new ArrayList<>();
                String[] split1 = queue.getResolvingPower().split(",");

                for (int i=0;i<split1.length;i++){
                    task1.add(split1[i]);
                }
                for (int i=0;i<resList.size();i++){
                    task2.add(resList.get(i).getResolvingPower());
                }
                System.out.println("成功清晰度：：："+task1);
                System.out.println("失败清晰度：：："+task2);
                task1.removeAll(task2);

                for (int i=0;i<task1.size();i++){
                    if (str.equals("")){
                        str = task1.get(i);
                    }else{
                        str = str+","+task1.get(i);
                    }
                }
                System.out.println("清晰度"+str);
                taskManager.setResolvingPower(str);
                segmentService.insertSegment(taskManager);
            }else {
                System.out.println("数据" + taskManager);
                segmentService.insertSegment(taskManager);
            }


            if (minioList.size() > 0) {
                for (int i = 0; i < minioList.size(); i++) {

                    minioInfoService.updateMinio(minioList.get(i), minioList.get(i).getId());
                }
                res.setCode(0);
                res.setMsg("返回分辨率，如有有数据那么就表示该分辨率存储桶已满,其他任务自动分配");
                res.setData(resList);
                return res;
            } else {
                String resolvingFail = "";
                String[] split1 = queue.getResolvingPower().split(",");
                for (int i = 0; i < split1.length; i++) {

                    if (resolvingFail.equals("")) {
                        resolvingFail = split1[i];
                    } else {
                        resolvingFail = resolvingFail + "," + split1[i];
                    }
                }
                resAddSegment.setResolvingPower(resolvingFail);
                resList.add(resAddSegment);
//                taskManager.setResolvingPower(str);
//                segmentService.insertSegment(taskManager);
                res.setCode(6);
                res.setMsg("返回分辨率，如有有数据那么就表示该分辨率存储桶已满,其他任务自动分配");
                res.setData(resList);
                return res;
            }

        }

    }


    public TaskManager resTaskManager(TaskManager segment, String MinioId, AddSegmentQueue queue, double filmSize, String languageId) {

        segment.setMinioId(MinioId);
        segment.setFilmId(getRandomString(10));
        segment.setFilmName(queue.getFilmName());
        segment.setDownloadState("0");
        segment.setResolvingPower(queue.getResolvingPower());
        segment.setBtUrl(queue.getBtUrl());
        segment.setSubtitleUrl(queue.getSubtitleUrl());
        segment.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000));
        segment.setUploadState("");
        segment.setLinkState("");
        segment.setUploadState("");
        segment.setSegmentState("");
        segment.setFilmSize(String.valueOf(filmSize));
        segment.setSubtitleSuffix(queue.getSubtitleSuffix());
        segment.setLanguageId(languageId);
        segment.setMinioUrl("");
//        segment.setDoubanId("");
        return segment;
    }


    @ApiOperation("客户端查看状态，传入不同的状态获得不同状态的数据")
    @PostMapping(value = "/findQueueAnnounceState", produces = "application/json;charset=UTF-8")
    public String findQueueState(@RequestBody FindAnnounce announce) {

        Integer page = (announce.getPage() - 1) * announce.getOffset();
        Integer offset = announce.getOffset();
        IPage<TaskManager> segmentManager = segmentService.selectState(page, offset, announce.getState());
        List<TaskManager> list = segmentManager.getRecords();

//        User user = userService.selectUserInfo(announce.getUserId());
//        Long currentTime = System.currentTimeMillis()/1000;
//        Long tokenTime =Long.valueOf(user.getTokenTime());


        if (list.size() > 0) {

            resFilmData.setCode(0);
            resFilmData.setMsg("err");
            resFilmData.setData(list);
            resFilmData.setTotal(segmentService.selectStateCount(announce.getState()));
            return GsonUtils.toJson(res);

        } else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("");

            return GsonUtils.toJson(res);
        }


    }

    @ApiOperation("客户端删除任务")
    @PostMapping(value = "/delTask", produces = "application/json;charset=UTF-8")
    public ResData delTask(@RequestBody FindOneSegment findOneSegment) {

//        User user = userService.selectUserInfo(findOneSegment.getUserId());
//        long currentTime = System.currentTimeMillis()/1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());

        TaskManager taskManager = segmentService.delTask(findOneSegment.getFilmId());
        if (taskManager != null) {
            res.setCode(0);
            res.setMsg("success");
            res.setData("");

            return res;
        } else {
            res.setCode(1);
            res.setMsg("没有这条数据");
            res.setData("");

            return res;
        }


    }

    @ApiOperation("客户端更新失败的任务")
    @PostMapping(value = "/updateTask", produces = "application/json;charset=UTF-8")
    public ResData updateTask(@RequestBody UpdateTask updateTask) {

//        User user = userService.selectUserInfo(updateTask.getUserId());
//        long currentTime = System.currentTimeMillis()/1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());

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

    @ApiOperation("客户端查看切片失败的任务")
    @PostMapping(value = "/findFailTask", produces = "application/json;charset=UTF-8")
    public ResFilmData findFailTask(@RequestBody FindFailTask findFailTask) {
        List<TaskManager> taskList = new ArrayList<>();
//        User user = userService.selectUserInfo(findFailTask.getUserId());
//        long currentTime = System.currentTimeMillis()/1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());

        Integer page = (findFailTask.getPage() - 1) * findFailTask.getOffset();
        Integer offset = findFailTask.getOffset();
        IPage<TaskManager> taskManagerIPage = segmentService.selectSegment(page, offset);

        List<TaskManager> list = taskManagerIPage.getRecords();
        List<String> fail = new ArrayList<>();

        String str = "";
        boolean state = false;

        for (int i = 0; i < list.size(); i++) {
            TaskManager task = new TaskManager();

            SegmentState segmentState = GsonUtils.fromJson(String.valueOf(list.get(i).getSegmentState()), SegmentState.class);
            if (segmentState != null) {

                if (!segmentState.getSegmentFail().equals("")) {
                    if (!segmentState.getSegmentFail().contains(",")) {

                        if (segmentState.getSegmentFail().equals("2021")) {
                            state = true;
                        } else if (segmentState.getSegmentFail().equals("2022")) {
                            state = true;
                        } else if (segmentState.getSegmentFail().equals("2023")) {
                            state = true;
                        } else {

                        }
                        if (state == true) {
                            list.get(i).setSegmentState(segmentState);
                            SegmentUploadState uploadState = GsonUtils.fromJson(String.valueOf(list.get(i).getUploadState()), SegmentUploadState.class);
                            list.get(i).setUploadState(uploadState);
                            taskList.add(list.get(i));
                        }
                        state = false;
                    } else {
                        String[] split = segmentState.getSegmentFail().split(",");
                        for (int j = 0; j < split.length; j++) {
                            if (split[j].equals("2021")) {
                                state = true;
                            } else if (split[j].equals("2022")) {
                                state = true;
                            } else if (split[j].equals("2023")) {
                                state = true;
                            } else {

                            }
                        }
                        if (state == true) {
                            list.get(i).setSegmentState(segmentState);
                            SegmentUploadState uploadState = GsonUtils.fromJson(String.valueOf(list.get(i).getUploadState()), SegmentUploadState.class);
                            list.get(i).setUploadState(uploadState);
                            taskList.add(list.get(i));
                        }
                        state = false;
                    }
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
        resTaskData.setCode(0);
        resTaskData.setMsg("返回所有切片失败的任务");
        resTaskData.setData(taskList);
        resTaskData.setTotal(taskList.size());
//            taskList.clear();
        return resTaskData;

    }

    /**
     * 添加语言
     *
     * @param addLanguage
     * @return
     */
    @ApiOperation("添加语言")
    @PostMapping(value = "/addLanguage", produces = "application/json;charset=UTF-8")
    public ResData addLanguage(@RequestBody AddLanguage addLanguage) {

//        User user = userService.selectUserInfo(addLanguage.getUserId());
//
//        long currentTime = System.currentTimeMillis()/1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());


        LanguageInfo languageInfo = new LanguageInfo();
        languageInfo.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000));
        languageInfo.setLanguage(addLanguage.getLanguage());
        languageInfoService.insertLanguage(languageInfo);

        res.setCode(0);
        res.setMsg("success");
        res.setData("");

        return res;

    }

    @ApiOperation("查看所有语言")
    @PostMapping(value = "/findAllLanguage", produces = "application/json;charset=UTF-8")
    public ResFilmData addLanguage(@RequestBody FindAllLanguage findAllLanguage) {

        List<LanguageInfo> languageInfos = languageInfoService.selectAllLanguage();
        resTaskData.setCode(0);
        resTaskData.setMsg("success");
        resTaskData.setData(languageInfos);
        resTaskData.setTotal(languageInfos.size());
        System.out.println(resTaskData);

        return resTaskData;

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
