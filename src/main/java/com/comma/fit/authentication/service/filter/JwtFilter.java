package com.comma.fit.authentication.service.filter;

import com.comma.fit.authentication.service.common.Constant;
import com.comma.fit.authentication.service.entity.JwtToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtFilter extends BasicHttpAuthenticationFilter {

    private static final Logger LOG= LoggerFactory.getLogger(JwtFilter.class);




    /**
     * 拒绝登录后进入此方法，做一些跳转处理
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = getSubject(request, response);
        if (subject.getPrincipal() == null) {
            // 如果未登录，保存当前页面，重定向到登录页面
            saveRequestAndRedirectToLogin(request, response);
        } else {
                //不存在则返回404
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

        return false;
    }

    /**
     * 是否允许登录
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest=(HttpServletRequest) request;
        String accessToken=httpServletRequest.getHeader("authentication");
        JwtToken token=new JwtToken(accessToken);
        try{
            LOG.info("开始token验证");
            getSubject(request,response).login(token);
            request.setAttribute("responseCode",Constant.PASS);
            return true;
        }catch (Exception e){
            if(e.getMessage().contains(Constant.TOKEN_EXPIRED)){
                try{
                    request.setAttribute("responseCode",Constant.REFRESH);
                }catch (Exception e1){
                    LOG.info("request set exception!");
                }

            }
            LOG.info("登录认证出现异常"+e);
        }
        return false;
    }
}
