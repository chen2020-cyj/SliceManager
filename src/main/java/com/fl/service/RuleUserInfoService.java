package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.Rule;
import com.fl.mapper.RuleUserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleUserInfoService extends ServiceImpl<RuleUserInfoMapper, Rule> {
    @Autowired
    RuleUserInfoMapper ruleUserInfoMapper;

    public Rule selectUser(Integer id){

        QueryWrapper<Rule> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return ruleUserInfoMapper.selectOne(wrapper);
    }

    public Rule selectByRoles(String rule){
        QueryWrapper<Rule> wrapper = new QueryWrapper<>();
        wrapper.eq("roles",rule);

        return ruleUserInfoMapper.selectOne(wrapper);
    }
    public List<Rule> selectByRoles(){

        QueryWrapper<Rule> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return ruleUserInfoMapper.selectList(wrapper);
    }
}
