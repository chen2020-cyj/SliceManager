package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.*;
import com.fl.kafka.data.KafkaMessage;
import com.fl.kafka.data.KafkaTitles;
import com.fl.kafka.producer.KafkaProducer;
import com.fl.model.*;
import com.fl.model.clientRes.ReqSliceServer;
import com.fl.model.clientRes.ResData;
import com.fl.model.sliceServerReq.BitTorrent;
import com.fl.model.sliceServerReq.MinioBackMessage;
import com.fl.model.sliceServerRes.ResSegmentManager;
import com.fl.service.*;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
import com.fl.utils.StateCodeUtils;
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
    private LanguageInfoService languageInfoService;

    private ResData res = new ResData();
    private Gson gson = new Gson();

    private Msg msg = new Msg();

    @ApiOperation("分配任务")
    @PostMapping(value = "/distributionTask",produces = "application/json;charset=UTF-8")
    public ResSegmentManager distributionTask(){
        ResSegmentManager resSegment = new ResSegmentManager();

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

                resSegment.setSubtitleUrl("");
                resSegment.setResolvingPower(listTask.get(0).getResolvingPower()+"P");
                resSegment.setFilmId(listTask.get(0).getFilmId());
                resSegment.setFilmRandom(listTask.get(0).getFilmRandom());
                resSegment.setBtUrl(listTask.get(0).getBtUrl());
                resSegment.setSubtitleSuffix("");
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
                resSegment.setSubtitleUrl("");
                resSegment.setResolvingPower(str);
                resSegment.setFilmId(listTask.get(0).getFilmId());
                resSegment.setFilmRandom(listTask.get(0).getFilmRandom());
                resSegment.setBtUrl(listTask.get(0).getBtUrl());
                resSegment.setMsg(serverInfo);
                resSegment.setSubtitleSuffix("");
                resSegment.setFilmSize(listTask.get(0).getFilmSize());
                resSegment.setDoubanId(listTask.get(0).getDoubanId());
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
//                TaskManager taskManager1 = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
                taskManager.setDownloadState("2");

                taskManagerService.updateUploadState(reqSliceServer.getFilmId(),taskManager);
                btDownLoadService.insertBtDownLoad(bt);

                res.setCode(0);
                res.setMsg("ok");
                res.setData("");
                return res;

            }else {
                if (btDownLoad.getBtState().equals(bitTorrent.getDone())){
                    long currentTime = System.currentTimeMillis()/1000;
                    long startTime =Long.valueOf(btDownLoad.getStartTime()+ 600);

                    if (startTime < currentTime){
                        resData.setCode(1);
                        resData.setMsg("任务超时");
                        resData.setData(GsonUtils.toJson(btDownLoad));

                        taskManager.setLinkState("1003");
                        btDownLoadService.delByFilmId(reqSliceServer.getFilmId());
                        if (taskManager.getLinkState().equals("1003")){

                        }else {
                            taskManagerService.updateIdLinkState(filmId,"1003");
                        }

                        return resData;
                    }
                }else {
                    btDownLoad.setBtState(bitTorrent.getDone());
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

        switch (id){

            case 3:
                //bt种子下载完成
                taskManagerService.updateIdTask(filmId,state);
                break;
            case StateCodeUtils.BtFail:
                TaskManager taskManager = taskManagerService.selectByFilmId(filmId);
                if (taskManager.equals("")){
                    taskManagerService.updateIdLinkState(filmId,state);
                }
                //种子链接无效
                break;
            case 1002:
                //字幕链接无效
                taskManagerService.updateIdLinkState(filmId,state);
                break;
            case StateCodeUtils.HD_Segment_Start:
                //720开始切片
                segmentStart(reqSliceServer);
                break;
            case StateCodeUtils.HG_Segment_Start:
                //480开始切片
                segmentStart(reqSliceServer);
                break;
            case StateCodeUtils.SD_Segment_Start:
                //320开始切片
                segmentStart(reqSliceServer);
                break;
            case StateCodeUtils.HD_Segment_Success:
                //720切片完成
                segmentSuccess(reqSliceServer);
                break;
            case StateCodeUtils.HG_Segment_Success:
                //480切片完成
                segmentSuccess(reqSliceServer);
                break;
            case StateCodeUtils.SD_Segment_Success:
                //320切片完成
                segmentSuccess(reqSliceServer);
                break;
            case StateCodeUtils.HD_Segment_Fail:
                //720切片失败
                segmentFail(reqSliceServer);
                break;
            case StateCodeUtils.HG_Segment_Fail:
                //480切片失败
                segmentFail(reqSliceServer);
                break;
            case StateCodeUtils.SD_Segment_Fail:
                //320切片失败
                segmentFail(reqSliceServer);
                break;
            case StateCodeUtils.HD_Upload_Start:
                //720上传状态
                upload(reqSliceServer);
                break;
            case StateCodeUtils.HG_Upload_Start:
                //480上传状态
                upload(reqSliceServer);
                break;
            case StateCodeUtils.SD_Upload_Start:
                //320上传状态
                upload(reqSliceServer);
                break;
            case StateCodeUtils.HD_Upload_Fail:
                //720上传失败
                uploadFail(reqSliceServer);
                break;
            case StateCodeUtils.HG_Upload_Fail:
                //480上传失败
                uploadFail(reqSliceServer);
                break;
            case StateCodeUtils.SD_Upload_Fail:
                //320上传失败
                uploadFail(reqSliceServer);
                break;

        }
    }

    /**
     * 开始切片状态
     * @param reqSliceServer
     */
    private void segmentStart(ReqSliceServer reqSliceServer) {
        BtDownLoad bt = btDownLoadService.selectByFilmId(reqSliceServer.getFilmId());
        if (bt == null){

        }else {
            btDownLoadService.delByFilmId(bt.getFilmId());
        }
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        if (!taskManager.getDownloadState().equals("3")){
            taskManager.setDownloadState("3");
            taskManagerService.updateUploadState(taskManager.getFilmId(),taskManager);
        }
        String segmentState = String.valueOf(taskManager.getSegmentState());
        System.out.println("切片状态"+segmentState);
        Integer code = reqSliceServer.getCode();
        if (!segmentState.equals("")) {
            SegmentState segmentState1 = GsonUtils.fromJson(segmentState, SegmentState.class);

            switch (code) {

                case StateCodeUtils.HD_Segment_Start:
                    segmentStartDecide(segmentState1, String.valueOf(StateCodeUtils.HD_Segment_Start));
                    break;
                case StateCodeUtils.HG_Segment_Start:
                    segmentStartDecide(segmentState1, String.valueOf(StateCodeUtils.HG_Segment_Start));
                    break;
                case StateCodeUtils.SD_Segment_Start:
                    segmentStartDecide(segmentState1, String.valueOf(StateCodeUtils.HG_Segment_Start));
                    break;
            }
            taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(), GsonUtils.toJson(segmentState1));
        } else {
            SegmentState segmentState1 = new SegmentState();
            segmentState1.setSegmentFail("");
            segmentState1.setSegmentSuccess("");
            segmentState1.setSegmentStart(String.valueOf(code));

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
        Integer code = reqSliceServer.getCode();

        SegmentState segmentState1 = GsonUtils.fromJson(segmentState, SegmentState.class);

        switch (code){
            case StateCodeUtils.HD_Segment_Success:
                segmentSuccessDecide(segmentState1,String.valueOf(code));
                break;
            case StateCodeUtils.HG_Segment_Success:
                segmentSuccessDecide(segmentState1,String.valueOf(code));
                break;
            case StateCodeUtils.SD_Segment_Success:
                segmentSuccessDecide(segmentState1,String.valueOf(code));
                break;
        }
        taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(),GsonUtils.toJson(segmentState1));

    }
    private void segmentSuccessDecide(SegmentState segmentState,String code){
        System.out.println(segmentState.getSegmentSuccess().equals(""));
        if (segmentState.getSegmentSuccess().equals("") || segmentState.getSegmentSuccess() == null){
            String str = "";

            if (code.equals(String.valueOf(StateCodeUtils.HD_Segment_Success))){
                str = "2001";
            }else if (code.equals(String.valueOf(StateCodeUtils.HG_Segment_Success))){
                str = "2002";
            }else if (code.equals(String.valueOf(StateCodeUtils.SD_Segment_Success))){
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

                    segmentState.setSegmentSuccess(code);
//                }
//            }


        }else {
//            System.out.println("下层判断");
            if (segmentState.getSegmentSuccess().contains(code)) {
                System.out.println(segmentState);
            } else {

                String str = "";
                if (code.equals(String.valueOf(StateCodeUtils.HD_Segment_Success))) {
                    str = "2001";
                } else if (code.equals(String.valueOf(StateCodeUtils.HG_Segment_Success))) {
                    str = "2002";
                } else if (code.equals(String.valueOf(StateCodeUtils.SD_Segment_Success))) {
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
    }

    /**
     * 切片失败状态
     */
    private void segmentFail(ReqSliceServer reqSliceServer){
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        String segmentState = String.valueOf(taskManager.getSegmentState());
        Integer code = reqSliceServer.getCode();
        SegmentState segmentState1 = GsonUtils.fromJson(segmentState, SegmentState.class);

        switch (code){
            case StateCodeUtils.HD_Segment_Fail:
                segmentFailDecide(segmentState1,String.valueOf(code));
                break;
            case StateCodeUtils.HG_Segment_Fail:
                segmentFailDecide(segmentState1,String.valueOf(code));
                break;
            case StateCodeUtils.SD_Segment_Fail:
                segmentFailDecide(segmentState1,String.valueOf(code));
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
        Integer code =reqSliceServer.getCode();
        //切片未完成
        if (reqSliceServer.getData().equals("")){
//            System.out.println("上传状态");
            if (!uploadState.equals("")){
                SegmentUploadState segmentUploadState = GsonUtils.fromJson(uploadState, SegmentUploadState.class);

                switch (code){
                    case StateCodeUtils.HD_Upload_Start:
                        if (segmentUploadState.getSegmentUpload().contains(String.valueOf(code))){

                        }else {
                            segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
                        }
                        break;
                    case StateCodeUtils.HG_Upload_Start:
                        if (segmentUploadState.getSegmentUpload().contains(String.valueOf(code))){

                        }else {
                            segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
                        }
                        break;
                    case StateCodeUtils.SD_Upload_Start:
                        if (segmentUploadState.getSegmentUpload().contains(String.valueOf(code))){

                        }else {
                            segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload()+","+code);
                        }
                        break;
                }
//                        System.out.println(segmentUploadState);
                taskManager.setUploadState(GsonUtils.toJson(segmentUploadState));
                taskManagerService.updateUploadState(reqSliceServer.getFilmId(),taskManager);
            }else {
                SegmentUploadState segmentUploadState = new SegmentUploadState();
                segmentUploadState.setSegmentUploadComplete("");
                segmentUploadState.setSegmentUploadFail("");
                segmentUploadState.setSegmentUpload(String.valueOf(code));
                switch (code){
                    case StateCodeUtils.HD_Upload_Start:

                        break;
                    case StateCodeUtils.HG_Upload_Start:
                        segmentUploadState.setSegmentUpload(String.valueOf(code));

                        break;
                    case StateCodeUtils.SD_Upload_Start:
                        segmentUploadState.setSegmentUpload(String.valueOf(code));

                        break;
                }
                taskManager.setUploadState(GsonUtils.toJson(segmentUploadState));
                taskManagerService.updateUploadState(reqSliceServer.getFilmId(),taskManager);
            }
        }else {
            //切片完成
            SegmentUploadState segmentUploadState;
            if (uploadState.equals("")){
                segmentUploadState = new SegmentUploadState();
            }else {
                segmentUploadState = GsonUtils.fromJson(uploadState, SegmentUploadState.class);
            }
            MinioBackMessage minioBackMessage = GsonUtils.fromJson(GsonUtils.toJson(reqSliceServer.getData()), MinioBackMessage.class);
            switch (code){
                case StateCodeUtils.HD_Upload_Start:
                    uploadDecide(segmentUploadState,String.valueOf(code),taskManager,minioBackMessage,reqSliceServer);
                    break;
                case StateCodeUtils.HG_Upload_Start:
                    uploadDecide(segmentUploadState,String.valueOf(code),taskManager,minioBackMessage,reqSliceServer);
                    break;
                case StateCodeUtils.SD_Upload_Start:
                    uploadDecide(segmentUploadState,String.valueOf(code),taskManager,minioBackMessage,reqSliceServer);
                    break;
            }
//            taskManagerService.updateUploadState(reqSliceServer.getFilmId(),GsonUtils.toJson(segmentUploadState));
        }
    }
    private void uploadDecide(SegmentUploadState segmentUploadState,String code,TaskManager taskManager,MinioBackMessage minioBackMessage,ReqSliceServer reqSliceServer) {

        String newCode = "";
        String resolvingPower = "";
        if (code.equals("6003")) {
            newCode = "6013";
            resolvingPower = "720";
        } else if (code.equals("6004")) {
            newCode = "6014";
            resolvingPower = "480";
        } else if (code.equals("6005")) {
            newCode = "6015";
            resolvingPower = "320";
        }

        System.out.println("对应的清晰度为:"+resolvingPower);
        if (segmentUploadState.getSegmentUploadComplete().equals("")) {
            segmentUploadState.setSegmentUploadComplete(newCode);
        } else {
            segmentUploadState.setSegmentUploadComplete(segmentUploadState.getSegmentUploadComplete() + "," + newCode);
        }

        if (segmentUploadState.getSegmentUpload().contains(code)) {
            if (segmentUploadState.getSegmentUpload().contains("," + code)) {
                segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace("," + code, ""));
            } else if (segmentUploadState.getSegmentUpload().contains(code + ",")) {
                segmentUploadState.setSegmentUpload(segmentUploadState.getSegmentUpload().replace(code + ",", ""));
            } else {
                segmentUploadState.setSegmentUpload("");
            }
        }


        String minioId = taskManager.getMinioId();
        MinioInfo minioInfo = new MinioInfo();
//        if (code.equals("6003")) {
//
//        } else if (code.equals("6004")) {
//
//        } else if (code.equals("6005")) {
//
//        }
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
//        LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(taskManager.getLanguageId()));
//        VisitUrl visitUrl = newVisitUrl(taskManager,urlList);
        VisitUrl visitUrl1 = visitService.selectByFilmRandom(taskManager.getFilmRandom());

        if (visitUrl1.getMinioUrl().equals("")) {

            List<UploadUrl> uploadUrls = new ArrayList<>();
            UploadUrl uploadUrl1 = uploadUrl(resolvingPower, minioBackMessage.getUrl());
            uploadUrls.add(uploadUrl1);

            visitUrl1.setMinioUrl(GsonUtils.toJson(uploadUrls));
            visitUrl1.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
            visitService.updateByFilmRandom(taskManager.getFilmRandom(), visitUrl1);
        } else {

            System.out.println(visitUrl1.getMinioUrl());

            List<UploadUrl> uploadUrlList = gson.fromJson(visitUrl1.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
            }.getType());


            UploadUrl uploadUrl2 = uploadUrl(resolvingPower, minioBackMessage.getUrl());

            uploadUrlList.add(uploadUrl2);

            visitUrl1.setMinioUrl(GsonUtils.toJson(uploadUrlList));
            visitService.updateByFilmRandom(taskManager.getFilmRandom(), visitUrl1);
        }

        //查询电影信息表
        IPage<FilmInfo> infoIPage = filmInfoService.selectByDouBanId(taskManager.getDoubanId());
        List<FilmInfo> filmInfos = infoIPage.getRecords();
        //更新电影信息表 更新状态为已分配
        System.out.println(filmInfos);
        filmInfos.get(0).setWhetherUpload("1");
        filmInfoService.updateByFilmInfoId(filmInfos.get(0));

//            filmSourceRecord.setVisitUrlId(String.valueOf());
//        FilmSourceRecord film = filmSourceService.findFilm(taskManager.getDoubanId());
        FilmSourceRecord filmSourceRecord1 = filmSourceService.selectByFilmId(taskManager.getFilmId());

        KafkaProducer kafkaProducer = new KafkaProducer();

        KafkaMessage message = new KafkaMessage();
        message.setTitle(KafkaTitles.redisUpdateId);
        message.setData(filmInfos.get(0).getId());
        kafkaProducer.send(message);

        if (filmSourceRecord1 == null){
            FilmSourceRecord filmSourceRecord = newFilmSourceRecord(taskManager, visitUrl1.getId());
//        filmSourceService.addFilmSource(filmSourceRecord);
//        if (film == null) {


            filmSourceRecord.setVisitUrlId(String.valueOf(visitUrl1.getId()));
            filmSourceRecord.setFilmInfoId(filmInfos.get(0).getId());
            filmSourceService.addFilmSource(filmSourceRecord);
        }

    }
    public UploadUrl uploadUrl(String resolvingPower,String uploadUrl){
        UploadUrl uploadUrl1 = new UploadUrl();
        uploadUrl1.setResolving(resolvingPower);
        uploadUrl1.setUrl(uploadUrl);

        return uploadUrl1;
    }
//    public static VisitUrl newVisitUrl(TaskManager taskManager,List<UploadUrl> list){
//        VisitUrl visitUrl = new VisitUrl();
//
//        visitUrl.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
//        visitUrl.setFilmRandom(taskManager.getFilmRandom());
//        visitUrl.setMinioUrl(GsonUtils.toJson(list));
//
//        return  visitUrl;
//    }
    public static FilmSourceRecord newFilmSourceRecord(TaskManager taskManager,Integer visitUrlId){
        FilmSourceRecord filmSourceRecord = new FilmSourceRecord();
        filmSourceRecord.setBtUrl(taskManager.getBtUrl());
        filmSourceRecord.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000));
        filmSourceRecord.setFilmId(taskManager.getFilmId());
        filmSourceRecord.setFilmName(taskManager.getFilmName());
        filmSourceRecord.setVisitUrlId(String.valueOf(visitUrlId));
        filmSourceRecord.setSubtitleUrl("");
        filmSourceRecord.setResolvingPower(taskManager.getResolvingPower());
//            filmSourceRecord.setMinioUrl(taskManager1.getMinioUrl());
        filmSourceRecord.setUpdateTime("");
//        filmSourceRecord.setLanguageId(taskManager.getLanguageId());
        return  filmSourceRecord;
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
