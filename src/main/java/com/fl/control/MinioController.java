package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.aop.annotation.Log;
import com.fl.entity.MinioInfo;

import com.fl.entity.SystemManager;
import com.fl.entity.TaskManager;
import com.fl.entity.User;
import com.fl.model.MinioData;
import com.fl.model.Msg;
import com.fl.model.SegmentState;
import com.fl.model.SegmentUploadState;
import com.fl.model.clientReq.AddMinio;
import com.fl.model.clientReq.AddServerInfo;
import com.fl.model.clientReq.FindAllMinio;
import com.fl.model.clientReq.ReqChangeMinio;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResFilmData;
import com.fl.model.clientRes.ResMinio;
import com.fl.service.MinioInfoService;
import com.fl.service.SystemManagerService;
import com.fl.service.TaskManagerService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import com.fl.utils.Md5Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static javax.crypto.Cipher.PRIVATE_KEY;

@Api(tags = "存储桶管理接口")
@RestController
public class MinioController {


    @Autowired
    private MinioInfoService minioInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskManagerService taskManagerService;

    @Autowired
    private SystemManagerService systemManagerService;


    private static final String url ="http://192.168.116.134:10110/addNginxConf";
    private static final String PRIVATE_KEY ="88630c3729f709506dabcc3a24cb8386";

    private static  Gson gson = new Gson();
    private MinioInfo minioInfo = new MinioInfo();
    private ResData res = new ResData();

    private ResMinio resMinio = new ResMinio();
    private ResFilmData resMinioData = new ResFilmData();
    private Msg msg = new Msg();

    @PreAuthorize("@zz.check('user:addMinio')")
    @Log("user:addMinio")
    @ApiOperation("添加一个存储桶")
    @PostMapping(value = "/addMinio",produces = "application/json;charset=UTF-8")
    public String addMinio(@RequestBody AddMinio addMinio) {


        msg.setMsg(addMinio.getData());

        minioInfo.setNickName(addMinio.getNickName());
        minioInfo.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000));
        minioInfo.setResolvingPower(addMinio.getResolvingPower());
        minioInfo.setMsg(GsonUtils.toJson(msg));
        minioInfo.setTotalCapacity(addMinio.getTotalCapacity());
        minioInfo.setArea(addMinio.getArea());
        minioInfo.setAvailableCapacity(addMinio.getAvailableCapacity());
        minioInfo.setUsageStatus("0");
        minioInfoService.insertMinio(minioInfo);

        System.out.println(addMinio);

        resMinioData.setCode(0);
        resMinioData.setMsg("success");
        resMinioData.setData(minioInfo);
        resMinioData.setTotal(minioInfoService.selectCount(addMinio.getResolvingPower()));

