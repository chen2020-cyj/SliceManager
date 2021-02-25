package com.fl.control;

import com.fl.aop.annotation.Log;
import com.fl.entity.*;
import com.fl.model.SegmentState;
import com.fl.model.SegmentUploadState;
import com.fl.model.UploadUrl;
import com.fl.model.clientReq.AnnounceErrM3U8;
import com.fl.model.clientReq.DelM3U8;
import com.fl.model.clientReq.ReqLog;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResErrM3U8;
import com.fl.model.clientRes.ResFilmData;
import com.fl.service.*;

import com.fl.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = "m3u8文件错误上报")
@RestController
public class VideoM3U8Controller {

    @Autowired
    private VideoM3U8ErrInfoService videoM3U8ErrInfoService;
    @Autowired
    private FilmInfoService filmInfoService;
    @Autowired
    private TaskManagerService taskManagerService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private FilmSourceService filmSourceService;
    @Autowired
    private LanguageInfoService languageInfoService;
    @Autowired
    private MinioInfoService minioInfoService;


    @PreAuthorize("@zz.check('user:failureFilm')")
    @ApiOperation("查看所有错误的m3u8")
    @PostMapping(value = "/selectAllErrVideo",produces = "application/json;charset=UTF-8")
    public ResFilmData selectErrVideo(@RequestBody ReqLog reqLog){
        ResFilmData resData = new ResFilmData();

        List<VideoM3U8ErrInfo> videoM3U8ErrInfos = videoM3U8ErrInfoService.selectAll(reqLog.getPage(),reqLog.getOffset());
        List<ResErrM3U8> m3U8List = new ArrayList<>();

        Gson gson = new Gson();
        for (int i = 0; i < videoM3U8ErrInfos.size(); i++) {
            ResErrM3U8 resErrM3U8 = new ResErrM3U8();
            UploadUrl list = gson.fromJson(videoM3U8ErrInfos.get(i).getErrInfo(),UploadUrl.class);
//            videoM3U8ErrInfos.get(i).setErrInfo(list);
            FilmInfo filmInfo = filmInfoService.selectById(videoM3U8ErrInfos.get(i).getFilmInfoId());
//            LanguageInfo languageInfo = languageInfoService.selectById(videoM3U8ErrInfos.get(i).getLanguageId());
//            visitService.selectById()
            resErrM3U8.setId(videoM3U8ErrInfos.get(i).getId());
//            resErrM3U8.setLanguage(languageInfo.getLanguage());
            resErrM3U8.setUrl(list);
            resErrM3U8.setFilmName(filmInfo.getChineseName());
            resErrM3U8.setCreateTime(videoM3U8ErrInfos.get(i).getCreateTime());
            m3U8List.add(resErrM3U8);
        }
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData(m3U8List);
        resData.setTotal(videoM3U8ErrInfoService.count());
        return resData;
    }
    @ApiOperation("上报错误信息")
    @PostMapping(value = "/announceErrInfo",produces = "application/json;charset=UTF-8")
    public ResData announceErrInfo(@RequestBody AnnounceErrM3U8 announceErrM3U8){
        ResData resData = new ResData();
        VideoM3U8ErrInfo videoM3U8ErrInfo = new VideoM3U8ErrInfo();
//        LanguageInfo languageInfo = languageInfoService.selectByLanguage(announceErrM3U8.getLanguage());
        UploadUrl uploadUrl = new UploadUrl();

        uploadUrl.setResolving(announceErrM3U8.getResolving());
        uploadUrl.setUrl(announceErrM3U8.getUrl());

        videoM3U8ErrInfo.setFilmInfoId(announceErrM3U8.getFilmInfoId());
        videoM3U8ErrInfo.setErrInfo(GsonUtils.toJson(uploadUrl));
//        videoM3U8ErrInfo.setLanguageId(languageInfo.getId());
        videoM3U8ErrInfo.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));

        videoM3U8ErrInfoService.save(videoM3U8ErrInfo);
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");
        return resData;

    }

    @PreAuthorize("@zz.check('user:delM3U8')")
    @Log("user:delM3U8")
    @ApiOperation("将不能使用的m3u8路径删除")
    @PostMapping("/delM3U8")
    public ResData delM3U8(@RequestBody DelM3U8 delM3U8) {
        ResData resData = new ResData();
        List<Integer> list = new ArrayList<>();

        if (delM3U8.getId().contains(",")) {
            String[] split = delM3U8.getId().split(",");
            for (int i = 0; i < split.length; i++) {
                list.add(Integer.valueOf(split[i]));
            }
        } else {
            list.add(Integer.valueOf(delM3U8.getId()));
        }

        for (int i = 0; i < list.size(); i++) {
            VideoM3U8ErrInfo videoM3U8ErrInfo = videoM3U8ErrInfoService.selectById(list.get(i));
            List<FilmSourceRecord> filmSourceRecords = filmSourceService.selectByFilmInfoId(videoM3U8ErrInfo.getFilmInfoId());
            VisitUrl visitUrl = visitService.selectById(Integer.valueOf(filmSourceRecords.get(0).getVisitUrlId()));
//            LanguageInfo languageInfo = languageInfoService.selectById(videoM3U8ErrInfo.getLanguageId());

            Gson gson = new Gson();
            List<UploadUrl> uploadList = gson.fromJson(visitUrl.getMinioUrl(),new TypeToken<List<UploadUrl>>(){}.getType());
            UploadUrl uploadUrl = GsonUtils.fromJson(videoM3U8ErrInfo.getErrInfo(), UploadUrl.class);
            for (int j = 0; j < uploadList.size(); j++) {
                if (uploadList.get(j).getResolving().equals(uploadUrl.getResolving())){
                    uploadList.remove(j);
                }
            }

            List<TaskManager> taskManagerList = taskManagerService.selectByFilmRandom(visitUrl.getFilmRandom());

            visitUrl.setMinioUrl(GsonUtils.toJson(uploadList));
            visitUrl.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));

            visitService.updateByFilmRandom(visitUrl.getFilmRandom(), visitUrl);
            String uploadStateSuccess = "";
            String segmentStateSuccess = "";
            if (uploadUrl.getResolving().equals("720")) {
                uploadStateSuccess = "6013";
                segmentStateSuccess = "2011";
            } else if (uploadUrl.getResolving().equals("480")) {
                uploadStateSuccess = "6014";
                segmentStateSuccess = "2012";
            } else if (uploadUrl.getResolving().equals("320")) {
                uploadStateSuccess = "6015";
                segmentStateSuccess = "2013";
            }

            for (int k = 0; k < taskManagerList.size(); k++) {
//                taskManagerList.get(k).get
                if (taskManagerList.get(k).getResolvingPower().contains(uploadUrl.getResolving())) {
                    if (taskManagerList.get(k).getResolvingPower().contains(",")) {
                        SegmentState segmentState = GsonUtils.fromJson(String.valueOf(taskManagerList.get(k).getSegmentState()), SegmentState.class);
                        if (segmentState.getSegmentSuccess().contains("," + segmentStateSuccess)) {
                            segmentState.setSegmentSuccess(segmentState.getSegmentSuccess().replace("," + segmentStateSuccess, ""));
                        } else if (segmentState.getSegmentSuccess().contains(segmentStateSuccess + ",")) {
                            segmentState.setSegmentSuccess(segmentState.getSegmentSuccess().replace(segmentStateSuccess + ",", ""));
                        }
                        taskManagerList.get(k).setSegmentState(GsonUtils.toJson(segmentState));
                        SegmentUploadState segmentUploadState = GsonUtils.fromJson(String.valueOf(taskManagerList.get(k).getUploadState()), SegmentUploadState.class);
                        if (segmentUploadState.getSegmentUploadComplete().contains("," + uploadStateSuccess)) {
                            segmentUploadState.setSegmentUploadComplete(segmentUploadState.getSegmentUploadComplete().replace("," + uploadStateSuccess, ""));
                        } else if (segmentUploadState.getSegmentUploadComplete().contains(uploadStateSuccess + ",")) {
                            segmentUploadState.setSegmentUploadComplete(segmentUploadState.getSegmentUploadComplete().replace(uploadStateSuccess + ",", ""));
                        }
                        String[] split = taskManagerList.get(k).getMinioId().split(",");
                        String minioId = "";
                        for (int n = 0; n < split.length; n++) {
                            MinioInfo minio = minioInfoService.findMinio(Integer.valueOf(split[n]));
                            if (minio.getResolvingPower().equals(uploadUrl.getResolving())) {
                                minioId = String.valueOf(minio.getId());
                                break;
                            }
                        }
                        if (taskManagerList.get(k).getMinioId().contains("," + minioId)) {
                            taskManagerList.get(k).setMinioId(taskManagerList.get(k).getMinioId().replace("," + minioId, ""));
                        } else if (taskManagerList.get(k).getMinioId().contains(minioId + ",")) {
                            taskManagerList.get(k).setMinioId(taskManagerList.get(k).getMinioId().replace(minioId + ",", ""));
                        }

                        taskManagerList.get(k).setUploadState(GsonUtils.toJson(segmentUploadState));
                        taskManagerService.updateUploadState(taskManagerList.get(k).getFilmId(), taskManagerList.get(k));
                    } else {
                        taskManagerService.delTask(taskManagerList.get(k).getFilmId());
                    }
                }
            }
        }
        videoM3U8ErrInfoService.removeByIds(list);
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");

        return resData;
    }
}
