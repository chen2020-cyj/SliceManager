package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.*;
import com.fl.model.UploadUrl;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResFilmInfoSource;
import com.fl.model.clientRes.ResFilmSource;
import com.fl.service.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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


    private ResFilmData resFilmData = new ResFilmData();
    private ResData res = new ResData();
    private Gson gson = new Gson();


    @ApiOperation("查询所有影片信息")
    @PostMapping(value = "/selectFilm", produces = "application/json;charset=UTF-8")
    public ResFilmData selectFilm(@RequestBody FindAllFilmInfo findAllFilmInfo) {
//        ResFilmInfoSource

        Integer page = (findAllFilmInfo.getPage() - 1) * findAllFilmInfo.getOffset();
        Integer offset = findAllFilmInfo.getOffset();
        IPage<FilmInfo> infoIPage = filmInfoService.selectAll(page, offset);
        List<FilmInfo> filmInfos = infoIPage.getRecords();

        List<ResFilmInfoSource> list = new ArrayList<>();


        for (int i = 0; i < filmInfos.size(); i++) {
            ResFilmInfoSource resFilmInfoSource = new ResFilmInfoSource();
//            ResFilmSource resFilmSource = new ResFilmSource();
//            System.out.println(languageInfoService.selectById(1));
            FilmSourceRecord filmSourceRecord = new FilmSourceRecord();
            filmSourceRecord = filmSourceService.selectByFilmInfoId(filmInfos.get(i).getId());
            if (filmSourceRecord != null) {
                LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecord.getLanguageId()));
                VisitUrl visitUrl = visitService.selectByFilmId(filmSourceRecord.getFilmId());

                List<UploadUrl> uploadUrlList = gson.fromJson(visitUrl.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
                }.getType());


                ResFilmSource resFilmSource = resFilmSource(filmSourceRecord);
                resFilmSource.setLanguage(languageInfo.getLanguage());
                resFilmSource.setMinioUrl(uploadUrlList);

                resFilmInfoSource.setFilmInfo(filmInfos.get(i));
                resFilmInfoSource.setResFilmSource(resFilmSource);
                list.add(resFilmInfoSource);
            } else {
                resFilmInfoSource.setFilmInfo(filmInfos.get(i));
                resFilmInfoSource.setResFilmSource(null);
                list.add(resFilmInfoSource);
            }


        }

        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData(list);
        resFilmData.setTotal(filmInfoService.selectCount());

        return resFilmData;


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

    @ApiOperation("根据Id查找影片信息")
    @PostMapping(value = "/selectIdFilmInfo", produces = "application/json;charset=UTF-8")
    public ResFilmData selectIdFilmInfo(@RequestBody FindFilmInfoById findFilmInfoById) {

        FilmInfo filmInfo = filmInfoService.selectById(findFilmInfoById.getFilmInfoId());
        ResFilmInfoSource resFilmInfoSource = new ResFilmInfoSource();
        FilmSourceRecord filmSourceRecord = filmSourceService.selectByFilmInfoId(filmInfo.getId());
        if (filmSourceRecord != null) {

            LanguageInfo languageInfo = languageInfoService.selectByLanguage(filmSourceRecord.getLanguageId());
            VisitUrl visitUrl = visitService.selectByFilmId(filmSourceRecord.getFilmId());

            List<UploadUrl> uploadUrlList = gson.fromJson(visitUrl.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
            }.getType());

            ResFilmSource resFilmSource = resFilmSource(filmSourceRecord);
            resFilmSource.setLanguage(languageInfo.getLanguage());
            resFilmSource.setMinioUrl(uploadUrlList);

            resFilmInfoSource.setFilmInfo(filmInfo);
            resFilmInfoSource.setResFilmSource(resFilmSource);
        }else {
            resFilmInfoSource.setFilmInfo(filmInfo);
            resFilmInfoSource.setResFilmSource(null);
        }


        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData(resFilmInfoSource);
        resFilmData.setTotal(1);

        return resFilmData;

    }

    @ApiOperation("修改电影信息")
    @PostMapping(value = "/updateFilmInfo", produces = "application/json;charset=UTF-8")
    public ResFilmData updateFilmInfo(@RequestBody UpdateInfo updateInfo) {
//        System.out.println("afafaaf");
        FilmInfo filmInfo = filmInfoService.selectById(updateInfo.getFilmInfoId());

        filmInfo.setChineseName(updateInfo.getChineseName());
        filmInfo.setEnglishName(updateInfo.getEnglishName());
        filmInfo.setDescription(updateInfo.getDescription());
        System.out.println(updateInfo.getFile());
//        String url = FileUtils.fixFileName(updateInfo.getFilmCoverImage(), String.valueOf(filmInfo.getId()));
//        if (url.equals("") || url == null){
//            resFilmData.setCode(1);
//            resFilmData.setMsg("修改失败");
//            resFilmData.setData("");
//            resFilmData.setTotal(1);
//
//            return resFilmData;
//        }

//        MinioPicUpLoad minioPicUpLoad = new MinioPicUpLoad();
//        String minioPath = minioPicUpLoad.push("test-upload", String.valueOf(filmInfo.getId()), "jpg", url);
//
//        filmInfoService.updateByFilmInfoId(filmInfo);

        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData("");
        resFilmData.setTotal(1);

        return resFilmData;

    }

    @ApiOperation("图片上传功能")
    @PostMapping(value = "/picUpload", produces = "application/json;charset=UTF-8")
    public ResData picUpload(MultipartFile file) throws IOException {

        System.out.println(file);
//        System.out.println(GsonUtils.toJson(pic));
//        System.out.println(id);
//        System.out.println(file);
//        String upload = FileUtils.upload(file);
//        if (upload.equals("")) {
//            res.setCode(1);
//            res.setMsg("图片上传失败");
//            res.setData("");
//        } else {
//            ResUpload resUpload = new ResUpload();
//            resUpload.setUploadUrl(upload);
//            res.setCode(0);
//            res.setMsg("success");
//            res.setData(resUpload);
//        }
        return res;
    }

