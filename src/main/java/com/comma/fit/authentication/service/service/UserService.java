package com.comma.fit.authentication.service.service;


import javax.servlet.http.HttpServletRequest;

public interface UserService {

    boolean checkUserByName(String name,String passWord);

    String getFreshToken(HttpServletRequest request);


}
