package com.fl.control;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.MinioInfo;
import com.fl.entity.NginxManager;
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
import com.fl.service.NginxManagerService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    private NginxManagerService nginxManagerService;


    private static final String url ="http://192.168.116.134:10110/addNginxConf";
    private static final String PRIVATE_KEY ="88630c3729f709506dabcc3a24cb8386";

    private static  Gson gson = new Gson();
    private MinioInfo minioInfo = new MinioInfo();
    private ResData res = new ResData();

    private ResMinio resMinio = new ResMinio();
    private ResFilmData resMinioData = new ResFilmData();
    private Msg msg = new Msg();
    @ApiOperation("添加一个存储桶")
    @PostMapping(value = "/addMinio",produces = "application/json;charset=UTF-8")
    public String addMinio(@RequestBody AddMinio addMinio) {


        msg.setMsg(addMinio.getData());

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

    @ApiOperation("查询多个存储桶")
    @PostMapping(value = "/selectAllMinio",produces = "application/json;charset=UTF-8")
    public String selectAllMinio(@RequestBody FindAllMinio findAllMinio){


//        Integer page = (findAllMinio.getPage()-1)*findAllMinio.getOffset();
        Integer offset = findAllMinio.getOffset();
//        User user = userService.selectUserInfo(findAllMinio.getUserId());

        List<MinioInfo> listMinioInfo = new ArrayList<>();



                IPage<MinioInfo> minioInfoIPage = minioInfoService.selectAllMinio(findAllMinio.getResolvingPower(), findAllMinio.getPage(), offset);
                listMinioInfo = minioInfoIPage.getRecords();

                for (int i=0;i<listMinioInfo.size();i++) {

                    System.out.println(listMinioInfo.get(i).getMsg());
                    Msg da = gson.fromJson(String.valueOf(listMinioInfo.get(i).getMsg()), Msg.class);

                    List<MinioData> list = gson.fromJson(String.valueOf(da.getMsg()), new TypeToken<List<MinioData>>() {
                    }.getType());


                    listMinioInfo.get(i).setMsg(list);

                }

                resMinio.setList(listMinioInfo);
                resMinio.setTotal(minioInfoService.selectCount(findAllMinio.getResolvingPower()));


                resMinioData.setCode(0);
                resMinioData.setMsg("success");
                resMinioData.setData(listMinioInfo);
                resMinioData.setTotal(resMinio.getTotal());



                return GsonUtils.toJson(resMinioData);

    }

        @ApiOperation("无法运行的存储桶")
        @PostMapping(value = "/updateMinio",produces = "application/json;charset=UTF-8")
        public ResData updateMinio(@RequestBody ReqChangeMinio reqChangeMinio) {
            String filmId = getRandomString(10);
            MinioInfo minio = minioInfoService.findMinio(reqChangeMinio.getId());
            minio.setUsageStatus("1");
            minioInfoService.updateMinio(minio,minio.getId());
            ResData data = new ResData();
            //算出已用空间
            double usedCapacity = minio.getTotalCapacity() - minio.getAvailableCapacity();
            //查找该清晰度的存储桶分配一个出去
            MinioInfo info = new MinioInfo();
            List<MinioInfo> minioInfoList = minioInfoService.findMinio(minio.getResolvingPower());
            for (int i =0;i<minioInfoList.size();i++){
//                double capacity = minioInfoList.get(i).getTotalCapacity()-minioInfoList.get(i).getAvailableCapacity();
                if (minioInfoList.get(i).getAvailableCapacity() >usedCapacity){
                    System.out.println(minioInfoList.get(i));
                    info = minioInfoList.get(i);
                    break;
                }
            }

            if (info.getId() != null){
                List<TaskManager> taskManagerList = taskManagerService.selectAllSegment();
                List<TaskManager> list = new ArrayList<>();
                for (int i = 0; i < taskManagerList.size(); i++) {
                    if (taskManagerList.get(i).getMinioId().contains(String.valueOf(reqChangeMinio.getId()))) {
                        list.add(taskManagerList.get(i));
                    }
                }
                for (int i=0;i<list.size();i++){
                    list.get(i).setMinioId(String.valueOf(info.getId()));
                    list.get(i).setFilmId(filmId);
                    info.setAvailableCapacity(info.getTotalCapacity()-Double.valueOf(list.get(i).getFilmSize()));
                    info.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
                    minioInfoService.updateMinio(info,info.getId());
                    taskManagerService.insertSegment(list.get(i));
                }
                data.setCode(0);
                data.setMsg("success");
                data.setData("");
                return data;
            }else {
                data.setCode(1);
                data.setMsg("error");
                data.setData("");
                return data;
            }

        }

    public  void addNginxUrl(String ip,Integer minioId){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url("")//请求的url
                .post(formBody(minioId,ip))
                .build();
        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    ResData resData = gson.fromJson(response.body().string(), ResData.class);
                    if (resData.getCode() == 0){
                        NginxManager nginxManager = new NginxManager();
                        nginxManager.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
                        nginxManager.setNginxUrl(String.valueOf(resData.getData()));
                        nginxManager.setMinioId(minioId);
                        nginxManagerService.insertNginxUrl(nginxManager);
                    }
//                    System.out.println(response.body().string());
                }
            }
        });
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
