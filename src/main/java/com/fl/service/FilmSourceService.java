package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    }
    public FilmSourceRecord findFilm(String filmId){


        QueryWrapper<FilmSourceRecord> wrapper = new QueryWrapper<>();

        wrapper.eq("film_id",filmId);
        FilmSourceRecord filmSourceManager = filmSourceMapper.selectOne(wrapper);
        return filmSourceManager;
    }
    public List<FilmSourceRecord>  selectPage(int offset, int page){

        return userMapper.selectPage(offset,page);
    }

    /**
     * count 总数
     */
    public Integer selectTotal(){
        QueryWrapper<FilmSourceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");
        return filmSourceMapper.selectCount(wrapper);
    }

}
