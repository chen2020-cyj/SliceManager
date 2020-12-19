package com.fl.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.TaskManager;
import lombok.Data;

@Data
public class ResTaskInfoMapper {
    private Integer count;

    private IPage<TaskManager> iPage;
}
