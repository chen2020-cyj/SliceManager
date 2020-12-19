package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.Search;
import com.fl.mapper.SearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchService extends ServiceImpl<SearchMapper, Search> {
    @Autowired
    private SearchMapper searchMapper;

    /**
     * 查询所有数据
     */
    public List<Search> selectAll(){
        QueryWrapper<Search> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return searchMapper.selectList( wrapper);
    }
    public Search selectByYear(Map<String,String> map,String categoryId){
        QueryWrapper<Search> wrapper = new QueryWrapper<>();
        wrapper.allEq(map).and(i->i.eq("category_id",categoryId));

        return searchMapper.selectOne(wrapper);
    }
}
