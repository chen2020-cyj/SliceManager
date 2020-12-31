package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.*;
import com.fl.model.*;
import com.fl.model.clientRes.ReqSliceServer;
import com.fl.model.clientRes.ResData;
import com.fl.model.sliceServerReq.BitTorrent;
import com.fl.model.sliceServerReq.MinioBackMessage;
import com.fl.model.sliceServerRes.ResSegmentManager;
import com.fl.service.*;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
    @Autowired
    private FilmSourceService filmSourceService;
    @Autowired
    private BtDownLoadService btDownLoadService;
    @Autowired
    private FilmInfoService filmInfoService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private NginxManagerService nginxManagerService;

    private ResData res = new ResData();
    private Gson gson = new Gson();
//    private ResSegmentManager resData = new ResSegmentManager();

    private Msg msg = new Msg();
    private ResSegmentManager resSegment = new ResSegmentManager();
    @ApiOperation("分配任务")
    @PostMapping(value = "/distributionTask",produces = "application/json;charset=UTF-8")
    public ResSegmentManager distributionTask(){

        Map<String,String> serverInfo = new HashMap<>();

        IPage<TaskManager> taskManagerIPage = taskManagerService.selectOneTask("0", 0, 1);
        List<TaskManager> listTask;

        listTask = taskManagerIPage.getRecords();
        System.out.println(listTask);
        if (listTask.size()>0){
            if (!listTask.get(0).getResolvingPower().contains(",")){

                MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(listTask.get(0).getMinioId()));

                Msg msg = GsonUtils.fromJson(String.valueOf(minio.getMsg()), Msg.class);
                System.out.println(msg);
                serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));

                taskManagerService.updateIdTask(listTask.get(0).getFilmId(),"1");

//                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setResolvingPower(listTask.get(0).getResolvingPower()+"P");
                resSegment.setFilmId(listTask.get(0).getFilmId());
                resSegment.setBtUrl(listTask.get(0).getBtUrl());
                resSegment.setSubtitleSuffix(listTask.get(0).getSubtitleSuffix());
                resSegment.setMsg(serverInfo);
                resSegment.setFilmSize(listTask.get(0).getFilmSize());
                resSegment.setDoubanId(listTask.get(0).getDoubanId());
                resSegment.setWhetherClimb(0);
                System.out.println(resSegment);
//                System.out.println(resSegment);

                return resSegment;
            }else {
//                System.out.println("1231464657654");
                String[] split = listTask.get(0).getMinioId().split(",");
                for (int i=0;i<split.length;i++){
                    MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(split[i]));
                    Msg msg = GsonUtils.fromJson(String.valueOf(minio.getMsg()), Msg.class);
                    serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));
                }
                taskManagerService.updateIdTask(listTask.get(0).getFilmId(),"1");

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
                resSegment.setFilmSize(listTask.get(0).getFilmSize());
                resSegment.setDoubanId(listTask.get(0).getDoubanId());
                resSegment.setWhetherClimb(listTask.get(0).getWhetherClimb());
                return resSegment;
            }
        }else {

            return null;
        }

    }
    @ApiOperation("任务轮询更新数据")
    @PostMapping("/taskState")
    public synchronized  ResData taskComplete(@RequestBody ReqSliceServer reqSliceServer){


        ResData resData = new ResData();
        resData.setCode(0);
        System.out.println("轮询");
        Integer id = reqSliceServer.getCode();
        String filmId = reqSliceServer.getFilmId();
        System.out.println(reqSliceServer);
        //对请求的code进行判断处理
        String state = String.valueOf(reqSliceServer.getCode());
        if (id == 2){

            TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
            BitTorrent bitTorrent = GsonUtils.fromJson(String.valueOf(reqSliceServer.getData()), BitTorrent.class);

            BtDownLoad btDownLoad = btDownLoadService.selectByFilmId(reqSliceServer.getFilmId());
            if (btDownLoad == null){
                BtDownLoad bt = new BtDownLoad();
                bt.setFilmId(reqSliceServer.getFilmId());
                bt.setBtState(bitTorrent.getDone());
                bt.setStartTime(String.valueOf(System.currentTimeMillis()/1000));
                TaskManager taskManager1 = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
                taskManager1.setDownloadState("2");
                taskManagerService.updateUploadState(reqSliceServer.getFilmId(),taskManager1);
                btDownLoadService.insertBtDownLoad(bt);

                res.setCode(0);
                res.setMsg("ok");
                res.setData("");
                return res;

            }else {
                if (btDownLoad.getBtState().equals(bitTorrent.getDone())){
                    long currentTime = System.currentTimeMillis()/1000;
                    long startTime =Long.valueOf(btDownLoad.getStartTime());

                    long time = currentTime - startTime;
                    if (time> (60*10)){
                        resData.setCode(1);
                        resData.setMsg("任务超时");
                        resData.setData(GsonUtils.toJson(btDownLoad));

                        taskManagerService.updateIdLinkState(filmId,"1003");
                        return resData;
                    }
                }else {
                    btDownLoad.setStartTime(String.valueOf(System.currentTimeMillis()/1000));
                    btDownLoad.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));

                    resData.setCode(2);
                    resData.setMsg("任务可以继续下载");
                    resData.setData(GsonUtils.toJson(btDownLoad));

                    btDownLoadService.updateBtDownLoad(btDownLoad.getFilmId(),btDownLoad);

                    return resData;
                }
            }
