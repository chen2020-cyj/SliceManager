package com.fl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fl.entity.FilmSourceRecord;
import com.fl.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {

    List<FilmSourceRecord>  selectPage(@Param("offset") int offset, @Param("page") int page);
}
