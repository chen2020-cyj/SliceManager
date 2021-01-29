package com.fl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fl.entity.FilmSourceRecord;
import com.fl.entity.VisitUrl;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmSourceMapper extends BaseMapper<FilmSourceRecord> {

    /**
     * 根据filmMessage 的id 查找 visit_url表中对应的数据
     */
    public VisitUrl selectByFilmInfoId(Integer filmInfoId);
}