//            return  res;
        }
        reqTaskState(id,filmId,state,reqSliceServer,resData);

        if (resData.getCode() == 1){
            return resData;
        }else if (resData.getCode() == 2){
            return resData;
        }

        res.setCode(0);
        res.setMsg("ok");
        res.setData("");

        return res;
    }
    private void reqTaskState(Integer id,String filmId,String state,ReqSliceServer reqSliceServer,ResData resData1){
        System.out.println("进来了");
        switch (id){

            case 3:
                //bt种子下载完成
                taskManagerService.updateIdTask(filmId,state);
                break;
            case 1001:
                taskManagerService.updateIdLinkState(filmId,state);
                //种子链接无效
                break;
            case 1002:
                //字幕链接无效
                taskManagerService.updateIdLinkState(filmId,state);
                break;
            case 2001:
                //720开始切片
                segmentStart(reqSliceServer);
                break;
            case 2002:
                //480开始切片
                segmentStart(reqSliceServer);
                break;
            case 2003:
                //320开始切片

                segmentStart(reqSliceServer);
                break;
            case 2011:
                //720切片完成
                segmentSuccess(reqSliceServer);
                break;
            case 2012:
                //480切片完成
                segmentSuccess(reqSliceServer);
                break;
            case 2013:
                //320切片完成
                segmentSuccess(reqSliceServer);
                break;
            case 2021:
                //720切片失败
                segmentFail(reqSliceServer);
                break;
            case 2022:
                //480切片失败
                segmentFail(reqSliceServer);
                break;
            case 2023:
                //320切片失败
                segmentFail(reqSliceServer);
                break;
            case 6003:
                //720上传状态
                upload(reqSliceServer);
                break;
            case 6004:
                //480上传状态
                upload(reqSliceServer);
                break;
            case 6005:
                //320上传状态
                upload(reqSliceServer);
                break;
            case 6023:
                //720上传失败
                uploadFail(reqSliceServer);
                break;
            case 6024:
                //480上传失败
                uploadFail(reqSliceServer);
                break;
            case 6025:
                //320上传失败
                uploadFail(reqSliceServer);
                break;
            case 1212:
                reptile(reqSliceServer,resData1);

                break;
        }
    }

    /**
     * 开始切片状态
     * @param reqSliceServer
     */
    private void segmentStart(ReqSliceServer reqSliceServer) {
        System.out.println("adadadahfhgfhgg");
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        if (!taskManager.getDownloadState().equals("3")){
            taskManager.setDownloadState("3");
            taskManagerService.updateUploadState(taskManager.getFilmId(),taskManager);
        }
        String segmentState = String.valueOf(taskManager.getSegmentState());
        System.out.println("切片状态"+segmentState);
        String code = String.valueOf(reqSliceServer.getCode());
        if (!segmentState.equals("")) {
            SegmentState segmentState1 = GsonUtils.fromJson(segmentState, SegmentState.class);
            switch (code) {
                case "2001":
                    segmentStartDecide(segmentState1, code);
                    break;
                case "2002":
                    segmentStartDecide(segmentState1, code);
                    break;
                case "2003":
                    segmentStartDecide(segmentState1, code);
                    break;
            }
            taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(), GsonUtils.toJson(segmentState1));
        } else {
            SegmentState segmentState1 = new SegmentState();
            segmentState1.setSegmentFail("");
            segmentState1.setSegmentSuccess("");
            segmentState1.setSegmentStart(code);

            taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(), GsonUtils.toJson(segmentState1));
        }
    }
    private void segmentStartDecide(SegmentState segmentState,String code){
        if (segmentState.getSegmentStart().equals("")){
            segmentState.setSegmentStart(code);
        }else {
            if (segmentState.getSegmentStart().contains(code)){

            }else {
                segmentState.setSegmentStart(segmentState.getSegmentStart()+","+code);
            }
        }
    }
    /**
     * 切片完成状态
     */
    private void segmentSuccess(ReqSliceServer reqSliceServer){
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        String segmentState = String.valueOf(taskManager.getSegmentState());
        String code = String.valueOf(reqSliceServer.getCode());

        SegmentState segmentState1 = GsonUtils.fromJson(segmentState, SegmentState.class);

        switch (code){
            case "2011":
                segmentSuccessDecide(segmentState1,code);
                break;
            case "2012":
                segmentSuccessDecide(segmentState1,code);
                break;
            case "2013":
                segmentSuccessDecide(segmentState1,code);
                break;
        }
        taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(),GsonUtils.toJson(segmentState1));

    }
    private void segmentSuccessDecide(SegmentState segmentState,String code){
        System.out.println(segmentState.getSegmentSuccess().equals(""));
        if (segmentState.getSegmentSuccess().equals("") || segmentState.getSegmentSuccess() == null){
            String str = "";
            if (code.equals("2011")){
                str = "2001";
            }else if (code.equals("2012")){
                str = "2002";
            }else if (code.equals("2013")){
                str = "2003";
            }
            if (segmentState.getSegmentStart().contains(str)){
                if (segmentState.getSegmentStart().contains(",")){
                    if (segmentState.getSegmentStart().contains(","+str)) {
                        segmentState.setSegmentStart(segmentState.getSegmentStart().replace(","+str,""));
                    }else if (segmentState.getSegmentStart().contains(str+",")){
                        segmentState.setSegmentStart(segmentState.getSegmentStart().replace(str+",",""));
                    }
                }else {
                    if (segmentState.getSegmentStart().equals(str)){
                        segmentState.setSegmentStart("");
                    }
                }

            }else {
                if (segmentState.getSegmentStart().equals(str)){
                    segmentState.setSegmentStart("");
                }
            }
//            if (segmentState.getSegmentSuccess().equals("")){
//                segmentState.setSegmentSuccess(code);
//            }else {
//                if (segmentState.getSegmentSuccess().contains(code)){
//
//                }else {
                    segmentState.setSegmentSuccess(code);
//                }
//            }


        }else {
//            System.out.println("下层判断");
            if (segmentState.getSegmentSuccess().contains(code)) {
//                System.out.println("afafafhggggggg");
                System.out.println(segmentState);
            } else {

                String str = "";
                if (code.equals("2011")) {
                    str = "2001";
                } else if (code.equals("2012")) {
                    str = "2002";
                } else if (code.equals("2013")) {
                    str = "2003";
                }
                if (segmentState.getSegmentStart().contains(",")) {
                    if (segmentState.getSegmentStart().contains("," + str)) {
                        segmentState.setSegmentStart(segmentState.getSegmentStart().replace("," + str, ""));
                    } else if (segmentState.getSegmentStart().contains(str + ",")) {
                        segmentState.setSegmentStart(segmentState.getSegmentStart().replace(str + ",", ""));
                    }
                } else {
                    if (segmentState.getSegmentStart().equals(str)) {
                        segmentState.setSegmentStart("");
                    }
                    System.out.println("切片状态" + segmentState);
                }


                if (segmentState.getSegmentSuccess().equals("")) {
                    segmentState.setSegmentSuccess(code);
                } else {
                    if (segmentState.getSegmentSuccess().contains(code)) {

                    } else {
                        segmentState.setSegmentSuccess(segmentState.getSegmentSuccess() + "," + code);
                    }
                }

            }
        }
//
//        if (!segmentState.getSegmentStart().contains(",")){
//            segmentState.setSegmentStart("");
//        }else {
//            String[] split = segmentState.getSegmentStart().split(",");
//            String str = "";
//            for (int i=0;i<split.length;i++){
//                if (code.equals(split[i])){
//
//                }else {
//                    if (str.equals("")){
//                        str = split[i];
//                    }else {
//                        str = str+","+split[i];
//                    }
//                }
//            }
//            segmentState.setSegmentStart(str);
//        }

    }

    /**
     * 切片失败状态
     */
    private void segmentFail(ReqSliceServer reqSliceServer){
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        String segmentState = String.valueOf(taskManager.getSegmentState());
        String code = String.valueOf(reqSliceServer.getCode());
        SegmentState segmentState1 = GsonUtils.fromJson(segmentState, SegmentState.class);

        switch (code){
            case "2021":
                segmentFailDecide(segmentState1,code);
                break;
            case "2022":
                segmentFailDecide(segmentState1,code);
                break;
            case "2023":
                segmentFailDecide(segmentState1,code);
                break;
        }
    }
    public void segmentFailDecide(SegmentState segmentState,String code){
        if (segmentState.getSegmentSuccess().equals("")){
            segmentState.setSegmentSuccess(code);
        }else {
            if (segmentState.getSegmentSuccess().contains(code)){

            }else {
                segmentState.setSegmentSuccess(segmentState.getSegmentSuccess()+","+code);
            }
        }
    }

    /**
     * 切片上传成功or正在上传状态
     * @param reqSliceServer
     */
    private void upload(ReqSliceServer reqSliceServer){
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        String uploadState = String.valueOf(taskManager.getUploadState());
        String code = String.valueOf(reqSliceServer.getCode());
        if (reqSliceServer.getData().equals("")){
//            System.out.println("上传状态");
            if (!uploadState.equals("")){
                SegmentUploadState segmentUploadState = GsonUtils.fromJson(uploadState, SegmentUploadState.class);
//                segmentUploadState.setSegmentUploadComplete("");
//                segmentUploadState.setSegmentUploadFail("");
//                segmentUploadState.setSegmentUploadComplete("");

                switch (code){
                    case "6003":
                        if (segmentUploadState.getSegmentUpload().contains(code)){

                        }else {
                            segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
                        }
                        break;
                    case "6004":
                        if (segmentUploadState.getSegmentUpload().contains(code)){

                        }else {
                            segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
                        }
                        break;
                    case "6005":
                        if (segmentUploadState.getSegmentUpload().contains(code)){

                        }else {
                            segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
                        }
                        break;
                }
                        System.out.println(segmentUploadState);
                taskManager.setUploadState(GsonUtils.toJson(segmentUploadState));
                taskManagerService.updateUploadState(reqSliceServer.getFilmId(),taskManager);
            }else {
                SegmentUploadState segmentUploadState = new SegmentUploadState();
                segmentUploadState.setSegmentUploadComplete("");
                segmentUploadState.setSegmentUploadFail("");
                switch (code){
                    case "6003":
//                        segmentUploadState.setSegmentUpload(code);
//                        if (segmentUploadState.getSegmentUpload().equals("")){
//                            segmentUploadState.setSegmentUpload(code);
//                        }else {
//                            if (segmentUploadState.getSegmentUpload().contains(code)){
//
//                            }else {
//                                segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
//                            }
//                        }
                        segmentUploadState.setSegmentUpload(code);
                        break;
                    case "6004":
                        segmentUploadState.setSegmentUpload(code);
//                        if (segmentUploadState.getSegmentUpload().equals("")){
//                            segmentUploadState.setSegmentUpload(code);
//                        }else {
//                            if (segmentUploadState.getSegmentUpload().contains(code)){
//
//                            }else {
//                                segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
//                            }
//                        }
                        break;
                    case "6005":
                        segmentUploadState.setSegmentUpload(code);
//                        if (segmentUploadState.getSegmentUpload().equals("")){
//                            segmentUploadState.setSegmentUpload(code);
//                        }else {
//                            if (segmentUploadState.getSegmentUpload().contains(code)){
//
//                            }else {
//
//                            }
//                        }
                        break;
                }
                taskManager.setUploadState(GsonUtils.toJson(segmentUploadState));
                taskManagerService.updateUploadState(reqSliceServer.getFilmId(),taskManager);
            }
        }else {
            SegmentUploadState segmentUploadState;
            if (uploadState.equals("")){
                segmentUploadState = new SegmentUploadState();
            }else {
                segmentUploadState = GsonUtils.fromJson(uploadState, SegmentUploadState.class);
            }

            MinioBackMessage minioBackMessage = GsonUtils.fromJson(String.valueOf(reqSliceServer.getData()), MinioBackMessage.class);
            switch (code){
                case "6003":
                    uploadDecide(segmentUploadState,"6013",taskManager,minioBackMessage,reqSliceServer);
                    break;
                case "6004":
                    uploadDecide(segmentUploadState,"6014",taskManager,minioBackMessage,reqSliceServer);
                    break;
                case "6005":
                    uploadDecide(segmentUploadState,"6015",taskManager,minioBackMessage,reqSliceServer);
                    break;
            }
//            taskManagerService.updateUploadState(reqSliceServer.getFilmId(),GsonUtils.toJson(segmentUploadState));
        }
    }
    private void uploadDecide(SegmentUploadState segmentUploadState,String code,TaskManager taskManager,MinioBackMessage minioBackMessage,ReqSliceServer reqSliceServer) {
        System.out.println("案发发个广告"+minioBackMessage);
//        MinioBackMessage minioBackMessage1 = GsonUtils.fromJson(GsonUtils.toJson(reqSliceServer.getData()), MinioBackMessage.class);
//        System.out.println(minioBackMessage1);
        String newCode = "";
        if (code.equals("6013")) {
            newCode = "6003";
        } else if (code.equals("6014")) {
            newCode = "6004";
        } else if (code.equals("6015")) {
            newCode = "6005";
        }

        if (segmentUploadState.getSegmentUploadComplete().equals("")){
            if (segmentUploadState.getSegmentUpload().contains(newCode)){
                if (segmentUploadState.getSegmentUpload().contains(",")){
                    if (segmentUploadState.getSegmentUpload().contains(","+newCode)){
                        segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace(","+newCode,""));
                    }else if (segmentUploadState.getSegmentUpload().contains(newCode+",")){
                        segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace(newCode+",",""));

                    }
                }else {
                    segmentUploadState.setSegmentUpload("");
                }
            }
            segmentUploadState.setSegmentUploadComplete(code);
        }else {

        }