//        addNginxUrl(addMinio.getData().get(0).getIp(),minioInfo.getId());
        return GsonUtils.toJson(resMinioData);

    }
    @PreAuthorize("@zz.check('menu:minioInfo')")
    @Log("user:selectAllMinio")
    @ApiOperation("查询多个存储桶")
    @PostMapping(value = "/selectAllMinio",produces = "application/json;charset=UTF-8")
    public String selectAllMinio(@RequestBody FindAllMinio findAllMinio){

        Integer offset = findAllMinio.getOffset();

        List<MinioInfo> listMinioInfo = new ArrayList<>();

                IPage<MinioInfo> minioInfoIPage = minioInfoService.selectAllMinio(findAllMinio.getResolvingPower(), findAllMinio.getPage(), offset);
                listMinioInfo = minioInfoIPage.getRecords();
//                List<MinioInfo> list = new ArrayList<>();
                for (int i=0;i<listMinioInfo.size();i++) {
                    if (listMinioInfo.get(i).getUsageStatus().equals("0")){
//                        System.out.println(listMinioInfo.get(i).getMsg());
                        Msg da = gson.fromJson(String.valueOf(listMinioInfo.get(i).getMsg()), Msg.class);

                        List<MinioData> minioDataList = gson.fromJson(String.valueOf(da.getMsg()), new TypeToken<List<MinioData>>() {
                        }.getType());

                        listMinioInfo.get(i).setMsg(minioDataList);
                    }
                }


                resMinio.setList(listMinioInfo);
                resMinio.setTotal(minioInfoService.selectCount(findAllMinio.getResolvingPower()));

                resMinioData.setCode(0);
                resMinioData.setMsg("success");
                resMinioData.setData(listMinioInfo);
                resMinioData.setTotal(resMinio.getTotal());

                return GsonUtils.toJson(resMinioData);

    }
    @Log("user:readyMinio")
    @ApiOperation("存储桶预警")
    @PostMapping(value = "/readyMinio",produces = "application/json;charset=UTF-8")
    public ResData readyMinio(){
        ResData resData = new ResData();
        List<MinioInfo> minioInfoList = minioInfoService.selectAllMinio();
        List<MinioInfo> list = new ArrayList<>();

        SystemManager systemManager = systemManagerService.selectByOne(1);

        //剩余容量
        double availableCapacity = 0.0;
        String definition = "";
        for (int i = 0; i < minioInfoList.size(); i++) {
            if ( minioInfoList.get(i).getAvailableCapacity() > systemManager.getReadyMinioCapacity()) {
                if (definition.equals("")){
                    definition = minioInfoList.get(i).getResolvingPower();
                }else {
                    definition = definition + "," + minioInfoList.get(i).getResolvingPower();
                }
            }else {
                list.add(minioInfoList.get(i));
            }
        }

        String resolvingPower = "";
        for (int i = 0; i < list.size(); i++) {
            if (resolvingPower.equals("")){

                if (definition.contains(list.get(i).getResolvingPower())){

                }else {
                    resolvingPower = list.get(i).getResolvingPower();
                }
            }else {
                if (resolvingPower.contains(list.get(i).getResolvingPower())){

                }else {
                    if (definition.contains(list.get(i).getResolvingPower())){

                    }else {
                        resolvingPower = resolvingPower + ","+list.get(i).getResolvingPower();
                    }
                }
            }
        }
        if (!resolvingPower.equals("")){
            resData.setCode(1);
            resData.setMsg("容量不足的存储桶");
            resData.setData(resolvingPower);
        }else {
            resData.setCode(0);
            resData.setMsg("success");
            resData.setData("");
        }

        return resData;
//        return GsonUtils.toJson(resMinioData);

    }

    @ApiOperation("查询有哪些分辨率")
    @PostMapping(value = "/selectResolvingPower",produces = "application/json;charset=UTF-8")
    public ResData selectResolvingPower(){
        ResData resData = new ResData();
        List<MinioInfo> minioInfoList = minioInfoService.selectAllMinio();
        List<MinioInfo> list = new ArrayList<>();

        String str = "";

        for (int i = 0; i < minioInfoList.size(); i++) {

            if (str.equals("")){
                str = minioInfoList.get(i).getResolvingPower();
            }
            if (!str.contains(minioInfoList.get(i).getResolvingPower())){
                str = str + "," + minioInfoList.get(i).getResolvingPower();
            }
        }
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData(str);

        return resData;
//        return GsonUtils.toJson(resMinioData);

    }
        @Log("user:updateMinio")
        @ApiOperation("无法运行的存储桶")
        @PostMapping(value = "/updateMinio",produces = "application/json;charset=UTF-8")
        public ResData updateMinio(@RequestBody ReqChangeMinio reqChangeMinio) {
        String filmId = getRandomString(10);
        MinioInfo minio = minioInfoService.findMinio(reqChangeMinio.getId());
        minio.setUsageStatus("1");
        minioInfoService.updateMinio(minio, minio.getId());
        ResData data = new ResData();
        //算出已用空间
        double usedCapacity = minio.getTotalCapacity() - minio.getAvailableCapacity();
        //查找该清晰度的存储桶分配一个出去
        MinioInfo info = new MinioInfo();
        List<MinioInfo> minioInfoList = minioInfoService.findMinioByResolvingPower(minio.getResolvingPower());
        for (int i = 0; i < minioInfoList.size(); i++) {
//                double capacity = minioInfoList.get(i).getTotalCapacity()-minioInfoList.get(i).getAvailableCapacity();
            if (minioInfoList.get(i).getAvailableCapacity() > usedCapacity) {
                System.out.println(minioInfoList.get(i));
                info = minioInfoList.get(i);
                break;
            }
        }

        if (info.getId() != null) {
            List<TaskManager> taskManagerList = taskManagerService.selectAllSegment();
            List<TaskManager> list = new ArrayList<>();
            for (int i = 0; i < taskManagerList.size(); i++) {
                if (taskManagerList.get(i).getMinioId().contains(String.valueOf(reqChangeMinio.getId()))) {
                    list.add(taskManagerList.get(i));
                }
            }
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setMinioId(String.valueOf(info.getId()));
                list.get(i).setFilmId(filmId);
                info.setAvailableCapacity(info.getTotalCapacity() - Double.valueOf(list.get(i).getFilmSize()));
                info.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
                minioInfoService.updateMinio(info, info.getId());
                taskManagerService.insertSegment(list.get(i));
            }
            data.setCode(0);
            data.setMsg("success");
            data.setData("");
            return data;
        } else {
            data.setCode(1);
            data.setMsg("error");
            data.setData("");
            return data;
        }

    }


    public static FormBody formBody(Integer id ,String ip){
        Map<String,Object> map = new ConcurrentHashMap<>();
        map.put("id",id);
        map.put("ip",ip);
        map.put("sign",PRIVATE_KEY);
//        ASSICMD5 assicmd5 = new ASSICMD5();
        String signToken = Md5Utils.getSignToken(map);
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("ip", ip)
                .add("timestamp",String.valueOf(System.currentTimeMillis()/1000))
                .add("sign",signToken)
                .build();
        return formBody;
    }
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
