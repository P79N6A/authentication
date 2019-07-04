package com.comma.fit.authentication.service.serviceImpl;


import cn.hutool.core.date.DateUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.comma.fit.api.interf.admin.vo.AdminVObj;
import com.comma.fit.api.redis.RedisService;
import com.comma.fit.api.service.admin.AdminService;
import com.comma.fit.authentication.service.service.UserService;
import com.comma.fit.authentication.service.utils.JwtTokenUtils;
import com.comma.fit.authentication.service.utils.MD5Util;
import com.comma.fit.constant.CommonConstant;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Reference(version = CommonConstant.VERSION)
    private RedisService redisService;
    @Reference(version = CommonConstant.VERSION)
    private AdminService adminService;

    @Override
    public boolean checkUserByName(String name,String passWord) {
        AdminVObj user=adminService.selectBaseInfoByEmail(name);
        String pw= MD5Util.md5(passWord);
        if(user!=null&&user.getPassword().equals(pw)){
            return true;
        }
        return false;
    }

    /**
     * 有效期内获取新的token
     * @param request
     * @return
     */
    @Override
    public String getFreshToken(HttpServletRequest request) {
        //获取token
        String token=request.getHeader("authentication");
        String userName=JwtTokenUtils.getParams(token).get("userName");
        //解析token 获取时间戳
        long accTokenTime=Long.valueOf(JwtTokenUtils.getParams(token).get("refreshKey"));
        //从缓存中获取refreshtoken
        long refreshTokenTime= Long.valueOf(redisService.get(userName));
        if((refreshTokenTime==accTokenTime)&&(refreshTokenTime>=(System.currentTimeMillis()))){

            Map map = new LinkedHashMap();
            //refreshTime 时间
            long refreshTime= DateUtil.offsetDay(new Date(), 30).getTime();
            //access_token过期时间一天
            map.put("exp", DateUtil.offsetDay(new Date(), 1).getTime());
            map.put("name",userName);
            map.put("refreshKey",refreshTime);

            token =JwtTokenUtils.generatorToken(map);
            redisService.setEx(userName,String.valueOf(refreshTokenTime),DateUtil.offsetDay(new Date(), 30).getTime(), TimeUnit.MICROSECONDS);
            return token;
        }

        return null;
    }


}
