package com.fl.model.clientReq;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data

public class UserInfoTest {
    private Integer id;

    private String introduction;

    private String avatar;

    private String name;

    private List<String> roles;

}