//        if (segmentUploadState.getSegmentUploadComplete().equals("")) {
//
//
//            if (segmentUploadState.getSegmentUpload().contains(newCode)) {
//
//                if (segmentUploadState.getSegmentUpload().contains(",")) {
//
//                    if (segmentUploadState.getSegmentUpload().contains("," + newCode)) {
//                        segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace("," + newCode, ""));
//                    } else if (segmentUploadState.getSegmentUpload().contains(newCode + ",")) {
//                        segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace(newCode + ",", ""));
//                    }
//                } else {
////
//                    segmentUploadState.setSegmentUpload("");
//                }
//            }
//
//            segmentUploadState.setSegmentUploadComplete(code);
//
//        } else {
//
//            if (segmentUploadState.getSegmentUpload().contains(newCode)) {
//
//                if (segmentUploadState.getSegmentUpload().contains(",")) {
//
//                    if (segmentUploadState.getSegmentUpload().contains("," + newCode)) {
//                        segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace("," + newCode, ""));
//                    } else if (segmentUploadState.getSegmentUpload().contains(newCode + ",")) {
//                        segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace(newCode + ",", ""));
//                    }
//                } else {
////
//                    segmentUploadState.setSegmentUpload("");
//                }
//            }
//
//            if (segmentUploadState.getSegmentUploadComplete().equals("")) {
//                segmentUploadState.setSegmentUploadComplete(code);
//            } else {
//                if (segmentUploadState.getSegmentUploadComplete().equals(code)){
//
//                }else {
//                    segmentUploadState.setSegmentUploadComplete(segmentUploadState.getSegmentUploadComplete() + "," + code);
//                }
//
//            }
//        }
        String resolvingPower = "";
        String minioId = taskManager.getMinioId();
        MinioInfo minioInfo = new MinioInfo();
        if (code.equals("6013")) {
            resolvingPower = "720";
        } else if (code.equals("6014")) {
            resolvingPower = "480";
        } else if (code.equals("6015")) {
            resolvingPower = "320";
        }
        Integer newMinioId = 0;
        taskManager.setUploadState(GsonUtils.toJson(segmentUploadState));
        taskManager.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
        System.out.println(taskManager);
        taskManagerService.updateUploadState(taskManager.getFilmId(), taskManager);

        if (minioId.contains(",")) {
            String[] split = minioId.split(",");

            for (int i = 0; i < split.length; i++) {
                MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(split[i]));
                if (minio.getResolvingPower().equals(resolvingPower)) {
                    minioInfo = minio;
                    newMinioId = minioInfo.getId();
                }
            }
        } else {
            MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(minioId));
            if (minio.getResolvingPower().equals(resolvingPower)) {
                minioInfo = minio;
                newMinioId = minioInfo.getId();
            }
        }
        System.out.println("解析出来的消息"+minioBackMessage);
        //原本的filmSize
        double filmSize = Double.valueOf(minioBackMessage.getOriginalSize());

        //应该要减去的大小
        double actualSize = Double.valueOf(turnGB(minioBackMessage.getActualSize()));
        minioInfo.setAvailableCapacity(minioInfo.getAvailableCapacity() + filmSize - actualSize);
        minioInfo.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));

        minioInfoService.updateMinio(minioInfo, minioInfo.getId());



        List<UploadUrl> urlList = new ArrayList<>();
        UploadUrl uploadUrl = uploadUrl(resolvingPower, minioBackMessage.getUrl());
        urlList.add(uploadUrl);

        VisitUrl visitUrl = newVisitUrl(taskManager,urlList);
        VisitUrl visitUrl1 = visitService.selectByDouBanId(visitUrl.getDoubanId());

        if (visitUrl1.getMinioUrl().equals("")) {
            List<NginxUrlInfo> nginxUrlInfoList = new ArrayList<>();
//            NginxUrlInfo nginxUrlInfo = new NginxUrlInfo();
//            nginxUrlInfo.setResolving(uploadUrl.getResolving());
//
//            NginxManager nginxManager = nginxManagerService.selectByMinioId(newMinioId);

//            String nginxUrl = newNginxUrl(minioBackMessage.getUrl(),nginxManager.getNginxUrl());
//            nginxUrlInfo.setUrl(nginxUrl);
//            nginxUrlInfoList.add(nginxUrlInfo);
            List<UploadUrl> uploadUrls = new ArrayList<>();

            UploadUrl uploadUrl1 = uploadUrl(resolvingPower, minioBackMessage.getUrl());

            uploadUrls.add(uploadUrl1);
            visitUrl1.setNginxUrl(GsonUtils.toJson(nginxUrlInfoList));
            visitUrl1.setMinioUrl(GsonUtils.toJson(uploadUrls));
            visitUrl1.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
            visitService.updateFilmId(visitUrl.getDoubanId(),visitUrl1);
        } else {
            UploadUrl uploadUrl1 = new UploadUrl();
//            NginxUrlInfo nginxUrlInfo = new NginxUrlInfo();

            System.out.println(visitUrl1.getMinioUrl());

//            NginxManager nginxManager = nginxManagerService.selectByMinioId(newMinioId);

            List<UploadUrl> uploadUrlList = gson.fromJson(visitUrl1.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
            }.getType());


            List<NginxUrlInfo> nginxUrlInfoList = gson.fromJson(visitUrl1.getNginxUrl(), new TypeToken<List<NginxUrlInfo>>() {
            }.getType());

