package com.atguigu.springbootmybatisintegration.service.Impl;

import com.atguigu.springbootmybatisintegration.entity.User;
import com.atguigu.springbootmybatisintegration.mapper.UserMapper;
import com.atguigu.springbootmybatisintegration.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * ClassName: UserServiceImpl
 * Package: com.atguigu.springbootmybatisintegration.service.Impl
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
