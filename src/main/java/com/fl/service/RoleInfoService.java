package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.RoleInfo;
import com.fl.mapper.RoleInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleInfoService extends ServiceImpl<RoleInfoMapper, RoleInfo> {
    @Autowired
    private RoleInfoMapper roleInfoMapper;

    /**
     * 查询所有的角色组
     */
    public List<RoleInfo> selectAll(){
        QueryWrapper<RoleInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return roleInfoMapper.selectList(wrapper);
    }
    /**
     * 根据pid查找部下
     */
    public List<RoleInfo> selectByPid(Integer pid){
        QueryWrapper<RoleInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("pid",pid);

        return roleInfoMapper.selectList(wrapper);
    }
    /**
     * 根据id 查找
     */
    public RoleInfo selectById(Integer id){
        QueryWrapper<RoleInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        return roleInfoMapper.selectOne(wrapper);
    }
    /**
     * 删除角色组
     */
    public void delRoleById(Integer roleId){

        roleInfoMapper.deleteById(roleId);
    }
    /**
     * 根据角色组名称查找
     */
    public RoleInfo selectByRoleName(String name){
        QueryWrapper<RoleInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);

        return roleInfoMapper.selectOne(wrapper);
    }
}
