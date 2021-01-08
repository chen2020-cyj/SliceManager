package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.aop.annotation.Log;
import com.fl.entity.*;
import com.fl.model.FilmInfoData;
import com.fl.model.ResFilmInfoMapper;
import com.fl.model.UploadUrl;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.*;
import com.fl.service.*;
import com.fl.utils.FileUtils;
import com.fl.utils.MinioPicUpLoad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PreAuthorize("@zz.check('menu:filmInfo')")
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
        if (findAllFilmInfo.getCategoryId().equals("1")) {
            if (!findAllFilmInfo.getFilmId().equals("")){
                ResFilmInfoSource resFilmInfoSource = new ResFilmInfoSource();
                FilmInfo filmInfo = filmInfoService.selectById(Integer.valueOf(findAllFilmInfo.getFilmId()));
                if(filmInfo == null){
                    resFilmData.setCode(1);
                    resFilmData.setMsg("id输入错误");
                    resFilmData.setData("");
                    resFilmData.setTotal(0);

                    return resFilmData;
                }
                List<FilmSourceRecord> filmSourceRecords = filmSourceService.selectByFilmInfoId(filmInfo.getId());


                if (filmSourceRecords.size() >0){

                    LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecords.get(0).getLanguageId()));

                    List<ResFilmSource> resFilmSourceList = new ArrayList<>();
                    ResFilmSource resFilmSource = new ResFilmSource();
                    for (int i = 0; i < filmSourceRecords.size(); i++) {
                        resFilmSource = resSource(filmSourceRecords.get(i), languageInfo.getLanguage());

                        VisitUrl visitUrl = visitService.selectById(Integer.valueOf(filmSourceRecords.get(0).getVisitUrlId()));
                        List<UploadUrl> uploadUrlList = new ArrayList<>();

                        if (visitUrl != null){
                            uploadUrlList = gson.fromJson(visitUrl.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
                            }.getType());
                        }
                        if (uploadUrlList.size() == 0){
                            resFilmSource.setMinioUrl("");
                        }else {
                            Map<String,String> map1 = new HashMap<>();
                            for (int j = 0; j < uploadUrlList.size(); j++) {

                                map1.put(uploadUrlList.get(j).getResolving(),uploadUrlList.get(j).getUrl());
                            }
                            resFilmSource.setMinioUrl(map1);
                        }
                        resFilmSourceList.add(resFilmSource);
                    }

                    resFilmInfoSource.setFilmInfo(filmInfo);
                    resFilmInfoSource.setResFilmSource(resFilmSourceList);
                    list.add(resFilmInfoSource);
                }else {

                    resFilmInfoSource.setFilmInfo(filmInfo);
                    resFilmInfoSource.setResFilmSource(null);
                    list.add(resFilmInfoSource);
                }
                filmInfoData.setList(list);
                filmInfoData.setMinioInfoList(minioInfoList);
                resFilmData.setCode(0);
                resFilmData.setMsg("success");
                resFilmData.setData(filmInfoData);
                resFilmData.setTotal(1);

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
                        LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecords.get(0).getLanguageId()));

                        List<ResFilmSource> resFilmSourceList = new ArrayList<>();
                        ResFilmSource resFilmSource = new ResFilmSource();
                        for (int j = 0; j < filmSourceRecords.size(); j++) {
                            resFilmSource = resSource(filmSourceRecords.get(j), languageInfo.getLanguage());

                            VisitUrl visitUrl = visitService.selectById(Integer.valueOf(filmSourceRecords.get(0).getVisitUrlId()));
                            List<UploadUrl> uploadUrlList = new ArrayList<>();

                            if (visitUrl != null){
                                uploadUrlList = gson.fromJson(visitUrl.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
                                }.getType());
                            }
                            if (uploadUrlList.size() == 0){
                                resFilmSource.setMinioUrl("");
                            }else {
                                Map<String,String> map1 = new HashMap<>();
                                for (int k = 0; k < uploadUrlList.size(); k++) {

                                    map1.put(uploadUrlList.get(k).getResolving(),uploadUrlList.get(k).getUrl());
                                }
                                resFilmSource.setMinioUrl(map1);
                            }
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
        } else{
                return null;
        }
    }
    private ResFilmSource resSource(FilmSourceRecord filmSourceRecord, String language){
        ResFilmSource resFilmSource = resFilmSource(filmSourceRecord);
        resFilmSource.setLanguage(language);

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

//    @ApiOperation("根据Id查找影片信息")
//    @PostMapping(value = "/selectIdFilmInfo", produces = "application/json;charset=UTF-8")
//    public ResFilmData selectIdFilmInfo(@RequestBody FindFilmInfoById findFilmInfoById) {
//
//        FilmInfo filmInfo = filmInfoService.selectById(findFilmInfoById.getFilmInfoId());
//        ResFilmInfoSource resFilmInfoSource = new ResFilmInfoSource();
//        FilmSourceRecord filmSourceRecord = filmSourceService.selectByFilmInfoId(filmInfo.getId());
//        if (filmSourceRecord != null) {
//
//            LanguageInfo languageInfo = languageInfoService.selectByLanguage(filmSourceRecord.getLanguageId());
//            VisitUrl visitUrl = visitService.selectByFilmId(filmSourceRecord.getFilmId());
//
//            List<UploadUrl> uploadUrlList = gson.fromJson(visitUrl.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
//            }.getType());
//
//            ResFilmSource resFilmSource = resFilmSource(filmSourceRecord);
//            resFilmSource.setLanguage(languageInfo.getLanguage());
//            resFilmSource.setMinioUrl(uploadUrlList);
//
//            resFilmInfoSource.setFilmInfo(filmInfo);
//            resFilmInfoSource.setResFilmSource(resFilmSource);
//        }else {
//            resFilmInfoSource.setFilmInfo(filmInfo);
//            resFilmInfoSource.setResFilmSource(null);
//        }
//
//
//        resFilmData.setCode(0);
//        resFilmData.setMsg("success");
//        resFilmData.setData(resFilmInfoSource);
//        resFilmData.setTotal(1);
//
//        return resFilmData;
//
//    }
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

//        updateInfo.set
//        String url = FileUtils.fixFileName(updateInfo.getFilmCoverImage(), String.valueOf(filmInfo.getId()));
//        if (url.equals("") || url == null){
//            resFilmData.setCode(1);
//            resFilmData.setMsg("修改失败");
//            resFilmData.setData("");
//            resFilmData.setTotal(1);
////
//            return resFilmData;
//        }

        filmInfoService.updateByFilmInfoId(filmInfo);

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
