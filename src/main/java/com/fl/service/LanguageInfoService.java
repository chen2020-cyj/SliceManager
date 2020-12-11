package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.LanguageInfo;
import com.fl.mapper.LanguageInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageInfoService extends ServiceImpl<LanguageInfoMapper, LanguageInfo> {
    @Autowired
    private LanguageInfoMapper languageInfoMapper;

    /**
     * 根据语言查找数据
     * @param language
     * @return
     */
    public LanguageInfo selectByLanguage(String language){
        QueryWrapper<LanguageInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("language",language);

        return languageInfoMapper.selectOne(wrapper);
    }

    public void insertLanguage(LanguageInfo languageInfo){

        languageInfoMapper.insert(languageInfo);
    }

    public void updateLanguage(Integer id,LanguageInfo languageInfo){
        QueryWrapper<LanguageInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        languageInfoMapper.update(languageInfo,wrapper);
    }

    /**
     * 根据id查找语言
     * @param id
     * @return
     */
    public LanguageInfo selectById(Integer id){
        QueryWrapper<LanguageInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return languageInfoMapper.selectOne(wrapper);
    }

    /**
     * 查找所有语言
     */
    public List<LanguageInfo> selectAllLanguage(){
        QueryWrapper<LanguageInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return languageInfoMapper.selectList(wrapper);
    }

}