//            for (int i = 0;i<nginxUrlInfoList.size();i++){
//                if (nginxUrlInfoList.get(i).getResolving().equals(resolvingPower)){
//                    nginxUrlInfoList.get(i).setUrl(minioBackMessage.getUrl());
//                }else {
//                    String nginxUrl = newNginxUrl(minioBackMessage.getUrl(), nginxManager.getNginxUrl());
//                    nginxUrlInfo.setResolving(resolvingPower);
//                    nginxUrlInfo.setUrl(nginxUrl);
//                }
//            }

            for (int i = 0; i < uploadUrlList.size(); i++) {
                if (uploadUrlList.get(i).getResolving().equals(resolvingPower)){
                    uploadUrlList.get(i).setUrl(minioBackMessage.getUrl());
                }else {

                    uploadUrl1.setResolving(resolvingPower);
                    uploadUrl1.setUrl(minioBackMessage.getUrl());
                }
            }
//            nginxUrlInfoList.add(nginxUrlInfo);
            uploadUrlList.add(uploadUrl);
            visitUrl.setNginxUrl(GsonUtils.toJson(nginxUrlInfoList));
            visitUrl.setMinioUrl(GsonUtils.toJson(uploadUrlList));
            visitService.updateFilmId(taskManager.getFilmId(), visitUrl);
        }


