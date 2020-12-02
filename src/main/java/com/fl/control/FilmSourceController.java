package com.fl.control;

import com.fl.entity.FilmSourceRecord;
import com.fl.entity.User;
import com.fl.model.clientReq.AdminFindFilm;
import com.fl.model.clientReq.FindAllFilmSource;
import com.fl.model.clientReq.FindFilmSource;
import com.fl.model.clientReq.SelectFilmReq;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResFilmSourceInfo;
import com.fl.model.clientRes.ResFindAllFilmSource;
import com.fl.service.FilmSourceService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "片源管理")
@RestController
public class FilmSourceController {

    @Autowired
    private FilmSourceService filmSourceService;
    @Autowired
    private  UserService userService;

    private SelectFilmReq selectReq = new SelectFilmReq();
    private ResData res = new ResData();
    private ResFilmSourceInfo filmInfo = new ResFilmSourceInfo();
    private ResFindAllFilmSource resFindAllFilmSource = new ResFindAllFilmSource();
    private ResFilmData resFilmData = new ResFilmData();

    @ApiOperation("查询片源")
    @PostMapping(value = "/selectFilm",produces = "application/json;charset=UTF-8")
    public String selectFilm(@RequestBody FindFilmSource filmSource){

        FilmSourceRecord film = filmSourceService.findFilm(filmSource.getFilmId());

        if (film != null){

            filmInfo.setFilmId(film.getFilmId());
            filmInfo.setMinioUrl(film.getMinioUrl());
            filmInfo.setResolvingPower(film.getResolvingPower());

            res.setCode(0);
            res.setMsg("success");
            res.setData(filmInfo);

            return GsonUtils.toJson(res);
        }else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("");

            return GsonUtils.toJson(res);
        }
    }

    @ApiOperation("管理员查看单个片源")
    @PostMapping(value = "/adminSelectFilm",produces = "application/json;charset=UTF-8")
    public String selectFilm(@RequestBody AdminFindFilm admin){

        FilmSourceRecord film = filmSourceService.findFilm(admin.getFilmId());
        User user = userService.selectUserInfo(admin.getUserId());
        Long currentTime = System.currentTimeMillis()/1000;
        Long tokenTime =Long.valueOf(user.getTokenTime());

        if (tokenTime > currentTime){
            if (film != null){

                filmInfo.setFilmId(film.getFilmId());
                filmInfo.setMinioUrl(film.getMinioUrl());
                filmInfo.setResolvingPower(film.getResolvingPower());

                res.setCode(0);
                res.setMsg("success");
                res.setData(filmInfo);

                return GsonUtils.toJson(res);
            }else {
                res.setCode(1);
                res.setMsg("err");
                res.setData("");

                return GsonUtils.toJson(res);
            }
        }else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("403");

            return GsonUtils.toJson(res);
        }

    }
    @ApiOperation("管理员查询所有片源")
    @PostMapping(value = "/selectAllFilm",produces = "application/json;charset=UTF-8")
    public String selectAllFilm(@RequestBody FindAllFilmSource findAllFilmSource){


        Integer page = findAllFilmSource.getPage();
        Integer offset = page*(findAllFilmSource.getOffset()-1);
        List<FilmSourceRecord> filmSourceRecords = filmSourceService.selectPage(page-1, offset);

        User user = userService.selectUserInfo(findAllFilmSource.getUserId());
        Long currentTime = System.currentTimeMillis()/1000;
        Long tokenTime =Long.valueOf(user.getTokenTime());
        System.out.println();

        if (tokenTime > currentTime){

            resFilmData.setCode(0);
            resFilmData.setMsg("success");
            resFilmData.setData(filmSourceRecords);
            resFilmData.setTotal(filmSourceService.selectTotal());
                return GsonUtils.toJson(res);
        }else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("403");

            return GsonUtils.toJson(res);
        }
    }
}
