package com.fl.control;

import com.fl.aop.annotation.Log;
import com.fl.entity.Search;
import com.fl.model.clientRes.ResCondition;
import com.fl.model.clientRes.ResFilmData;
import com.fl.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "电影各种条件")

@RestController
public class FilmConditionController {

    @Autowired
    private SearchService searchService;


    private ResFilmData resFilmData = new ResFilmData();
    @Log("user:selectAllFilmCondition")
    @ApiOperation("查询所有电影的各种条件")
    @PostMapping("/selectAllFilmCondition")
    public ResFilmData selectAllFilmCondition() {

        List<Search> searches = searchService.selectAll();
        ResCondition resCondition = new ResCondition();
        List<String> area = new ArrayList<>();
        List<String> year = new ArrayList<>();
        List<String> tag = new ArrayList<>();

        for (int i = 0; i < searches.size(); i++) {
            if (searches.get(i).getCategoryId() == 1){
                if (searches.get(i).getParam().equals("area")) {
                        area.add(searches.get(i).getName());
                    } else if (searches.get(i).getParam().equals("time")) {
                        year.add(searches.get(i).getName());
                    } else if (searches.get(i).getParam().equals("type")) {
                        tag.add(searches.get(i).getName());
                }
            }
        }
        resCondition.setArea(area);
        resCondition.setTag(tag);
        resCondition.setYear(year);
        System.out.println(searches);

        resFilmData.setCode(0);
        resFilmData.setMsg("success");
        resFilmData.setData(resCondition);
        return resFilmData;
    }
}
