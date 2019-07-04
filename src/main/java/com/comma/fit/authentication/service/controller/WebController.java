package com.comma.fit.authentication.service.controller;


import cn.hutool.core.date.DateUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.comma.fit.api.redis.RedisService;
import com.comma.fit.authentication.service.common.Constant;
import com.comma.fit.authentication.service.response.ResponseBean;
import com.comma.fit.authentication.service.service.UserService;
import com.comma.fit.authentication.service.utils.JwtTokenUtils;
import com.comma.fit.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/comma")
public class WebController {


    private static final Logger LOG= LoggerFactory.getLogger(WebController.class);



    @Resource
    UserService userService;

    @Reference(version = CommonConstant.VERSION)
    private RedisService redisService;




    @RequestMapping("/getToken")
    public ResponseBean getToken(String userName){

        LOG.info("申请令牌:userName:"+userName);
            /**
             * 签发token
             */

            long refreshTokenTime= DateUtil.offsetDay(new Date(), 30).getTime();//refreshTime 时间

            LOG.info("userName："+userName+"都token期限时间戳为"+refreshTokenTime);

            Map map = new LinkedHashMap();
            map.put("refreshKey",refreshTokenTime);
            map.put("exp", DateUtil.offsetDay(new Date(), 5).getTime());//access_token过期时间
            map.put("name",userName);
            String accessToken=JwtTokenUtils.generatorToken(map);
            /**
             * 以用户userName为key，把refresh存入redis,有效期三十天
             */
            redisService.setEx(userName,String.valueOf(refreshTokenTime),DateUtil.offsetDay(new Date(), 30).getTime(), TimeUnit.MICROSECONDS);

            LOG.info("用户："+userName+"获发token："+accessToken+"refreshToken"+refreshTokenTime);

            return new ResponseBean(Constant.PASS, "login success",accessToken);

    }


    /**
     * token过期重新获取token
     */

    @RequestMapping("/refreshToken")
    public ResponseBean refreshToken(HttpServletRequest request){

        LOG.info("调用refreshToken");

        String freshToken=userService.getFreshToken(request);

        if(null!=freshToken){
            LOG.info("获取refreshToken成功");
            return new ResponseBean(Constant.FRESH_SUCCESS,"fresh token success!",freshToken);
        }

        return new ResponseBean(Constant.TOKEN_OVERDUE,"token overdue");
    }



    /**
     * 服务认证接口，调用该接口，shiro+jwt会做预处理，通过则返回pass
     * @return
     */
    @RequestMapping("/authentication")
    public ResponseBean authentication(HttpServletRequest request,HttpServletResponse response){
        String method=request.getMethod();
        /**
         * 如果是token过期问题，则shiro不做拦截，给responseCode,通知申请方需要从新申请新的token
         */
        int responseCode= (int) request.getAttribute("responseCode");
        LOG.info("responseCode:"+responseCode);
        if(responseCode==Constant.REFRESH){
            LOG.info("接口方法:"+method+"开始认证服务:"+Constant.REFRESH);
            return new ResponseBean(Constant.REFRESH,"need refreshToken");
        }
        LOG.info("接口方法:"+method+"认证服务通过");
        return new ResponseBean(Constant.PASS,"authentication success");
    }

}
