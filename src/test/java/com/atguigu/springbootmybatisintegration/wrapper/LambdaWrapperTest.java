package com.atguigu.springbootmybatisintegration.wrapper;

import com.atguigu.springbootmybatisintegration.entity.User;
import com.atguigu.springbootmybatisintegration.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ClassName: LambdaWrapperTest
 * Package: com.atguigu.springbootmybatisintegration.wrapper
 */
@SpringBootTest
public class LambdaWrapperTest {

    @Autowired
    private UserService userService;

    @Test
    public void testLambdaQueryWrapper() {
        // 查詢 name = Tom 的所有 User
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getName, "Tom");
        List<User> list = userService.list(lambdaQueryWrapper);
        list.forEach(System.out::println);
    }

    @Test
    public void testLambdaUpdateWrapper() {
        // 將 name = Tom 的所有 User 的油箱改為 Tom@tom.com
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getName, "Tom").set(User::getEmail, "Tom@tom.com");
        userService.update(lambdaUpdateWrapper);
    }
}
