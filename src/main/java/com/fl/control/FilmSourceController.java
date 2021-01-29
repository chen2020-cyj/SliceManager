package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.aop.annotation.Log;
import com.fl.entity.*;
import com.fl.model.UploadUrl;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.*;
import com.fl.service.FilmSourceService;
import com.fl.service.LanguageInfoService;
import com.fl.service.UserService;
import com.fl.service.VisitService;
import com.fl.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "片源管理")
@RestController
public class FilmSourceController {

    @Autowired
    private FilmSourceService filmSourceService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private LanguageInfoService languageInfoService;

    private Gson gson = new Gson();
    private SelectFilmReq selectReq = new SelectFilmReq();
    private ResData res = new ResData();
    private ResFilmSourceInfo filmInfo = new ResFilmSourceInfo();
    private ResFindAllFilmSource resFindAllFilmSource = new ResFindAllFilmSource();
    private ResFilmData resFilmData = new ResFilmData();

//    @ApiOperation("查询片源")
//    @PostMapping(value = "/selectFilm",produces = "application/json;charset=UTF-8")
//    public String selectFilm(@RequestBody FindFilmSource filmSource){
//
//        FilmSourceRecord film = filmSourceService.findFilm(filmSource.getFilmId());
//
//        if (film != null){
//
////            filmInfo.setFilmId(film.getFilmId());
////            filmInfo.setMinioUrl(GsonUtils.toJson(film.getMinioUrl()));
////            filmInfo.setResolvingPower(film.getResolvingPower());
//
//            resFilmData.setCode(0);
//            resFilmData.setMsg("success");
//            resFilmData.setData(filmInfo);
//            resFilmData.setTotal(1);
//
//            return GsonUtils.toJson(resFilmData);
//        }else {
//            res.setCode(1);
//            res.setMsg("err");
//            res.setData("");
//
//            return GsonUtils.toJson(res);
//        }
//    }

//    @ApiOperation("管理员查看单个片源")
//    @PostMapping(value = "/adminSelectFilm",produces = "application/json;charset=UTF-8")
//    public ResFilmData selectFilm(@RequestBody AdminFindFilm admin) {
//
//        FilmSourceRecord film = filmSourceService.findFilm(admin.getFilmId());
////        User user = userService.selectUserInfo(admin.getUserId());
////        Long currentTime = System.currentTimeMillis() / 1000;
////        Long tokenTime = Long.valueOf(user.getTokenTime());
//
//
//            if (film != null) {
//
//                LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(film.getLanguageId()));
//                VisitUrl visitUrl = visitService.selectByFilmId(film.getFilmId());
//
//                ResFilmSource resFilmSource = filmSource(film);
//                resFilmSource.setLanguage(languageInfo.getLanguage());
//                List<UploadUrl> list = gson.fromJson(visitUrl.getMinioUrl().toString(), new TypeToken<List<UploadUrl>>() {
//                }.getType());
//
//                resFilmSource.setMinioUrl(list);
//                resFilmSource.setMinioUrl(visitUrl.getMinioUrl());
//
//                resFilmData.setCode(0);
//                resFilmData.setMsg("success");
//                resFilmData.setData(resFilmSource);
//                resFilmData.setTotal(1);
//
//                return resFilmData;
//            } else {
//                resFilmData.setCode(1);
//                resFilmData.setMsg("err");
//                resFilmData.setData("");
//
//                return resFilmData;
//            }
//
//    }
    @Log("user:selectAllFilm")
    @ApiOperation("管理员查询所有片源")
    @PostMapping(value = "/selectAllFilm",produces = "application/json;charset=UTF-8")
    public ResFilmData selectAllFilm(@RequestBody FindAllFilmSource findAllFilmSource){



        if (!findAllFilmSource.getFilmId().equals("")){
            FilmSourceRecord filmSourceRecord = filmSourceService.selectByFilmId(findAllFilmSource.getFilmId());

            ResFilmSource resFilmSource = filmSource(filmSourceRecord);

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(resFilmSource);
            resFilmData.setTotal(1);

            return resFilmData;
        }else if (!findAllFilmSource.getFilmInfoId().equals("")){
            List<FilmSourceRecord> filmSourceRecords = filmSourceService.selectByFilmInfoId(Integer.valueOf(findAllFilmSource.getFilmInfoId()));

            List<ResFilmSource> list = new ArrayList<>();
            for (int i = 0; i < filmSourceRecords.size(); i++) {
                ResFilmSource resFilmSource = filmSource(filmSourceRecords.get(i));
                list.add(resFilmSource);
            }

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(list);
            resFilmData.setTotal(1);

            return resFilmData;
        }else {
            Integer offset = findAllFilmSource.getOffset();
            IPage<FilmSourceRecord> iPage = filmSourceService.selectPage(findAllFilmSource.getPage(), offset);
            List<FilmSourceRecord> filmSourceRecords = iPage.getRecords();


            List<ResFilmSource> filmData = new ArrayList<>();

            for (int i=0;i<filmSourceRecords.size();i++){


                ResFilmSource resFilmSource = filmSource(filmSourceRecords.get(i));


                filmData.add(resFilmSource);
            }

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(filmData);
            resFilmData.setTotal(filmSourceService.selectTotal());

            return resFilmData;
        }


    }