//            filmSourceRecord.setVisitUrlId(String.valueOf());
        FilmSourceRecord film = filmSourceService.findFilm(taskManager.getFilmId());
        if (film == null) {
            FilmSourceRecord filmSourceRecord = newFilmSourceRecord(taskManager);
            IPage<FilmInfo> infoIPage = filmInfoService.selectByDouBanId(taskManager.getDoubanId());


            List<FilmInfo> filmInfos = infoIPage.getRecords();

            filmInfos.get(0).setWhetherUpload("1");
            filmInfoService.updateByFilmInfoId(filmInfos.get(0));


            filmSourceRecord.setVisitUrlId(String.valueOf(visitUrl1.getId()));
            filmSourceRecord.setFilmInfoId(filmInfos.get(0).getId());
            filmSourceService.addFilmSource(filmSourceRecord);
        }

//        }

    }
    public UploadUrl uploadUrl(String resolvingPower,String uploadUrl){
        UploadUrl uploadUrl1 = new UploadUrl();
        uploadUrl1.setResolving(resolvingPower);
        uploadUrl1.setUrl(uploadUrl);

        return uploadUrl1;
    }
    public static VisitUrl newVisitUrl(TaskManager taskManager,List<UploadUrl> list){
        VisitUrl visitUrl = new VisitUrl();
        visitUrl.setCdnUrl("");
        visitUrl.setNginxUrl("");
        visitUrl.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        visitUrl.setDoubanId(taskManager.getDoubanId());
        visitUrl.setMinioUrl(GsonUtils.toJson(list));
//        visitUrl.setFilmId(taskManager.getFilmId());
        visitUrl.setNginxUrl("");
        return  visitUrl;
    }
    public static FilmSourceRecord newFilmSourceRecord(TaskManager taskManager){
        FilmSourceRecord filmSourceRecord = new FilmSourceRecord();
        filmSourceRecord.setBtUrl(taskManager.getBtUrl());
        filmSourceRecord.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000));
        filmSourceRecord.setFilmId(taskManager.getFilmId());
        filmSourceRecord.setFilmName(taskManager.getFilmName());

        filmSourceRecord.setSubtitleUrl(taskManager.getSubtitleUrl());
        filmSourceRecord.setResolvingPower(taskManager.getResolvingPower());
