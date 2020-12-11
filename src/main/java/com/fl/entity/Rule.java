package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "rule")//指定表名
public class Rule {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String roles;

}
