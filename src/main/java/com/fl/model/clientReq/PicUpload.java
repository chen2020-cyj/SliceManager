package com.fl.model.clientReq;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PicUpload {
    private String token;

    private Integer userId;

    private MultipartFile file;
}
