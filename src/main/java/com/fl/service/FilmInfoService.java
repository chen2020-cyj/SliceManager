package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.FilmInfo;
import com.fl.mapper.FilmInfoMapper;
import com.fl.model.ResFilmInfoMapper;
import com.fl.model.clientReq.FindAllFilmInfo;
import com.fl.model.clientReq.FindFilmInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.Area;
import java.sql.Wrapper;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmInfoService extends ServiceImpl<FilmInfoMapper, FilmInfo> {
    @Autowired
    private FilmInfoMapper filmInfoMapper;

    /**
     * 查询所有  并且按照豆瓣评分进行排序
     */
    public IPage<FilmInfo> selectAll(Integer page, Integer offset) {
        QueryWrapper<FilmInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("''", "").orderByDesc("rating_value");
        IPage<FilmInfo> infoIPage = new Page<>(page, offset);

        return filmInfoMapper.selectPage(infoIPage, queryWrapper);
    }

    public IPage<FilmInfo> selectByDouBanId(String doubanId){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("douban_id",doubanId);
        IPage<FilmInfo> infoIPage = new Page<>(0,1);
        return  filmInfoMapper.selectPage(infoIPage,wrapper);
    }
    /**
     * 根据Id进行查询
     */
    public FilmInfo selectById(Integer id) {
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);

        return filmInfoMapper.selectOne(wrapper);
    }


    /**
     * 根据年份进行查询
     */
    public IPage<FilmInfo> selectByYear(Integer year, Integer page, Integer offset) {
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("film_year", year);
        IPage<FilmInfo> infoIPage = new Page<>(page, offset);

        return filmInfoMapper.selectPage(infoIPage, wrapper);
    }

    /**
     * 根据电影类型  电影年份 地区 进行多条件筛选
     */
    public IPage<FilmInfo> selectMore(FindFilmInfo findFilmInfo, Integer page, Integer offset) {
        QueryWrapper<FilmInfo> queryWrapper = new QueryWrapper<>();

        if (findFilmInfo.getYear().equals("")) {

            if (findFilmInfo.getTag().equals("")) {
                queryWrapper.like("production_country", findFilmInfo.getArea()).orderByDesc("rating_value");
            } else if (findFilmInfo.getArea().equals("")) {
                queryWrapper.like("tag", findFilmInfo.getTag()).orderByDesc("rating_value");
            } else {
                queryWrapper.like("production_country", findFilmInfo.getArea()).and(Wrapper -> Wrapper.like("tag", findFilmInfo.getTag())).orderByDesc("rating_value");
            }

        } else if (findFilmInfo.getArea().equals("")) {
            if (findFilmInfo.getTag().equals("")) {
                if (findFilmInfo.getYear().contains(",")) {
                    String[] split = findFilmInfo.getYear().split(",");
                    queryWrapper.ge("film_year", split[0]).and(Wrapper -> Wrapper.lt("film_year", split[1])).orderByDesc("rating_value");
                } else {
                    queryWrapper.ge("film_year", findFilmInfo.getYear()).orderByDesc("rating_value");
                }
            } else if (findFilmInfo.getYear().equals("")) {
                queryWrapper.like("tag", findFilmInfo.getTag()).orderByDesc("rating_value");
            } else {
                if (findFilmInfo.getYear().contains(",")) {
                    String[] split = findFilmInfo.getYear().split(",");
                    queryWrapper.like("tag", findFilmInfo.getTag()).and(Wrapper -> Wrapper.ge("film_year", split[0])).and(Wrapper -> Wrapper.lt("film_year", split[1])).orderByDesc("rating_value");
                } else {
                    queryWrapper.like("tag", findFilmInfo.getTag()).and(Wrapper -> Wrapper.eq("film_year", findFilmInfo.getYear())).orderByDesc("rating_value");
                }
            }

        } else if (findFilmInfo.getTag().equals("")) {

            if (findFilmInfo.getYear().equals("")) {
                queryWrapper.like("production_country", findFilmInfo.getTag()).orderByDesc("rating_value");
            } else if (findFilmInfo.getArea().equals("")) {
                if (findFilmInfo.getYear().contains(",")) {
                    String[] split = findFilmInfo.getYear().split(",");
                    queryWrapper.ge("film_year", split[0]).and(Wrapper -> Wrapper.lt("film_year", split[1])).orderByDesc("rating_value");
                } else {
                    queryWrapper.eq("film_year", findFilmInfo.getYear());
                }
            } else {
                if (findFilmInfo.getYear().contains(",")) {
                    String[] split = findFilmInfo.getYear().split(",");
                    queryWrapper.like("production_country", findFilmInfo.getTag()).and(Wrapper -> Wrapper.ge("film_year", split[0])).and(Wrapper -> Wrapper.lt("film_year", split[1])).orderByDesc("rating_value");
                } else {
                    queryWrapper.like("production_country", findFilmInfo.getTag()).and(Wrapper -> Wrapper.eq("film_year", findFilmInfo.getYear())).orderByDesc("rating_value");
                }
            }
        } else if (!findFilmInfo.getYear().equals("") && !findFilmInfo.getArea().equals("") && !findFilmInfo.getTag().equals("")) {
            if (findFilmInfo.getYear().contains(",")) {
                String[] split = findFilmInfo.getYear().split(",");
                queryWrapper.ge("film_year", split[0]).and(Wrapper -> Wrapper.lt("film_year", split[1])).and(Wrapper->Wrapper.like("production_country",findFilmInfo.getArea())).and(Wrapper->Wrapper.like("tag",findFilmInfo.getTag())).orderByDesc("rating_value");
        } else {
            queryWrapper.eq("film_year", findFilmInfo.getYear()).and(Wrapper->Wrapper.like("production_country",findFilmInfo.getArea())).and(Wrapper->Wrapper.like("tag",findFilmInfo.getTag())).orderByDesc("rating_value");
        }
        } else {
            queryWrapper.eq("''", "").orderByDesc("rating_value");
        }


        IPage<FilmInfo> infoIPage = new Page<>(page, offset);

        return filmInfoMapper.selectPage(infoIPage, queryWrapper);
    }

    public void updateByFilmInfoId(FilmInfo filmInfo) {
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id", filmInfo.getId());
        filmInfoMapper.update(filmInfo, wrapper);
    }

    public void insertFilmInfo(FilmInfo filmInfo) {
//        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
//        wrapper.eq("id", filmInfo.getId());
        filmInfoMapper.insert(filmInfo);
    }
    public ResFilmInfoMapper selectMoreCondition(Integer id, String year, FindAllFilmInfo info){
        QueryWrapper<FilmInfo> queryWrapper = new QueryWrapper<>();
        switch (id){
            case 1:
                queryWrapper.eq("''","").and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                break;
            case 2:
                queryWrapper.like("tag",info.getTag()).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                break;
            case 3:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.ge("film_year",split[0]).and(Wrapper->Wrapper.le("film_year",split[1])).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                }else {
                    queryWrapper.eq("film_year",year).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                }
                break;
            case 4:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.ge("film_year",split[0]).and(Wrapper->Wrapper.le("film_year",split[1])).and(Wrapper->Wrapper.like("tag",info.getTag())).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                }else {
                    queryWrapper.eq("film_year",year).and(Wrapper->Wrapper.like("tag",info.getTag())).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }
                break;
            case 5:
                queryWrapper.like("production_country",info.getArea()).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                break;
            case 6:
                queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.like("tag",info.getTag())).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                break;
            case 7:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.ge("film_year",split[0])).and(Wrapper->Wrapper.le("film_year",split[1])).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                }else {
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.eq("film_year",year)).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }
                break;
            case 8:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.ge("film_year",split[0])).and(Wrapper->Wrapper.le("film_year",split[1])).and(Wrapper->Wrapper.like("tag",info.getTag())).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }else {
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.eq("film_year",year)).and(Wrapper->Wrapper.like("tag",info.getTag())).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }
                break;
        }
        ResFilmInfoMapper resFilmInfoMapper = new ResFilmInfoMapper();

        resFilmInfoMapper.setCount( filmInfoMapper.selectCount(queryWrapper));
        IPage<FilmInfo> iPage = new Page<>(info.getPage(),info.getOffset());
        resFilmInfoMapper.setFilmInfoIPage(filmInfoMapper.selectPage(iPage,queryWrapper));
