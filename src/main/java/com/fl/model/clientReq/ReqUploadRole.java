package com.fl.model.clientReq;

import lombok.Data;

@Data
public class ReqUploadRole {
    /**
     * 自身userId
     */
    private Integer selfUserId;
    /**
     * 要修改的用户
     */
    private Integer userId;
    /**
     * 要修改的角色组
     */
    private Integer roleId;
}
