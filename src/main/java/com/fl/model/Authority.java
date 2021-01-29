package com.fl.model;

import lombok.Data;

import java.util.List;

@Data
public class Authority {
    private Integer id;

    private String name;

    private String path;

    private String icon;

    private String viewUrl;

    private Object children;
}
