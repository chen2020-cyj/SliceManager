package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.BtDownLoad;
import com.fl.mapper.BtDownLoadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BtDownLoadService extends ServiceImpl<BtDownLoadMapper, BtDownLoad> {
    @Autowired
    BtDownLoadMapper btDownLoadMapper;

    public BtDownLoad selectByFilmId(String filmId){
        QueryWrapper<BtDownLoad> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        return btDownLoadMapper.selectOne(wrapper);
    }
    public void insertBtDownLoad(BtDownLoad bt){
        btDownLoadMapper.insert(bt);
    }

    public void updateBtDownLoad(String filmId,BtDownLoad bt){
        QueryWrapper<BtDownLoad> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        btDownLoadMapper.update(bt,wrapper);
    }
    public void delByFilmId(String filmId){
        QueryWrapper<BtDownLoad> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        btDownLoadMapper.delete(wrapper);
    }
}
