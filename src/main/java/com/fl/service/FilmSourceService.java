package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.FilmSourceRecord;
import com.fl.mapper.FilmSourceMapper;
import com.fl.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmSourceService extends ServiceImpl<FilmSourceMapper, FilmSourceRecord> {
    @Autowired
    private FilmSourceMapper filmSourceMapper;

    @Autowired
    private UserMapper userMapper;

    public void addFilmSource(FilmSourceRecord film){
        filmSourceMapper.insert(film);
    }
    public FilmSourceRecord findFilm(String filmId){


        QueryWrapper<FilmSourceRecord> wrapper = new QueryWrapper<>();

        wrapper.eq("film_id",filmId);
        FilmSourceRecord filmSourceManager = filmSourceMapper.selectOne(wrapper);
        return filmSourceManager;
    }
    public IPage<FilmSourceRecord> selectPage(int page, int offset){
        QueryWrapper<FilmSourceRecord> wrapper = new QueryWrapper<>();
        IPage<FilmSourceRecord> iPage = new Page<>(page,offset);

        return filmSourceMapper.selectPage(iPage,wrapper);
    }

    /**
     * count 总数
     */
    public Integer selectTotal(){
        QueryWrapper<FilmSourceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");
        return filmSourceMapper.selectCount(wrapper);
    }
    public List<FilmSourceRecord> selectByFilmInfoId(Integer id){
        QueryWrapper<FilmSourceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("film_info_id",id);

        return filmSourceMapper.selectList(wrapper);
    }
    public FilmSourceRecord selectByFilmId(String filmId){
        QueryWrapper<FilmSourceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        return filmSourceMapper.selectOne(wrapper);
    }
}
