package com.fl.control;

import com.fl.entity.FilmSourceRecord;
import com.fl.entity.LanguageInfo;
import com.fl.entity.User;
import com.fl.entity.VisitUrl;
import com.fl.model.UploadUrl;
import com.fl.model.clientReq.AdminFindFilm;
import com.fl.model.clientReq.FindAllFilmSource;
import com.fl.model.clientReq.FindFilmSource;
import com.fl.model.clientReq.SelectFilmReq;
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
    private UserService userService;
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

    @ApiOperation("管理员查看单个片源")
    @PostMapping(value = "/adminSelectFilm",produces = "application/json;charset=UTF-8")
    public ResFilmData selectFilm(@RequestBody AdminFindFilm admin) {

        FilmSourceRecord film = filmSourceService.findFilm(admin.getFilmId());
//        User user = userService.selectUserInfo(admin.getUserId());
//        Long currentTime = System.currentTimeMillis() / 1000;
//        Long tokenTime = Long.valueOf(user.getTokenTime());


            if (film != null) {

                LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(film.getLanguageId()));
                VisitUrl visitUrl = visitService.selectByFilmId(film.getFilmId());

                ResFilmSource resFilmSource = filmSource(film);
                resFilmSource.setLanguage(languageInfo.getLanguage());
                List<UploadUrl> list = gson.fromJson(visitUrl.getMinioUrl().toString(), new TypeToken<List<UploadUrl>>() {
                }.getType());

                resFilmSource.setMinioUrl(list);
                resFilmSource.setMinioUrl(visitUrl.getMinioUrl());

                resFilmData.setCode(0);
                resFilmData.setMsg("success");
                resFilmData.setData(resFilmSource);
                resFilmData.setTotal(1);

                return resFilmData;
            } else {
                resFilmData.setCode(1);
                resFilmData.setMsg("err");
                resFilmData.setData("");

                return resFilmData;
            }

    }
    @ApiOperation("管理员查询所有片源")
    @PostMapping(value = "/selectAllFilm",produces = "application/json;charset=UTF-8")
    public ResFilmData selectAllFilm(@RequestBody FindAllFilmSource findAllFilmSource){

        Integer page = findAllFilmSource.getPage();
        Integer offset = findAllFilmSource.getOffset();
        List<FilmSourceRecord> filmSourceRecords = filmSourceService.selectPage(page-1, offset);
//
//        User user = userService.selectUserInfo(findAllFilmSource.getUserId());
//        Long currentTime = System.currentTimeMillis()/1000;
//        Long tokenTime =Long.valueOf(user.getTokenTime());



            List<ResFilmSource> filmData = new ArrayList<>();

            for (int i=0;i<filmSourceRecords.size();i++){


                ResFilmSource resFilmSource = filmSource(filmSourceRecords.get(i));

                String filmId = filmSourceRecords.get(i).getFilmId();
                VisitUrl visitUrl = visitService.selectByFilmId(filmId);
                LanguageInfo languageInfo = languageInfoService.selectById(Integer.valueOf(filmSourceRecords.get(i).getLanguageId()));
                List<UploadUrl> list = gson.fromJson(visitUrl.getMinioUrl().toString(), new TypeToken<List<UploadUrl>>() {
                }.getType());
                resFilmSource.setMinioUrl(list);
                resFilmSource.setLanguage(languageInfo.getLanguage());
                filmData.add(resFilmSource);
            }

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(filmData);
            resFilmData.setTotal(filmSourceService.selectTotal());

            return resFilmData;

    }

    public ResFilmSource filmSource(FilmSourceRecord FilmSourceRecord){
        ResFilmSource resFilmSource = new ResFilmSource();
        resFilmSource.setBtUrl(FilmSourceRecord.getBtUrl());
        resFilmSource.setCreateTime(FilmSourceRecord.getCreateTime());
        resFilmSource.setFilmId(FilmSourceRecord.getFilmId());
        resFilmSource.setFilmInfoId(FilmSourceRecord.getFilmInfoId());
        resFilmSource.setFilmName(FilmSourceRecord.getFilmName());
        resFilmSource.setId(FilmSourceRecord.getId());
//                resFilmSource.setLanguageId(filmSourceRecords.get(i).getLanguageId());
        resFilmSource.setResolvingPower(FilmSourceRecord.getResolvingPower());
        resFilmSource.setSubtitleUrl(FilmSourceRecord.getSubtitleUrl());
        resFilmSource.setUpdateTime(FilmSourceRecord.getUpdateTime());

        return resFilmSource;
    }
}
