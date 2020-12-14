package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer pid;

    private String type;

    private String name;

    private String flag;

    private String keywords;

    private String description;

    private String image;

    private Integer createTime;

    private Integer updateTime;

    private Integer weigh;

    private String status;

    private String navi;

    private String navichild;

    private String index;

    private String indexchild;

    private String indexType;

    @TableField(value = "default")
    private Integer defaultNum;

    private String defaultName;

}
