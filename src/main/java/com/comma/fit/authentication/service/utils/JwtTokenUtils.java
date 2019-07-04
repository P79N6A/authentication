package com.comma.fit.authentication.service.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;


/**
 *  token utils
 * @author yumaoyuan
 */
public class JwtTokenUtils {

    private static Logger LOG = LoggerFactory.getLogger(JwtTokenUtils.class);


    private static Key generatorKey(){
        SignatureAlgorithm saa=SignatureAlgorithm.HS256;
        byte[] bin=DatatypeConverter.parseBase64Binary
                ("f3973b64918e4324ad85acea1b6cbec5");
        Key key=new SecretKeySpec(bin,saa.getJcaName());
        return key;
    }


    public static String generatorToken(Map<String,Object> payLoad){
        ObjectMapper objectMapper=new ObjectMapper();

        try {

            return Jwts.builder().setPayload(objectMapper.writeValueAsString(payLoad))
            .signWith(SignatureAlgorithm.HS256,generatorKey()).compact();

        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }
        return null;
    }


    public static Claims phaseToken(String token){
        Jws<Claims> claimsJwt=Jwts.parser().setSigningKey(generatorKey()).parseClaimsJws(token);

        return claimsJwt.getBody();
    }



    /**
     * 获得Token中的信息无需secret解密也能获得
     * @param token
     * @return
     */
    public static Map<String,String> getParams(String token) {
        Map<String,String> map=new HashMap<>();
        try {
            int indexBegin=token.indexOf(".");
            int indexLast=token.lastIndexOf(".");
            String str=token.substring(indexBegin+1,indexLast);
            String jsonString=new String(Base64.decode(str));
            LOG.info("token解码josn：{}",jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            String refreshKey=jsonObject.get("refreshKey").toString();
            String name=jsonObject.get("name").toString();
            map.put("refreshKey",refreshKey);
            map.put("userName",name);
            return map;
        } catch (Exception e) {
            LOG.info("异常原因",e);
            return null;
        }
    }


    public static void main(String[] args) {
        SignatureAlgorithm saa=SignatureAlgorithm.HS256;
        byte[] bin=DatatypeConverter.parseBase64Binary
                ("f3973b64918e4324ad85acea1b6cbec5");
        Key key=new SecretKeySpec(bin,saa.getJcaName());
        System.out.println(key);
    }
}
