package com.comma.fit.authentication.service.Realm;


import com.alibaba.dubbo.config.annotation.Reference;
import com.comma.fit.api.interf.admin.vo.AdminVObj;
import com.comma.fit.api.service.admin.AdminService;
import com.comma.fit.authentication.service.common.Constant;
import com.comma.fit.authentication.service.entity.JwtToken;
import com.comma.fit.authentication.service.utils.JwtTokenUtils;
import com.comma.fit.constant.CommonConstant;
import io.jsonwebtoken.*;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * shiroRealm  可支持多个realm
 */
public class ShiroRealm extends AuthorizingRealm {


    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroRealm.class);

    @Reference(version = CommonConstant.VERSION)
    private AdminService adminService;

    @Override
    public boolean supports(AuthenticationToken token) {

        //仅支持jwtToken
        return token instanceof JwtToken;
    }

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;

    }


    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        String userName="";
        String token=(String) authenticationToken.getCredentials();
        //校验token
        try{
            Claims claims= JwtTokenUtils.phaseToken(token);
            userName=claims.get("name").toString();
            AdminVObj user =adminService.selectBaseInfoByEmail(userName);
            /**
             * 具体判断逻辑，相关参数直接从claims中取即可,直接与数据库做对比
             */

            if(user==null){
                throw new AuthenticationException(Constant.EXCEPTION_MESSAGE);
            }
            LOGGER.info(userName+" token 认证通过");
        }catch (ExpiredJwtException e) {
            LOGGER.error(userName+" token JWT 令牌过期,错误原因：{}",e);
            throw new AuthenticationException("JWT 令牌过期:" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error(userName+" token JWT 令牌无效,错误原因：{}",e);
            throw new AuthenticationException("JWT 令牌无效:" + e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error(userName+" JWT 令牌格式错误,错误原因：{}",e);
            throw new AuthenticationException("JWT 令牌格式错误:" + e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error(userName+" JWT 令牌签名无效,错误原因：{}",e);
            throw new AuthenticationException("JWT 令牌签名无效:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error(userName+" JWT 令牌参数异常,错误原因：{}",e);
            throw new AuthenticationException("JWT 令牌参数异常:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error(userName+" JWT 令牌错误,错误原因：{}",e);
            throw new AuthenticationException("JWT 令牌错误:" + e.getMessage());
        }
        return new SimpleAuthenticationInfo(token, token, "shiroRealm");
    }
}
