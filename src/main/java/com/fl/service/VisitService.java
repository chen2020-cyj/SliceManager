package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.VisitUrl;
import com.fl.mapper.VisitUrlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitService extends ServiceImpl<VisitUrlMapper, VisitUrl> {
    @Autowired
    private VisitUrlMapper visitUrlMapper;

    /**
     * 根据 id查找数据
     */
    public VisitUrl selectById(Integer id){
        QueryWrapper<VisitUrl> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return visitUrlMapper.selectOne(wrapper);
    }

    /**
     * 根据filmId查找数据
     */
    public VisitUrl selectByFilmId(String filmId){
        QueryWrapper<VisitUrl> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        return visitUrlMapper.selectOne(wrapper);
    }
    /**
     * 根据filmId  更新数据
     */
    public void updateFilmId(String filmId,VisitUrl visitUrl){
        QueryWrapper<VisitUrl> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        visitUrlMapper.update(visitUrl,wrapper);
    }

    /**
     *  插入数据
     */
    public int insertVisitUrl(VisitUrl visitUrl){

        return visitUrlMapper.insert(visitUrl);
    }


}
