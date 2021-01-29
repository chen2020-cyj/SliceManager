package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.util.List;

@Data
@TableName(value = "film_message")//指定表名
public class FilmInfo {
    @TableId(type = IdType.AUTO)
    private int id;

    private String filmId;
    
    private String chineseName;//中文名

    private String englishName;//英文名

    private String director;//导演

    private String author;//编剧

    private String actor;//演员

    private String mold;//类型

    private String productionCountry;//制片国家

    private String moreLanguage;//语言

    private String datePublished;//上映日期

    private String Premiere;//首播

    private int episodeNumber;//集数

    private String singleLength;//单集片长

    private String otherName;//别名

    private String officialWebsite;//官方网站

    private int doubanId;//豆瓣id

    private String imdbId;//imdb编号

    private String tag;//标签

//    private List<String> fileImages;//影片图片集
    @TableField(value = "filmCover_image")
    private String filmCoverImage;//电影封面

    private String description;//内容简介

    private List<String> singleIntroduction;//每集简介

    private String ratingCount;//评分人数

    private double ratingValue;//豆瓣评分

//    private String filmUrl;//电影的路径

    @TableId(value = "film_url_id")
    private Integer filmUrlId = 0;


    private String filmYear;

    private int category;//对于电影电视剧的判断
    /**
     * 电影全名
     */
    private String fullName;
    /**
     * 预删除
     */
    private String deleteFlag;
    /**
     * 电影是否上传
     */
    private String whetherUpload;

    private String updateTime;
}