    /**
     * 转换数据格式
     * @param FilmSourceRecord
     * @return
     */
    public ResFilmSource filmSource(FilmSourceRecord FilmSourceRecord){
        ResFilmSource resFilmSource = new ResFilmSource();
        resFilmSource.setBtUrl(FilmSourceRecord.getBtUrl());
        resFilmSource.setCreateTime(FilmSourceRecord.getCreateTime());
        resFilmSource.setFilmId(FilmSourceRecord.getFilmId());
        resFilmSource.setFilmInfoId(FilmSourceRecord.getFilmInfoId());
        resFilmSource.setFilmName(FilmSourceRecord.getFilmName());
        resFilmSource.setId(FilmSourceRecord.getId());
//                resFilmSource.setLanguageId(filmSourceRecords.get(i).getLanguageId());
//        resFilmSource
        resFilmSource.setResolvingPower(FilmSourceRecord.getResolvingPower());
        resFilmSource.setSubtitleUrl(FilmSourceRecord.getSubtitleUrl());
        resFilmSource.setUpdateTime(FilmSourceRecord.getUpdateTime());

        String visitUrlId = FilmSourceRecord.getVisitUrlId();
        VisitUrl visitUrl = visitService.selectById(Integer.valueOf(visitUrlId));
//        LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(FilmSourceRecord.getLanguageId()));
        List<UploadUrl> list = gson.fromJson(visitUrl.getMinioUrl(), new TypeToken<List<UploadUrl>>() {
        }.getType());
        resFilmSource.setMinioUrl(list);
//        resFilmSource.setLanguage(languageInfo.getLanguage());

        return resFilmSource;
    }


//    @ApiOperation("根据电影信息id查找片源信息")
//    @PostMapping(value = "/selectByFilmInfoId", produces = "application/json;charset=UTF-8")
//    public ResFilmData selectByFilmInfoId(@RequestBody FindFilmInfoById findFilmInfoById) {
//
//        FilmSourceRecord filmSourceRecord = filmSourceService.selectByFilmInfoId(findFilmInfoById.getFilmInfoId());
//
//        LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecord.getLanguageId()));
//
//        VisitUrl visitUrl = visitService.selectByFilmId(filmSourceRecord.getFilmId());
//
//        String minioUrl = visitUrl.getMinioUrl();
//        List<UploadUrl> list = gson.fromJson(minioUrl, new TypeToken<List<UploadUrl>>() {
//        }.getType());
////        System.out.println(languageInfo);
//
//        ResFilmSource resFilmSource = filmSource(filmSourceRecord);
//        resFilmSource.setMinioUrl(list);
//        resFilmSource.setLanguage(languageInfo.getLanguage());
//        resFilmData.setCode(0);
//        resFilmData.setMsg("success");
//        resFilmData.setData(resFilmSource);
//        resFilmData.setTotal(1);
//
//        return resFilmData;
//
//    }

}
