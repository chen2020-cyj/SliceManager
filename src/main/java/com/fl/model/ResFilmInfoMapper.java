package com.fl.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.FilmInfo;
import lombok.Data;

@Data
public class ResFilmInfoMapper {
    private Integer count;

    private IPage<FilmInfo> filmInfoIPage;
}
