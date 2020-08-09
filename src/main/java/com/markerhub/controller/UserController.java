package com.markerhub.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.User;
import com.markerhub.service.UserService;
import com.markerhub.util.JwtUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author llk
 * @since 2020-08-01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @RequiresAuthentication
    @GetMapping("/index")
    public Result index() {
        User user = userService.getById(1L);
        return Result.succ(user);
    }


    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user, HttpServletResponse response) {
        User user_temp = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if(user_temp == null){
            user.setPassword(SecureUtil.md5(user.getPassword()));//加密存
            boolean saveres = userService.save(user);
            if (saveres){
                User user_temp1 = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
                //创建jwt
                String register_jwt = jwtUtils.generateToken(user_temp1.getId());
                response.setHeader("Authorization", register_jwt);
                response.setHeader("Access-control-Expose-Headers", "Authorization");
                return Result.succ(MapUtil.builder()
                        .put("id", user_temp1.getId())
                        .put("username", user.getUsername())
                        .put("avatar", user.getAvatar())
                        .put("email", user.getEmail())
                        .map());
            }else {
                return Result.fail("注册/创建失败");
            }
        }else {
            return Result.fail("用户已存在");
        }

    }
}
