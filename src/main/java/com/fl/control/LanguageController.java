package com.fl.control;

import com.fl.aop.annotation.Log;
import com.fl.entity.LanguageInfo;
import com.fl.model.clientReq.ReqAddLanguage;
import com.fl.model.clientRes.ResData;
import com.fl.service.LanguageInfoService;
import com.fl.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "查询语言")
@RestController
public class LanguageController {


    @Autowired
    private LanguageInfoService languageInfoService;

    private ResData res = new ResData();
    @Log("user:selectAllLanguage")
    @ApiOperation("查询所有语言")
    @PostMapping("/selectAllLanguage")
    public ResData selectAllLanguage(){

        List<LanguageInfo> languageInfos = languageInfoService.selectAllLanguage();

        res.setCode(0);
        res.setMsg("success");
        res.setData(languageInfos);
        return res;
    }
    @Log("user:addNewLanguage")
    @ApiOperation("添加新的语言")
    @PostMapping("/addNewLanguage")
    public ResData addNewLanguage(@RequestBody ReqAddLanguage reqAddLanguage){


        LanguageInfo languageInfo = languageInfoService.selectByLanguage(reqAddLanguage.getLanguage());
        if (languageInfo == null){
            LanguageInfo languageInfo1 = new LanguageInfo();
            languageInfo1.setLanguage(reqAddLanguage.getLanguage());
            languageInfo1.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
            languageInfo1.setLanguageKey(reqAddLanguage.getOtherName());
            languageInfoService.insertLanguage(languageInfo1);

            res.setCode(0);
            res.setMsg("success");
            res.setData("");
            return res;

        }else {
            res.setCode(1);
            res.setMsg("该语言已经存在");
            res.setData(reqAddLanguage);

            return res;
        }


    }

}
