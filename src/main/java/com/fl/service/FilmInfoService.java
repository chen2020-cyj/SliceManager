package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.FilmInfo;
import com.fl.mapper.FilmInfoMapper;
import com.fl.model.clientReq.FindFilmInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.Map;

@Service
public class FilmInfoService extends ServiceImpl<FilmInfoMapper, FilmInfo> {
    @Autowired
    private FilmInfoMapper filmInfoMapper;

    /**
     * 查询所有  并且按照豆瓣评分进行排序
     */
    public IPage<FilmInfo> selectAll(Integer page,Integer offset){
        QueryWrapper<FilmInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("''","").orderByDesc("rating_value");
        IPage<FilmInfo> infoIPage = new Page<>(page,offset);

        return  filmInfoMapper.selectPage(infoIPage,queryWrapper);
    }

    /**
     * 查询所有个数
     */
    public Integer selectCount(){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return filmInfoMapper.selectCount(wrapper);
    }
    /**
     * 根据电影名称进行查询
     */
    public IPage<FilmInfo> selectByFilmName(String filmName){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.like("chinese_name",filmName);
        IPage<FilmInfo> infoIPage = new Page<>(0,1);

        return filmInfoMapper.selectPage(infoIPage,wrapper);
    }
    public FilmInfo selectByNameAndLanguage(Map<String,String> map){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.allEq(map);

        return filmInfoMapper.selectOne(wrapper);
    }
    /**
     * 根据Id进行查询
     */
    public FilmInfo selectById(Integer id){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return filmInfoMapper.selectOne(wrapper);
    }
    /**
     * 根据地区查询数据
     */
    public IPage<FilmInfo> selectByArea(String area, Integer page, Integer offset){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.like("production_country",area);
        IPage<FilmInfo> infoIPage = new Page<>(page,offset);

        return filmInfoMapper.selectPage(infoIPage,wrapper);
    }
    /**
     * 根据年份进行查询
     */
    public IPage<FilmInfo> selectByYear(Integer year,Integer page,Integer offset){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("film_year",year);
        IPage<FilmInfo> infoIPage = new Page<>(page,offset);

        return filmInfoMapper.selectPage(infoIPage,wrapper);
    }
    /**
     * 根据电影类型  电影年份 地区 进行多条件筛选
     */
    public IPage<FilmInfo> selectMore(FindFilmInfo findFilmInfo,Integer page,Integer offset){
        QueryWrapper<FilmInfo> queryWrapper = new QueryWrapper<>();

       if (findFilmInfo.getYear().equals("")){

           if (findFilmInfo.getTag().equals("")){
                queryWrapper.like("production_country",findFilmInfo.getArea()).orderByDesc("rating_value");
           }else if (findFilmInfo.getArea().equals("")){
                queryWrapper.like("tag",findFilmInfo.getTag()).orderByDesc("rating_value");
           }else {
                queryWrapper.like("production_country",findFilmInfo.getArea()).and(Wrapper->Wrapper.like("tag",findFilmInfo.getTag())).orderByDesc("rating_value");
           }

       }else if (findFilmInfo.getArea().equals("")){
            if (findFilmInfo.getTag().equals("")){
                if (findFilmInfo.getYear().contains(",")){
                    String[] split = findFilmInfo.getYear().split(",");
                    queryWrapper.ge("film_year",split[0]).and(Wrapper->Wrapper.lt("film_year",split[1])).orderByDesc("rating_value");
                }else {
                    queryWrapper.ge("film_year",findFilmInfo.getYear()).orderByDesc("rating_value");
                }
            }else if (findFilmInfo.getYear().equals("")){
                    queryWrapper.like("tag",findFilmInfo.getTag());
            }else {
                if (findFilmInfo.getYear().contains(",")){
                    String[] split = findFilmInfo.getYear().split(",");
                    queryWrapper.like("tag",findFilmInfo.getTag()).and(Wrapper->Wrapper.ge("film_year",split[0])).and(Wrapper->Wrapper.lt("film_year",split[1])).orderByDesc("rating_value");
                }else {
                    queryWrapper.like("tag",findFilmInfo.getTag()).and(Wrapper->Wrapper.eq("film_year",findFilmInfo.getYear())).orderByDesc("rating_value");
                }
            }

       }else if (findFilmInfo.getTag().equals("")){

           if (findFilmInfo.getYear().equals("")){
               queryWrapper.like("production_country",findFilmInfo.getTag()).orderByDesc("rating_value");
           }else if (findFilmInfo.getArea().equals("")){
               if (findFilmInfo.getYear().contains(",")){
                   String[] split = findFilmInfo.getYear().split(",");
                   queryWrapper.ge("film_year",split[0]).and(Wrapper->Wrapper.lt("film_year",split[1]));
               }else {
                   queryWrapper.eq("film_year",findFilmInfo.getYear());
               }
           }else {
               if (findFilmInfo.getYear().contains(",")){
                   String[] split = findFilmInfo.getYear().split(",");
                   queryWrapper.like("production_country",findFilmInfo.getTag()).and(Wrapper->Wrapper.ge("film_year",split[0])).and(Wrapper->Wrapper.lt("film_year",split[1])).orderByDesc("rating_value");
               }else {
                   queryWrapper.like("production_country",findFilmInfo.getTag()).and(Wrapper->Wrapper.eq("film_year",findFilmInfo.getYear())).orderByDesc("rating_value");
               }
           }
       }else {
           queryWrapper.eq("''","");
       }
        IPage<FilmInfo> infoIPage = new Page<>(page,offset);

        return  filmInfoMapper.selectPage(infoIPage,queryWrapper);
    }

    public void updateByFilmInfoId(FilmInfo filmInfo){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",filmInfo.getId());
        filmInfoMapper.update(filmInfo,wrapper);
    }
}
