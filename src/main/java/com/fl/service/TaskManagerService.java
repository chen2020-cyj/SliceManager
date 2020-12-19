package com.fl.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fl.entity.TaskManager;
import com.fl.entity.User;
import com.fl.mapper.TaskManagerMapper;
import com.fl.mapper.UserMapper;
import com.fl.model.ResTaskInfoMapper;
import com.fl.model.clientReq.FindSegmentTask;
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
    public Integer selectTaskCount(){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");


        return taskManagerMapper.selectCount(wrapper);
    }
    public List<TaskManager> selectByFilmName(Map<String,String> map){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.allEq(map);
        return taskManagerMapper.selectList(wrapper);
    }
    /**
     * 查询所有任务
     */
    public List<TaskManager> selectAllSegment(){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("''","");

        return taskManagerMapper.selectList(wrapper);
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
     * @param
     * @return
     */
    public List<TaskManager> selectSegmentList(String resolvingPower,String languageId,String doubanId){

        QueryWrapper<TaskManager> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("douban_id",doubanId).and(Wrapper->Wrapper.eq("resolving_power",resolvingPower)).and(Wrapper->Wrapper.eq("language_id",languageId));
        return taskManagerMapper.selectList(queryWrapper);
    }

    public TaskManager selectTaskOne(String filmName,String languageId){
        QueryWrapper<TaskManager> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("film_name",filmName).and(Wrapper->Wrapper.eq("language_id",languageId));

        return taskManagerMapper.selectOne(queryWrapper);
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
     * 根据filmId  更新download_state
     */
    public void updateIdTask(String filmId,String state){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        TaskManager taskManager = taskManagerMapper.selectOne(wrapper);
        taskManager.setDownloadState(state);

        taskManagerMapper.update(taskManager,wrapper);
    }
    /**
     * 根据filmId 更新link_state
     */
    public void updateIdLinkState(String filmId,String state){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        TaskManager taskManager = taskManagerMapper.selectOne(wrapper);
        if (taskManager.getLinkState().equals("")){
            taskManager.setLinkState(state);
        }else {
            taskManager.setLinkState(taskManager.getLinkState()+","+state);
        }
        taskManagerMapper.update(taskManager,wrapper);
    }
    /**
     * 根据filmId 更新segment_state
     */
    public void updateIdSegmentState(String filmId,String state){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        TaskManager taskManager = taskManagerMapper.selectOne(wrapper);
        taskManager.setSegmentState(state);
        taskManagerMapper.update(taskManager,wrapper);
    }
    /**
     * 根据filmId 更新upload_state
     */
    public void updateUploadState(String filmId,TaskManager taskManager){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);


        taskManagerMapper.update(taskManager,wrapper);
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
    public TaskManager delTask(String filmId){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);

        taskManagerMapper.delete(wrapper);
        return taskManagerMapper.selectOne(wrapper);
    }
    /**
     * 更新失败的url链接
     */
    public String updateFailUrl(String filmId,String btUrl,String subtitleUrl){
        QueryWrapper<TaskManager> wrapper = new QueryWrapper<>();
        wrapper.eq("film_id",filmId);
        TaskManager taskManager = taskManagerMapper.selectOne(wrapper);

        if (btUrl.equals("") && subtitleUrl.equals("")){
            return "";
        }else if (!btUrl.equals("") && subtitleUrl.equals("")){
            taskManager.setBtUrl(btUrl);
            System.out.println(taskManager);
            taskManagerMapper.update(taskManager,wrapper);
            return "success";
        }else if (btUrl.equals("") && !subtitleUrl.equals("")){
            taskManager.setSubtitleUrl(subtitleUrl);
            taskManagerMapper.update(taskManager,wrapper);
            return "success";
        }else if (!btUrl.equals("") && !subtitleUrl.equals("")){
            taskManager.setSubtitleUrl(subtitleUrl);
            taskManager.setBtUrl(btUrl);
            taskManagerMapper.update(taskManager,wrapper);
            return "success";
        }else {
            return "";
        }
    }
    /**
     * 查询所有的任务 多条件
     */
    public ResTaskInfoMapper selectAllTaskManager(Integer id, FindSegmentTask task){
        QueryWrapper<TaskManager> queryWrapper = new QueryWrapper<>();
        switch (id){
            case 1:
                //  downloadState不传 linkState不传 id = 1
                queryWrapper.eq("''","");
                break;
            case 2:
                // downloadState不传 linkState传 id = 2
                queryWrapper.eq("link_state",task.getLinkState());
                break;
            case 3:
                // downloadState传 linkState不传 id = 3
                queryWrapper.eq("download_state",task.getDownloadState());
                break;
            case 4:
                //downloadState传 linkState传 id = 4
                queryWrapper.eq("download_state",task.getDownloadState()).and(Wrapper->Wrapper.eq("link_state",task.getLinkState()));
                break;

        }
        ResTaskInfoMapper resTaskInfoMapper = new ResTaskInfoMapper();
        resTaskInfoMapper.setCount(taskManagerMapper.selectCount(queryWrapper));
        IPage<TaskManager> iPage = new Page<>(task.getPage(),task.getOffset());
        resTaskInfoMapper.setIPage(taskManagerMapper.selectPage(iPage,queryWrapper));

        return resTaskInfoMapper;
    }
}
