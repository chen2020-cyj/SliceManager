package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Search {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String type;

    private String param;

    private String name;

    private Integer weigh;

    private Integer categoryId;

    private Integer hot;

    private Integer navi;

    private String limits;
}
