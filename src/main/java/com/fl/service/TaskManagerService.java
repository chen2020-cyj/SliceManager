package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.TaskManager;
import com.fl.entity.User;
import com.fl.mapper.TaskManagerMapper;
import com.fl.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TaskManagerService extends ServiceImpl<TaskManagerMapper, TaskManager> {

    @Autowired
    private TaskManagerMapper taskManagerMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 查看多个任务
     * @param offset
     * @param page
     * @return
     */
    public IPage<TaskManager>  selectSegment(Integer offset, Integer page) {

        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("''", "");
        Page<TaskManager> page1 = new Page<>(offset, page);

        return taskManagerMapper.selectPage(page1, wrapper);
    }

    /**
     * 添加新任务
     * @param segment
     */
    public void insertSegment(TaskManager segment){

        taskManagerMapper.insert(segment);
    }

    /**
     * 根据分辨率和电影名称查询
     * @param map
     * @return
     */
    public List<TaskManager> selectSegmentOne(Map<String,String> map){

        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.allEq(map);

        return taskManagerMapper.selectList(wrapper);
    }

    public TaskManager selectSegmentFilmOne(String filmId){

        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        return taskManagerMapper.selectOne(wrapper);
    }

    /**
     *  根据state状态获取数据
     */
    public IPage<TaskManager> selectOneTask(String state,Integer offset,Integer page){

        QueryWrapper<TaskManager> queryWrapper = new QueryWrapper<>();


        if (state.equals("1")){
            queryWrapper.ne("download_state",state).and(wrapper->wrapper.ne("download_state","0"));
        }else {
            queryWrapper.eq("download_state",state);
        }

        Page<TaskManager> page1 = new Page<>(offset,page);


        return taskManagerMapper.selectPage(page1,queryWrapper);
    }
    /**
     * 字幕链接或者bt种子链接出错
     * @param btFail
     * @param subtitleFail
     */
    public TaskManager selectLinkFail(int btFail, int subtitleFail){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("link_state",btFail);
        TaskManager taskManager = taskManagerMapper.selectOne(wrapper);
        wrapper.eq("link_state",subtitleFail);
        TaskManager taskManager1 = taskManagerMapper.selectOne(wrapper);
        wrapper.eq("link_state",btFail+subtitleFail);
        TaskManager taskManager2 = taskManagerMapper.selectOne(wrapper);

        if (taskManager != null){
            return taskManager;
        }else if (taskManager1 != null){
            return  taskManager1;
        }else if (taskManager2 != null){
            return  taskManager2;
        }else {
            return null;
        }
    }
    /**
     * 根据id  更新任务表
     */
    public void updateIdTask(Integer id,String state){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        TaskManager taskManager = taskManagerMapper.selectOne(wrapper);
        taskManager.setDownloadState(state);

        taskManagerMapper.update(taskManager,wrapper);
    }

    /**
     * 查询任务数量
     */
    public Integer selectAllTask(){

        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return taskManagerMapper.selectCount(wrapper);
    }

    /**
     * 查询download_state状态
     */
    public IPage<TaskManager> selectState(Integer offset,Integer page,String state){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("download_state",state);

        Page<TaskManager> page1 = new Page<>(offset,page);

        return  taskManagerMapper.selectPage(page1,wrapper);
    }
    /**
     * 根据download_state状态获取数量
     */
    public Integer selectStateCount(String state){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("download_state",state);

        return  taskManagerMapper.selectCount(wrapper);
    }
    /**
     * 根据filmId查询
     */
    public TaskManager selectByFilmId(String filmId){

        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        return taskManagerMapper.selectOne(wrapper);
    }
    /**
     * 根据filmId删除
     */
    public void delTask(String filmId){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        taskManagerMapper.delete(wrapper);
    }
}
