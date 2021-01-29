package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.VideoM3U8ErrInfo;
import com.fl.mapper.VideoM3U8ErrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoM3U8ErrInfoService extends ServiceImpl<VideoM3U8ErrInfoMapper,VideoM3U8ErrInfo> {
    @Autowired
    private VideoM3U8ErrInfoMapper videoM3U8ErrInfoMapper;


    /**
     * 查询所有的错误信息
     */
    public List<VideoM3U8ErrInfo> selectAll(Integer page,Integer offset){
        QueryWrapper<VideoM3U8ErrInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");
        IPage<VideoM3U8ErrInfo> iPage = new Page<>(page,offset);
//        System.out.println(videoM3U8ErrInfoMapper.selectPage(iPage,wrapper).getRecords());
        IPage<VideoM3U8ErrInfo> videoM3U8ErrInfoIPage = videoM3U8ErrInfoMapper.selectPage(iPage, wrapper);
        return videoM3U8ErrInfoIPage.getRecords();
    }
    public int count(){
        QueryWrapper<VideoM3U8ErrInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return videoM3U8ErrInfoMapper.selectCount(wrapper);
    }
    /**
     * 根据id查找
     */
    public VideoM3U8ErrInfo selectById(Integer id){
       return videoM3U8ErrInfoMapper.selectById(id);
    }

    /**
     * 删除错误数据
     */
    public void delById(Integer id){
        QueryWrapper<VideoM3U8ErrInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        videoM3U8ErrInfoMapper.delete(wrapper);
    }
}