//    FindFilmInfo

    @ApiOperation("多条件查询")
    @PostMapping(value = "/selectMoreParameter", produces = "application/json;charset=UTF-8")
    public ResFilmData selectMoreParameter(@RequestBody FindFilmInfo findFilmInfo) throws IOException {

        Map<String, String> map = new HashMap<>();
        map.put("name", findFilmInfo.getYear());
        map.put("category_id", String.valueOf(findFilmInfo.getCategoryId()));
        Search search = searchService.selectByYear(map);
        if (search.getLimits().contains(",")) {
            String[] split = search.getLimits().split(",");
            findFilmInfo.setYear(split[0] + "," + split[1]);
        }
//        if (findFilmInfo.getYear().equals("00年代")){
//            findFilmInfo.setYear("2000,2010");
//        }else if (findFilmInfo.getYear().equals("90年代")){
//            findFilmInfo.setYear("1990,2000");
//        }else if (findFilmInfo.getYear().equals("80年代")){
//            findFilmInfo.setYear("1980,1990");
//        }else if (findFilmInfo.getYear().equals("70年代")){
//            findFilmInfo.setYear("1970,1980");
//        }else if (findFilmInfo.getYear().equals("更早")){
//            findFilmInfo.setYear("0000,1970");
//        }
        Integer page = (findFilmInfo.getPage() - 1) * findFilmInfo.getOffset();
        Integer offset = findFilmInfo.getOffset();

        IPage<FilmInfo> infoIPage = filmInfoService.selectMore(findFilmInfo, page, offset);
        List<FilmInfo> filmInfos = infoIPage.getRecords();

        List<ResFilmInfoSource> list = new ArrayList<>();

        for (int i = 0; i < filmInfos.size(); i++) {
            ResFilmInfoSource resFilmInfoSource = new ResFilmInfoSource();
            FilmSourceRecord filmSourceRecord = filmSourceService.selectByFilmInfoId(filmInfos.get(i).getId());
            if (filmSourceRecord != null) {
                LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecord.getLanguageId()));
                VisitUrl visitUrl = visitService.selectByFilmId(filmSourceRecord.getFilmId());
                ResFilmSource resFilmSource = resFilmSource(filmSourceRecord);
                resFilmSource.setLanguage(languageInfo.getLanguage());
                List<UploadUrl> uploadUrlList = gson.fromJson(visitUrl.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
                }.getType());

                resFilmSource.setMinioUrl(uploadUrlList);

                resFilmInfoSource.setFilmInfo(filmInfos.get(i));
                resFilmInfoSource.setResFilmSource(resFilmSource);
                list.add(resFilmInfoSource);
            } else {
                resFilmInfoSource.setFilmInfo(filmInfos.get(i));
                resFilmInfoSource.setResFilmSource(null);
                list.add(resFilmInfoSource);
            }

        }


        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setTotal(filmInfoService.selectCount());
        resFilmData.setData(list);
        return resFilmData;
    }
}