//        map.put("iPage",filmInfoMapper.selectPage(iPage,queryWrapper));
        return resFilmInfoMapper;
    }
    public ResFilmInfoMapper selectComplete(Integer id, String year, FindAllFilmInfo info){
        QueryWrapper<FilmInfo> queryWrapper = new QueryWrapper<>();
        switch (id){
            case 1:
                queryWrapper.eq("''","").and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");
                break;
            case 2:
                queryWrapper.like("tag",info.getTag()).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                break;
            case 3:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.ge("film_year",split[0]).and(Wrapper->Wrapper.le("film_year",split[1])).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }else {
                    queryWrapper.eq("film_year",year).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }
                break;
            case 4:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.ge("film_year",split[0]).and(Wrapper->Wrapper.le("film_year",split[1])).and(Wrapper->Wrapper.like("tag",info.getTag())).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }else {
                    queryWrapper.eq("film_year",year).and(Wrapper->Wrapper.like("tag",info.getTag())).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }
                break;
            case 5:
                queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                break;
            case 6:
                queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.like("tag",info.getTag())).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                break;
            case 7:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.ge("film_year",split[0])).and(Wrapper->Wrapper.le("film_year",split[1])).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }else {
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.eq("film_year",year)).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }
                break;
            case 8:
                if (year.contains(",")){
                    String[] split = year.split(",");
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.ge("film_year",split[0])).and(Wrapper->Wrapper.le("film_year",split[1])).and(Wrapper->Wrapper.like("tag",info.getTag())).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }else {
                    queryWrapper.like("production_country",info.getArea()).and(Wrapper->Wrapper.eq("film_year",year)).and(Wrapper->Wrapper.like("tag",info.getTag())).and(Wrapper->Wrapper.eq("whether_upload","1")).and(i->i.eq("category",info.getCategoryId())).orderByDesc("rating_value");;
                }
                break;
        }
        ResFilmInfoMapper resFilmInfoMapper = new ResFilmInfoMapper();

        resFilmInfoMapper.setCount( filmInfoMapper.selectCount(queryWrapper));
        IPage<FilmInfo> iPage = new Page<>(info.getPage(),info.getOffset());
        resFilmInfoMapper.setFilmInfoIPage(filmInfoMapper.selectPage(iPage,queryWrapper));
//        map.put("iPage",filmInfoMapper.selectPage(iPage,queryWrapper));
        return resFilmInfoMapper;
    }

    public List<FilmInfo> selectByLikeName(String filmName,String categoryId){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.like("chinese_name",filmName).and(i->i.eq("category",categoryId));

        return filmInfoMapper.selectList(wrapper);
    }
    public Integer selectByLikeNameCount(String filmName,String categoryId){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("chinese_name",filmName).and(i->i.eq("category",categoryId));

        return filmInfoMapper.selectCount(wrapper);
    }
    public FilmInfo selectByFilmId(String filmId){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        return filmInfoMapper.selectOne(wrapper);
    }
    public List<FilmInfo> All(){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id","");

        return filmInfoMapper.selectList(wrapper);
    }
    public FilmInfo selectByFilmRandom(String filmRandom){
        QueryWrapper<FilmInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("film_random",filmRandom);

        return filmInfoMapper.selectOne(wrapper);
    }
}
