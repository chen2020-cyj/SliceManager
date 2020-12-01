package com.fl.model.clientRes;

import com.fl.entity.MinioInfo;
import lombok.Data;

import java.util.List;

@Data
public class ResMinio {

    private List<MinioInfo> list;

    private Integer total;
}
