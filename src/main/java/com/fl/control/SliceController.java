package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.*;
import com.fl.model.Msg;
import com.fl.model.SegmentState;
import com.fl.model.SegmentUploadState;
import com.fl.model.UploadUrl;
import com.fl.model.clientRes.ReqSliceServer;
import com.fl.model.clientRes.ResData;
import com.fl.model.sliceServerReq.BitTorrent;
import com.fl.model.sliceServerReq.MinioBackMessage;
import com.fl.model.sliceServerRes.ResSegmentManager;
import com.fl.service.*;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
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

    private ResData res = new ResData();
    private ResSegmentManager resData = new ResSegmentManager();

    private Msg msg = new Msg();
    private ResSegmentManager resSegment = new ResSegmentManager();
    @ApiOperation("分配任务")
    @PostMapping(value = "/distributionTask",produces = "application/json;charset=UTF-8")
    public ResSegmentManager distributionTask(){

        Map<String,String> serverInfo = new HashMap<>();

        IPage<TaskManager> taskManagerIPage = taskManagerService.selectOneTask("0", 0, 1);
        List<TaskManager> listTask = new ArrayList<>();

        listTask = taskManagerIPage.getRecords();
        System.out.println(listTask);
        if (listTask.size()>0){
            if (!listTask.get(0).getResolvingPower().contains(",")){

                MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(listTask.get(0).getMinioId()));
                System.out.println();
                Msg msg = GsonUtils.fromJson(String.valueOf(minio.getMsg()), Msg.class);
                System.out.println(msg);
                serverInfo.put(minio.getResolvingPower()+"P",String.valueOf(msg.getMsg()));

                taskManagerService.updateIdTask(listTask.get(0).getFilmId(),"0");

//                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setResolvingPower(listTask.get(0).getResolvingPower()+"P");
                resSegment.setFilmId(listTask.get(0).getFilmId());
                resSegment.setBtUrl(listTask.get(0).getBtUrl());
                resSegment.setSubtitleSuffix(listTask.get(0).getSubtitleSuffix());
                resSegment.setMsg(serverInfo);
                resSegment.setFilmSize(listTask.get(0).getFilmSize());
                resSegment.setDoubanId(listTask.get(0).getDoubanId());
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

                resSegment.setSubtitleUrl(listTask.get(0).getSubtitleUrl());
                resSegment.setResolvingPower(str);
                resSegment.setFilmId(listTask.get(0).getFilmId());
                resSegment.setBtUrl(listTask.get(0).getBtUrl());
                resSegment.setMsg(serverInfo);
                resSegment.setSubtitleSuffix(listTask.get(0).getSubtitleSuffix());
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
    public ResData taskComplete(@RequestBody ReqSliceServer reqSliceServer){

        Integer id = reqSliceServer.getCode();
        System.out.println(reqSliceServer);
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        BitTorrent bitTorrent = GsonUtils.fromJson(reqSliceServer.getData(), BitTorrent.class);

        String filmId = taskManager.getFilmId();
        String state = String.valueOf(reqSliceServer.getCode());
        //对请求的code进行判断处理

        if (id == 2){
            BtDownLoad btDownLoad = btDownLoadService.selectByFilmId(reqSliceServer.getFilmId());
            if (btDownLoad == null){
                BtDownLoad bt = new BtDownLoad();
                bt.setFilmId(reqSliceServer.getFilmId());
                bt.setBtState(bitTorrent.getDone());
                bt.setStartTime(String.valueOf(System.currentTimeMillis()/1000));

                btDownLoadService.insertBtDownLoad(bt);
            }else {
                if (btDownLoad.getBtState().equals(bitTorrent.getDone())){
                    long currentTime = System.currentTimeMillis()/1000;
                    long startTime =Long.valueOf(btDownLoad.getStartTime());

                    long time = currentTime - startTime;
                    if (time> JwtUtils.EXPIRE_TIME){
                        res.setCode(1);
                        res.setMsg("任务超时");
                        res.setData(btDownLoad);

                        taskManagerService.updateIdLinkState(filmId,"1003");
                        return res;
                    }else {

                    }
                }else {
                    btDownLoad.setStartTime(String.valueOf(System.currentTimeMillis()/1000));
                    btDownLoad.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));

                    res.setCode(0);
                    res.setMsg("任务可以继续下载");
                    res.setData(btDownLoad);

                    btDownLoadService.updateBtDownLoad(btDownLoad.getFilmId(),btDownLoad);

                    return res;
                }
            }
            return  res;
        }
        reqTaskState(id,filmId,state,reqSliceServer);

        res.setCode(0);
        res.setMsg("ok");
        res.setData("");

        return res;
    }
    private void reqTaskState(Integer id,String filmId,String state,ReqSliceServer reqSliceServer){
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


                FilmInfo filmInfo = new FilmInfo();


                break;
        }
    }

    /**
     * 开始切片状态
     * @param reqSliceServer
     */
    private void segmentStart(ReqSliceServer reqSliceServer) {
        TaskManager taskManager = taskManagerService.selectByFilmId(reqSliceServer.getFilmId());
        String segmentState = String.valueOf(taskManager.getSegmentState());
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
        if (segmentState.getSegmentSuccess().equals("")){

            segmentState.setSegmentSuccess(code);
        }else {
            if (segmentState.getSegmentSuccess().contains(code)){

            }else {
                segmentState.setSegmentSuccess(segmentState.getSegmentSuccess()+","+code);
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

            if (!uploadState.equals("")){
                SegmentUploadState segmentUploadState = GsonUtils.fromJson(uploadState, SegmentUploadState.class);

                segmentUploadState.setSegmentUploadComplete(code);

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
                taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(),GsonUtils.toJson(segmentUploadState));
            }else {
                SegmentUploadState segmentUploadState = new SegmentUploadState();
                switch (code){
                    case "6003":
                        segmentUploadState.setSegmentUpload(code);
                        break;
                    case "6004":
                        segmentUploadState.setSegmentUpload(code);
                        break;
                    case "6005":
                        segmentUploadState.setSegmentUpload(code);
                        break;
                }
                taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(),GsonUtils.toJson(segmentUploadState));
            }
        }else {
            SegmentUploadState segmentUploadState = GsonUtils.fromJson(uploadState, SegmentUploadState.class);
            MinioBackMessage minioBackMessage = GsonUtils.fromJson(reqSliceServer.getData(), MinioBackMessage.class);
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
            taskManagerService.updateIdSegmentState(reqSliceServer.getFilmId(),GsonUtils.toJson(segmentUploadState));
        }
    }
    private void uploadDecide(SegmentUploadState segmentUploadState,String code,TaskManager taskManager,MinioBackMessage minioBackMessage,ReqSliceServer reqSliceServer) {
        MinioBackMessage minioBackMessage1 = GsonUtils.fromJson(reqSliceServer.getData(), MinioBackMessage.class);


        if (segmentUploadState.getSegmentUploadComplete().equals("")) {
            segmentUploadState.setSegmentUploadComplete(code);
        } else {
            if (segmentUploadState.getSegmentUploadComplete().contains(code)) {

            } else {
                segmentUploadState.setSegmentUploadComplete(segmentUploadState.getSegmentUploadComplete() + "," + code);
            }
        }
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
        List<UploadUrl> listUpload = new ArrayList<>();
        if (taskManager.getMinioUrl().equals("")){
            UploadUrl uploadUrl = new UploadUrl();
            uploadUrl.setResolving(resolvingPower);
            uploadUrl.setUrl(minioBackMessage1.getUrl());

            listUpload.add(uploadUrl);
            taskManager.setMinioUrl(GsonUtils.toJson(listUpload));
        }else {
            UploadUrl uploadUrl = GsonUtils.fromJson(taskManager.getMinioUrl(), UploadUrl.class);
            listUpload.add(uploadUrl);

            UploadUrl uploadUrl1 = new UploadUrl();

            uploadUrl1.setResolving(resolvingPower);
            uploadUrl1.setUrl(minioBackMessage1.getUrl());
            listUpload.add(uploadUrl1);

            taskManager.setMinioUrl(GsonUtils.toJson(listUpload));
        }
        taskManager.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
//
        taskManagerService.updateUploadState(taskManager.getFilmId(),taskManager);


//        filmInfoService.selectById();

        if (minioId.contains(",")) {
            String[] split = minioId.split(",");

            for (int i = 0; i < split.length; i++) {
                MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(split[i]));
                if (minio.getResolvingPower().equals(resolvingPower)) {
                    minioInfo = minio;
                }
            }
        } else {
            MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(minioId));
            if (minio.getResolvingPower().equals(resolvingPower)) {
                minioInfo = minio;
            }
        }


        TaskManager taskManager1 = taskManagerService.selectByFilmId(taskManager.getFilmId());
        SegmentUploadState segmentUploadState1 = GsonUtils.fromJson(String.valueOf(taskManager1.getUploadState()), segmentUploadState.getClass());


        //原本的filmSize
        double filmSize = Double.valueOf(minioBackMessage.getOriginalSize());
        //应该要减去的大小
        double actualSize = Double.valueOf(minioBackMessage.getActualSize());
        minioInfo.setAvailableCapacity(minioInfo.getAvailableCapacity() + filmSize - actualSize);
        minioInfo.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));

        minioInfoService.updateMinio(minioInfo, minioInfo.getId());

        String resolvingPower1 = taskManager.getResolvingPower();
        String str = "";

        if (resolvingPower1.contains(",")) {
            String[] split = resolvingPower1.split(",");
            for (int i = 0; i < split.length; i++) {
                if (split[i].equals("720")) {
                    if (str.equals("")) {
                        str = "6003";
                    } else {
                        str = str + "," + "6003";
                    }
                } else if (split[i].equals("480")) {
                    if (str.equals("")) {
                        str = "6004";
                    } else {
                        str = str + "," + "6004";
                    }
                } else if (split[i].equals("320")) {
                    if (str.equals("")) {
                        str = "6005";
                    } else {
                        str = str + "," + "6005";
                    }
                }
            }
        } else {
            if (resolvingPower1.equals("720")) {
                str = "6003";
            } else if (resolvingPower1.equals("480")) {
                str = "6004";
            } else if (resolvingPower1.equals("320")) {
                str = "6005";
            }
        }
        if (segmentUploadState1.getSegmentUploadComplete().equals(code)) {

            VisitUrl visitUrl = newVisitUrl(taskManager1);
            VisitUrl visitUrl1 = visitService.selectByFilmId(visitUrl.getFilmId());
            if (visitUrl1 != null){
                visitService.insertVisitUrl(visitUrl);
            }else {
                visitService.updateFilmId(visitUrl.getFilmId(),visitUrl);
            }

            FilmSourceRecord filmSourceRecord = newFilmSourceRecord(taskManager, str);
            filmSourceService.addFilmSource(filmSourceRecord);
        }

    }
    public static VisitUrl newVisitUrl(TaskManager taskManager){
        VisitUrl visitUrl = new VisitUrl();
        visitUrl.setCdnUrl("");
        visitUrl.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        visitUrl.setFilmId(taskManager.getFilmId());
        visitUrl.setMinioUrl(taskManager.getMinioUrl());
        visitUrl.setFilmId(taskManager.getFilmId());
        visitUrl.setNginxUrl("");
        return  visitUrl;
    }
    public static FilmSourceRecord newFilmSourceRecord(TaskManager taskManager,String str){
        FilmSourceRecord filmSourceRecord = new FilmSourceRecord();
        filmSourceRecord.setBtUrl(taskManager.getBtUrl());
        filmSourceRecord.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000));
        filmSourceRecord.setFilmId(taskManager.getFilmId());
        filmSourceRecord.setFilmName(taskManager.getFilmName());

        filmSourceRecord.setSubtitleUrl(taskManager.getSubtitleUrl());
        filmSourceRecord.setResolvingPower(str);
//            filmSourceRecord.setMinioUrl(taskManager1.getMinioUrl());
        filmSourceRecord.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
        filmSourceRecord.setLanguageId(taskManager.getLanguageId());
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
}
