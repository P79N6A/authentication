package com.comma.fit.authentication.service.common;

public interface Constant {

    /**
     * 账号密码验证异常
     */
    String EXCEPTION_MESSAGE="JWT 令牌无效";

    /**
     * 服务认证通过
     */
    Integer PASS=200;

    /**
     * 密码验证未通过
     */
    Integer NOT_PASS=400;

    /**
     * 令牌过期
     */

    String TOKEN_EXPIRED="令牌过期";
    /**
     * Token 需要刷新
     */

    Integer REFRESH=800;

    /**
     * fresh Token 刷新成功
     */
    Integer FRESH_SUCCESS=801;

    /**
     * fresh Token 过期
     */
    Integer TOKEN_OVERDUE =804;
}
