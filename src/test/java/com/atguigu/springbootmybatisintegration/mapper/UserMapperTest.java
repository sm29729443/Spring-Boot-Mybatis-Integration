package com.atguigu.springbootmybatisintegration.mapper;

import com.atguigu.springbootmybatisintegration.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: UserMapperTest
 * Package: com.atguigu.springbootmybatisintegration.mapper
 */
@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testList() {
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }
}