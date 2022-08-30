package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;

/**
 * 校验登录状态
 */

public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //TODO 1. 获取请求头中的token
        String token = request.getHeader("authorization");
        if(StringUtils.isEmpty(token)){
            response.setStatus(401);
            return false;
        }

        //TODO 2. 基于TOKEN获取redis中的用户
        Map<Object, Object>  userMap= stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY+token);

        //3. 判断用户是否存在
        if(userMap.isEmpty()){
            //4. 不存在，拦截，返回401未授权状态码
            response.setStatus(401);
            return false;
        }

        //TODO 5. 将查询到的Hash数据转为UserDTO对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        //TODO 6. 存在，保存用户信息到ThreadLocal
        UserHolder.saveUser(userDTO);

        //TODO 7. 刷新token有效期

        //7. 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
