package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "language_info")//指定表名
public class LanguageInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 语言
     */
    private String language;

    private String languageKey;

    private String createTime;

    private String updateTime;
}
