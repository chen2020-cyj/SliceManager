package com.fl.model;

import com.fl.entity.MinioInfo;
import lombok.Data;

import java.util.List;

@Data
public class FilmInfoData {
    private Object list;

    private List<MinioInfo> minioInfoList;
}