//            filmSourceRecord.setMinioUrl(taskManager1.getMinioUrl());
        filmSourceRecord.setUpdateTime("");
        filmSourceRecord.setLanguageId(taskManager.getLanguageId());
        return  filmSourceRecord;
    }

    public String newNginxUrl(String uploadUrl,String nginxUrl){

        Integer id1 = uploadUrl.indexOf("//");
        Integer id2 = uploadUrl.indexOf("film");
        String newStr = uploadUrl.substring(id1+2,id2+4);
        String replace = uploadUrl.replace(newStr, nginxUrl);

        return replace;
    }
    /**
     * 上传失败
     * @param reqSliceServer
     */
    private void uploadFail(ReqSliceServer reqSliceServer){
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        String uploadState = String.valueOf(taskManager.getUploadState());
        String code = String.valueOf(reqSliceServer.getCode());
        SegmentUploadState segmentUploadState = GsonUtils.fromJson(uploadState, SegmentUploadState.class);

        switch (code){
            case "6023":
                uploadFailDecide(segmentUploadState,code);
                break;
            case "6024":
                uploadFailDecide(segmentUploadState,code);
                break;
            case "6025":
                uploadFailDecide(segmentUploadState,code);
                break;
        }
        taskManager.setUploadState(GsonUtils.toJson(segmentUploadState));
        taskManager.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
        taskManagerService.updateUploadState(taskManager.getFilmId(),taskManager);
    }
    private void uploadFailDecide(SegmentUploadState segmentUploadState,String code){
        if (segmentUploadState.getSegmentUploadFail().equals("")){
            segmentUploadState.setSegmentUploadFail(code);
        }else {
            if (segmentUploadState.getSegmentUploadFail().contains(code)){

            }else {
                segmentUploadState.setSegmentUploadFail(segmentUploadState.getSegmentUploadFail()+","+code);
            }
        }

    }
    private void reptile(ReqSliceServer reqSliceServer,ResData res) {

        FilmInfo filmInfo = GsonUtils.fromJson(reqSliceServer.getData().toString(), FilmInfo.class);
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());

