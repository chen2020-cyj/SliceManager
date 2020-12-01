package com.fl.model.clientRes;

import com.fl.entity.FilmSourceRecord;
import lombok.Data;

import java.util.List;


@Data
public class ResFindAllFilmSource {
    private Integer total;

    private List<FilmSourceRecord> list;
}
