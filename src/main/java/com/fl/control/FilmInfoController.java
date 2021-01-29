package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.aop.annotation.Log;
import com.fl.entity.*;
import com.fl.kafka.data.KafkaMessage;
import com.fl.kafka.data.KafkaTitles;
import com.fl.kafka.producer.KafkaProducer;
import com.fl.model.FilmInfoData;
import com.fl.model.ResFilmInfoMapper;
import com.fl.model.UploadUrl;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.*;
import com.fl.service.*;
import com.fl.utils.FileUtils;
import com.fl.utils.GsonUtils;
import com.fl.utils.MinioPicUpLoad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.bcel.internal.generic.NEW;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Api(tags = "影片信息管理接口")

@RestController
public class FilmInfoController {

    @Autowired
    private FilmInfoService filmInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private FilmSourceService filmSourceService;
    @Autowired
    private LanguageInfoService languageInfoService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private MinioInfoService minioInfoService;

    private ResFilmData resFilmData = new ResFilmData();
    private ResData res = new ResData();
    private Gson gson = new Gson();

//    @PreAuthorize("@zz.check('menu:filmInfo')")
    @Log("user:selectFilm")
    @ApiOperation("查询所有影片信息")
    @PostMapping(value = "/selectFilm", produces = "application/json;charset=UTF-8")
    public ResFilmData selectFilm(@RequestBody FindAllFilmInfo findAllFilmInfo) {


        FilmInfoData filmInfoData = new FilmInfoData();
//        ResFilmInfoSource
        ResFilmData resFilmData = new ResFilmData();
        System.out.println(findAllFilmInfo);
        List<ResFilmInfoSource> list = new ArrayList<>();
        List<MinioInfo> minioInfoList = minioInfoService.selectAllMinio();

//        if (findAllFilmInfo.getCategoryId().equals("2")) {
            if (!findAllFilmInfo.getFilmName().equals("")){

                //根据电影名称进行模糊查询
                List<FilmInfo> filmList = filmInfoService.selectByLikeName(findAllFilmInfo.getFilmName(),findAllFilmInfo.getCategoryId());
//                System.out.println(filmList);
                if (filmList == null || filmList.size() == 0){
                    resFilmData.setCode(0);
                    resFilmData.setMsg("没找到该电影");
                    resFilmData.setData("");
                    resFilmData.setTotal(1);

                    return resFilmData;
                }
                System.out.println(GsonUtils.toJson(filmList));
                List<ResFilmSource> resFilmSourceList = new ArrayList<>();
                for (int i = 0; i < filmList.size(); i++) {
                    ResFilmInfoSource resFilmInfoSource = new ResFilmInfoSource();

                    List<FilmSourceRecord> filmSourceRecords = filmSourceService.selectByFilmInfoId(filmList.get(i).getId());
                    if (filmSourceRecords.size() > 0){

                        for (int j = 0; j < filmSourceRecords.size(); j++) {
                            ResFilmSource resFilmSource = new ResFilmSource();
//                            LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecords.get(j).getLanguageId()));
                            resFilmSource = resSource(filmSourceRecords.get(j));


                            VisitUrl visitUrl = visitService.selectById(Integer.valueOf(filmSourceRecords.get(j).getVisitUrlId()));

                            List<UploadUrl> uploadList = gson.fromJson(String.valueOf(visitUrl.getMinioUrl()),new TypeToken<List<UploadUrl>>(){}.getType());

                            resFilmSource.setMinioUrl(uploadList);

                            resFilmSourceList.add(resFilmSource);
                            resFilmInfoSource.setFilmInfo(filmList.get(i));
                            resFilmInfoSource.setResFilmSource(resFilmSourceList);
                            list.add(resFilmInfoSource);
                        }
                    }else {
                        resFilmInfoSource.setFilmInfo(filmList.get(i));
                        resFilmInfoSource.setResFilmSource(null);
                        list.add(resFilmInfoSource);
                    }
                }
                filmInfoData.setList(list);
                filmInfoData.setMinioInfoList(minioInfoList);
                resFilmData.setCode(0);
                resFilmData.setMsg("success");
                resFilmData.setData(filmInfoData);
                resFilmData.setTotal(filmInfoService.selectByLikeNameCount(findAllFilmInfo.getFilmName(),findAllFilmInfo.getCategoryId()));

                return resFilmData;
            }else {
                Integer id = 0;
                Integer newId = judgmentField(findAllFilmInfo, id);
                Map<String, String> map = new HashMap<>();
                map.put("name", findAllFilmInfo.getYear());
                String year = "";
                if (!findAllFilmInfo.getYear().equals("")){
                    Search search = searchService.selectByYear(map, findAllFilmInfo.getCategoryId());
                    year = search.getLimits();
                    System.out.println(year);
                }

                List<FilmInfo> listFilmInfo = new ArrayList<>();
//                List<FilmInfo>
                ResFilmInfoMapper resFilmInfoMapper = new ResFilmInfoMapper();
                if (findAllFilmInfo.getWhetherUpload().equals("0")) {
                    resFilmInfoMapper = filmInfoService.selectMoreCondition(newId, year, findAllFilmInfo);
                    listFilmInfo = resFilmInfoMapper.getFilmInfoIPage().getRecords();
                }else if (findAllFilmInfo.getWhetherUpload().equals("1")){
                    resFilmInfoMapper = filmInfoService.selectComplete(newId, year, findAllFilmInfo);
                    listFilmInfo =resFilmInfoMapper.getFilmInfoIPage().getRecords();
                }
                for (int i = 0; i < listFilmInfo.size(); i++) {
                    ResFilmInfoSource resFilmInfoSource = new ResFilmInfoSource();

                    List<FilmSourceRecord> filmSourceRecords = filmSourceService.selectByFilmInfoId(listFilmInfo.get(i).getId());
                    if (filmSourceRecords.size() > 0) {
//                        LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecords.get(0).getLanguageId()));
                        List<ResFilmSource> resFilmSourceList = new ArrayList<>();
                        ResFilmSource resFilmSource = new ResFilmSource();
                        for (int j = 0; j < filmSourceRecords.size(); j++) {
                            resFilmSource = resSource(filmSourceRecords.get(j));


                            VisitUrl visitUrl = visitService.selectById(Integer.valueOf(filmSourceRecords.get(j).getVisitUrlId()));

                            List<UploadUrl> uploadList = gson.fromJson(String.valueOf(visitUrl.getMinioUrl()),new TypeToken<List<UploadUrl>>(){}.getType());

                            resFilmSource.setMinioUrl(uploadList);

                            resFilmSourceList.add(resFilmSource);
                        }

                        resFilmInfoSource.setFilmInfo(listFilmInfo.get(i));
                        resFilmInfoSource.setResFilmSource(resFilmSourceList);
                        list.add(resFilmInfoSource);
                    } else {

                        resFilmInfoSource.setFilmInfo(listFilmInfo.get(i));
                        resFilmInfoSource.setResFilmSource(null);
                        list.add(resFilmInfoSource);
                    }
                }
                filmInfoData.setList(list);
                filmInfoData.setMinioInfoList(minioInfoList);
                resFilmData.setCode(0);
                resFilmData.setMsg("success");
                resFilmData.setData(filmInfoData);
                resFilmData.setTotal(resFilmInfoMapper.getCount());

                return resFilmData;

            }
//        } else{
//                return null;
//        }
    }
    private ResFilmSource resSource(FilmSourceRecord filmSourceRecord){
        ResFilmSource resFilmSource = resFilmSource(filmSourceRecord);
//        resFilmSource.setLanguage(language);

        return resFilmSource;
    }
    /**
     * 判断传入的参数  返回id
     * @param findAllFilmInfo
     * @param id
     * @return
     */
    private Integer judgmentField(FindAllFilmInfo findAllFilmInfo,Integer id){
        if (findAllFilmInfo.getArea().equals("")){
            if (findAllFilmInfo.getYear().equals("")){
                if (findAllFilmInfo.getTag().equals("")){
                    //area 不传 year 不传 tag不传
                    id = 1;
                }else {
                    //area 不传 year 不传 tag传
                    id = 2;
                }
            }else {
                if (findAllFilmInfo.getTag().equals("")){
                    //area 不传 year 传 tag不传
                    id = 3;
                }else {
                    //area 不传 year 传 tag传
                    id = 4;
                }
            }
        }else {
            if (findAllFilmInfo.getYear().equals("")){
                if (findAllFilmInfo.getTag().equals("")){
                    //area 传 year 不传 tag不传
                    id = 5;
                }else {
                    //area 传 year 不传 tag传
                    id = 6;
                }
            }else {
                if (findAllFilmInfo.getTag().equals("")){
                    //area 传 year 传 tag不传
                    id = 7;
                }else {
                    //area 传 year 传 tag传
                    id = 8;
                }
            }
        }
        return id;
    }
    public static ResFilmSource resFilmSource(FilmSourceRecord filmSourceRecord) {
        ResFilmSource resFilmSource = new ResFilmSource();
        resFilmSource.setUpdateTime(filmSourceRecord.getUpdateTime());
        resFilmSource.setSubtitleUrl(filmSourceRecord.getSubtitleUrl());
        resFilmSource.setResolvingPower(filmSourceRecord.getResolvingPower());
        resFilmSource.setFilmName(filmSourceRecord.getFilmName());
        resFilmSource.setId(filmSourceRecord.getId());
        resFilmSource.setFilmInfoId(filmSourceRecord.getFilmInfoId());
        resFilmSource.setFilmId(filmSourceRecord.getFilmId());
        resFilmSource.setCreateTime(filmSourceRecord.getCreateTime());
        resFilmSource.setBtUrl(filmSourceRecord.getBtUrl());

        return resFilmSource;
    }
//    public
    @PreAuthorize("@zz.check('user:updateFilm')")
    @Log("user:updateFilmInfo")
    @ApiOperation("修改电影信息")
    @PostMapping(value = "/updateFilmInfo", produces = "application/json;charset=UTF-8")
    public ResFilmData updateFilmInfo(@RequestBody UpdateInfo updateInfo) {
//        System.out.println("afafaaf");
        FilmInfo filmInfo = filmInfoService.selectById(updateInfo.getFilmInfoId());
        System.out.println("更新数据:"+updateInfo);
        filmInfo.setChineseName(updateInfo.getChineseName());
        filmInfo.setEnglishName(updateInfo.getEnglishName());
        filmInfo.setDescription(updateInfo.getDescription());

        if (updateInfo.getFilmCoverImage().contains("@")){
//            "D:\\uploadImage\\1Arj24EgEf.jpg";
            String replace = updateInfo.getFilmCoverImage().replace("@", "/");
            MinioPicUpLoad minioPicUpLoad = new MinioPicUpLoad();
            String minioPath = minioPicUpLoad.push("test-upload", String.valueOf(filmInfo.getId()), "jpg", replace);
            filmInfo.setFilmCoverImage(minioPath);

            FileUtils.delFile(replace);
        }


        filmInfoService.updateByFilmInfoId(filmInfo);

        KafkaProducer kafkaProducer = new KafkaProducer();
        KafkaMessage message = new KafkaMessage();
        message.setTitle(KafkaTitles.redisUpdateId);
        message.setData(filmInfo.getId());
        kafkaProducer.send(message);

        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData("");
//        resFilmData.setTotal(1);

        return resFilmData;

    }

    @ApiOperation("图片上传功能")
    @PostMapping(value = "/picUpload", produces = "application/json;charset=UTF-8")
    public ResData picUpload(MultipartFile file){


        if (file == null){
            res.setCode(3);
            res.setMsg("没有图片资源");
            res.setData("");

            return res;
        }else {
            String upload = FileUtils.upload(file);
            if (upload.equals("")) {
                res.setCode(1);
                res.setMsg("图片上传失败");
                res.setData("");

                return res;
            }else {
                ResUpload resUpload = new ResUpload();
                resUpload.setUploadUrl(upload.replace("/","@"));
                res.setCode(0);
                res.setMsg("success");
                res.setData(resUpload);

                return res;
            }
        }


    }


}
