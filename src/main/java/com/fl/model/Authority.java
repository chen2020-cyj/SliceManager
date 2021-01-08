package com.fl.model;

import lombok.Data;

import java.util.List;

@Data
public class Authority {
    private Integer id;

    private String mainMenu;

    private String mainMenuUrl;

    private String icon;

    private Object list;
}
