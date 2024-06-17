package com.atguigu.springbootmybatisintegration.wrapper;

/**
 * ClassName: WrapperTest
 * Package: com.atguigu.springbootmybatisintegration.wrapper
 */

import com.atguigu.springbootmybatisintegration.entity.User;
import com.atguigu.springbootmybatisintegration.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class WrapperTest {
    @Autowired
    private UserService userService;

    @Test
    public void testQueryWrapper() {
        // 查詢 name = Tom 的所有 User
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("name", "Tom");

        // 查詢 email 域名為 baomidou.com 的所有 User
        QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
        // MyBatis 中不用自己去加上%%，MyBatis 會為我們加上
        queryWrapper2.like("email", "@baomidou.com");

        // 查詢所有 User 訊息並按照 age 字段降序排序
        QueryWrapper<User> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.orderByDesc("age");

        // 查詢 age 位於 [20,30] 的所有 User
        QueryWrapper<User> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.between(  "age", 20, 30);

        // 查詢 age 小於 20 或大於 30 的用戶
        QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
        queryWrapper5.lt("age", 20).or().gt("age", 30);

        // email 域名為 baomidou.com 且 age < 30 or age > 40 的 User
        QueryWrapper<User> queryWrapper6 = new QueryWrapper<>();
        queryWrapper6.like("email", "baomidou.com")
                .and(wrapper -> wrapper.lt("age", 30).or().gt("age", 40));

        // 共用查詢結果程式碼
        List<User> list = userService.list(queryWrapper6);
        list.forEach(System.out::println);
    }

    @Test
    public void testUpdateWrapper() {
        // 將 name = Tom 的 User 的 email 改為 Tom@baobidou.com
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name", "Tom");
        updateWrapper.set("email", "Tom@baomidou.com");
        userService.update(updateWrapper);
    }
}
