package com.fl.control;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.*;
import com.fl.model.UploadUrl;
import com.fl.model.clientReq.FindAllFilmInfo;
import com.fl.model.clientReq.FindFilmInfoById;
import com.fl.model.clientReq.PicUpload;
import com.fl.model.clientReq.UpdateInfo;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResFilmSource;
import com.fl.model.clientRes.ResUpload;
import com.fl.service.FilmInfoService;
import com.fl.service.UserService;
import com.fl.utils.FileUtils;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Api(tags = "影片信息管理接口")

@RestController
public class FilmInfoController {

    @Autowired
    private FilmInfoService filmInfoService;
    @Autowired
    private UserService userService;

    private ResFilmData resFilmData = new ResFilmData();
    private ResData res = new ResData();

    @ApiOperation("查询所有影片信息")
    @PostMapping(value = "/selectFilm", produces = "application/json;charset=UTF-8")
    public ResFilmData selectFilm(@RequestBody FindAllFilmInfo findAllFilmInfo) {


        Integer page = findAllFilmInfo.getPage();
        Integer offset = findAllFilmInfo.getOffset();
        IPage<FilmInfo> infoIPage = filmInfoService.selectAll(page - 1, offset);

        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData(infoIPage);
        resFilmData.setTotal(filmInfoService.selectCount());

        return resFilmData;


    }

    @ApiOperation("根据Id查找影片信息")
    @PostMapping(value = "/selectIdFilmInfo", produces = "application/json;charset=UTF-8")
    public ResFilmData selectIdFilmInfo(@RequestBody FindFilmInfoById findFilmInfoById) {


        FilmInfo filmInfo = filmInfoService.selectById(findFilmInfoById.getFilmInfoId());

        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData(filmInfo);
        resFilmData.setTotal(1);

        return resFilmData;

    }

    @ApiOperation("修改电影信息")
    @PostMapping(value = "/updateFilmInfo", produces = "application/json;charset=UTF-8")
    public ResFilmData updateFilmInfo(@RequestBody UpdateInfo updateInfo) {

        FilmInfo filmInfo = filmInfoService.selectById(updateInfo.getFilmInfoId());

        filmInfo.setChineseName(updateInfo.getFilmChineseName());
        filmInfo.setEnglishName(updateInfo.getFilmEnglishName());
        filmInfo.setDescription(updateInfo.getIntroduction());

        File file = new File(updateInfo.getImageUrl());


        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData("");
        resFilmData.setTotal(1);

        return resFilmData;

    }

    @ApiOperation("图片上传功能")
    @PostMapping(value = "/picUpload", produces = "application/json;charset=UTF-8")
    public ResData picUpload(@RequestBody MultipartFile file) throws IOException {

        String upload = FileUtils.upload(file);
        if (upload.equals("")) {
            res.setCode(1);
            res.setMsg("图片上传失败");
            res.setData("");
        } else {
            ResUpload resUpload = new ResUpload();
            resUpload.setUploadUrl(upload);
            res.setCode(0);
            res.setMsg("success");
            res.setData(resUpload);
        }
        return res;
    }
}
