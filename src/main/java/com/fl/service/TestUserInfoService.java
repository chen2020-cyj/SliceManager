package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.TestUserInfo;
import com.fl.mapper.TestUserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestUserInfoService extends ServiceImpl<TestUserInfoMapper, TestUserInfo> {
    @Autowired
    TestUserInfoMapper testUserInfoMapper;

    public TestUserInfo selectUser(Integer id){

        QueryWrapper<TestUserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return testUserInfoMapper.selectOne(wrapper);
    }
}
