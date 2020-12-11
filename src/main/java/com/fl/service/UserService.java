package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.User;
import com.fl.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Autowired
    UserMapper userMapper;



    public User login(Map<String, String> map) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        wrapper.allEq(map);

        User user = userMapper.selectOne(wrapper);
        return user;
    }
    public User selectByUser(String username){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);

        return userMapper.selectOne(wrapper);

    }
    public void register(User user) {
        user.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        userMapper.insert(user);
    }

    public void updateToken(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",user.getUsername());

        int update = userMapper.update(user, queryWrapper);

    }

    public User selectUserInfo(Integer id){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return  userMapper.selectOne(wrapper);
    }

    public List<User> testtt(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        wrapper.eq("''","");

        return  userMapper.selectList(wrapper);
    }

    public User getUserByToken(String token){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("token",token);

        return  userMapper.selectOne(wrapper);
    }
}
