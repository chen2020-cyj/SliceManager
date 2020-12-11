package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.MinioInfo;
import com.fl.mapper.MinioInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinioInfoService extends ServiceImpl<MinioInfoMapper, MinioInfo> {
    @Autowired
    private MinioInfoMapper minioInfoMapper;

    /**
     * 根据id查找对应桶
     * @param id
     * @return
     */
    public MinioInfo findMinio(Integer id){

        QueryWrapper<MinioInfo> wrapper = new QueryWrapper();
        wrapper.eq("id",id);

        return  minioInfoMapper.selectOne(wrapper);
    }

    /**
     * 根据分辨率查找对应桶
     * @param resolvingPower
     * @return
     */
    public List<MinioInfo> findMinio(String resolvingPower){
        QueryWrapper<MinioInfo> wrapper = new QueryWrapper<>();

        wrapper.eq("resolving_power",resolvingPower);

        return  minioInfoMapper.selectList(wrapper);
    }

    public void updateMinio(MinioInfo minioInfo, Integer id){
        QueryWrapper<MinioInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        minioInfoMapper.update(minioInfo,wrapper);
    }

    /**
     * 添加新的存储桶
     */
    public void insertMinio(MinioInfo minioInfo){

        minioInfoMapper.insert(minioInfo);
    }
    /**
     * 根据分辨率查找多个存储桶  (客户端显示)
     */
    public IPage<MinioInfo> selectAllMinio(String resolvingPower,Integer offset,Integer page){

        QueryWrapper<MinioInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("resolving_power",resolvingPower);
        Page<MinioInfo> page1 = new Page<>(offset, page);
        return minioInfoMapper.selectPage(page1,wrapper);
    }

    /**
     * 查询总数
     */
    public Integer selectCount(String resolvingPower){

        QueryWrapper<MinioInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("resolving_power",resolvingPower);

        return minioInfoMapper.selectCount(wrapper);
    }

    /**
     * 根据分辨率查找多个存储桶
     */
    public List<MinioInfo> selectMoreMinio(String resolvingPower){
        QueryWrapper<MinioInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("resolving_power",resolvingPower);

        return  minioInfoMapper.selectList(wrapper);

    }
}