//        filmInfoService.insertFilmInfo(filmInfo);
        System.out.println("爬虫成功进来了");
        IPage<FilmInfo> infoIPage = filmInfoService.selectByDouBanId(String.valueOf(filmInfo.getDoubanId()));
        List<FilmInfo> filmInfos = infoIPage.getRecords();
        taskManager.setWhetherClimb(0);
        taskManager.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
        taskManagerService.updateUploadState(taskManager.getFilmId(),taskManager);


        if (filmInfos.size() == 0) {
            System.out.println(filmInfo);
            filmInfo.setWhetherUpload("1");
            filmInfoService.insertFilmInfo(filmInfo);
        }else {

        }
//        if (filmInfos.size() > 0) {
//
//        } else {
//            System.out.println("爬取的数据为:" + filmInfo);
//            filmInfoService.insertFilmInfo(filmInfo);
//        }

    }


    public static String turnGB(String size){

        if (size.contains("G")){
            return size.replace("G","");
        }else if (size.contains("M")){
            String m = size.replace("M", "");
            Double aDouble = Double.valueOf(m);
            return String.valueOf(aDouble / 1024);
        }else if (size.contains("K")){
            String k = size.replace("K", "");
            Double aDouble = Double.valueOf(k);
            return String.valueOf(aDouble / 1024 / 1024);
        }
        return null;
    }
}
