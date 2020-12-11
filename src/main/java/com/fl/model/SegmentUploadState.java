package com.fl.model;

import lombok.Data;

@Data
public class SegmentUploadState {
    private String segmentUploadComplete;

    private String segmentUpload;

    private String segmentUploadFail;
}
