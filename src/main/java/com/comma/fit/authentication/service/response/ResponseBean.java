package com.comma.fit.authentication.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
public class ResponseBean{

    // http 状态码
    private int code;

    // 返回信息
    private String msg;


    // 返回的数据
    private Object data;

    public ResponseBean(int code,String msg){
        this.code=code;
        this.msg=msg;
    }


}

