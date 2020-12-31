package com.fl.model.clientRes;

import com.fl.entity.FilmInfo;
import com.fl.entity.FilmSourceRecord;
import lombok.Data;

@Data
public class ResFilmInfoSource {
    private FilmInfo filmInfo;

    private Object resFilmSource;

}
