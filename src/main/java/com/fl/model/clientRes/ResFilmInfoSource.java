package com.fl.model.clientRes;

import com.fl.entity.FilmInfo;
import com.fl.entity.FilmSourceRecord;
import com.fl.entity.MinioInfo;
import lombok.Data;

import java.util.List;

@Data
public class ResFilmInfoSource {
    private FilmInfo filmInfo;

    private Object resFilmSource;

}
