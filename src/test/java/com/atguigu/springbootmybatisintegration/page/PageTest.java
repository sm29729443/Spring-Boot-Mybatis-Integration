package com.atguigu.springbootmybatisintegration.page;

import com.atguigu.springbootmybatisintegration.entity.User;
import com.atguigu.springbootmybatisintegration.mapper.UserMapper;
import com.atguigu.springbootmybatisintegration.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: PageTest
 * Package: com.atguigu.springbootmybatisintegration.page
 */
@SpringBootTest
public class PageTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Test
    public void testPageService() {
        // 查詢第 2 頁，每頁有 3 條數據
        IPage<User> page = new Page<>(2, 3);
        IPage<User> result = userService.page(page);
        result.getRecords().forEach(System.out::println);
    }

    @Test
    public void testPageMapper(){
        IPage<User> page = new Page<>(2, 3);
        IPage<User> result = userMapper.selectPage(page, null);
        result.getRecords().forEach(System.out::println);
    }

    @Test
    public void testCustomMapper(){
        IPage<User> page = new Page<>(2, 3);
        IPage<User> result = userMapper.selectUserPage(page);
        result.getRecords().forEach(System.out::println);
    }
}
