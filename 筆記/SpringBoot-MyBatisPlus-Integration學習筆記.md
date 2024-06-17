# MyBatis Plus 快速入門

對應的git repository 為 Spring-Boot-Mybatis-Integration

## 資料庫資料準備

```sql

CREATE DATABASE hello_mp CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE hello_mp;

DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主鍵ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT(11) NULL DEFAULT NULL COMMENT '年齡',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '信箱',
    PRIMARY KEY (id)
);

INSERT INTO user (id, name, age, email) VALUES
(1, 'Jone', 18, 'test1@baomidou.com'),
(2, 'Jack', 20, 'test2@baomidou.com'),
(3, 'Tom', 28, 'test3@baomidou.com'),
(4, 'Sandy', 21, 'test4@baomidou.com'),
(5, 'Billie', 24, 'test5@baomidou.com');
```

## Spring Boot 中使用 MyBatis Plus

Spring Boot 使用 MyBatis 很簡單，不像以前 SSM 要配置一大堆 XML檔。

### Step.1 引入 Maven 依賴

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.2</version>
</dependency>
```

### Step.2 在`application.properties`配置資料庫相關訊息

**這邊是採 yml 格式。**

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: Atguigu.123
    url: jdbc:mysql://192.168.246.100:3306/hello_mp?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2b8
```

##  創建實體類

```java
package com.atguigu.springbootmybatisintegration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("age")
    private Integer age;

    @TableField("email")
    private String email;
}
```

- `@TableName`:表名註解，用於標示 class 對應到哪張 Table，可以不加此註解，但 MyBatis 會默認此 class 對應到的 Table 是 class name 駝峰命名轉下畫線分隔，譬如 class name:User &rarr; Table name:user。
- `@TableId`:主鍵註解，用於標示此屬性是一個主鍵。
  - value:Table 的主鍵名。
  - type:聲明主鍵的生成策略，有 AUTO、ASSIGN_UUID、INPUT 等。
- `@TableField`:用於標示此屬性對應到 Table 的哪個字段。

## 創建通用 Mapper

### UserMapper

```java
package com.atguigu.springbootmybatisintegration.mapper;

import com.atguigu.springbootmybatisintegration.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

### JUnitTest

```java
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
```

### `@Mapper` 功能

- 取代 mapper.xml 配置檔。

    在未與 Spring boot 整合時，一般的 SSM Porject 都要編寫大量的 mapper.xml 檔，透過 xml 文件去描述 class 與 sql 的映射關係。

    譬如:

    ```xml
    <!-- UserMapper.xml -->
    <mapper namespace="com.example.mapper.UserMapper">
        <select id="getUserById" parameterType="int" resultType="User">
            SELECT * FROM users WHERE id = #{id}
        </select>
    </mapper>
    ```

    但對 interface 加上`@Mapper`註解後，此 interface 會直接變成一個 Mapper interface，此時就可直接在此 interface 去寫 sql 了。

    ```java
    @Mapper
    public interface UserMapper {
        @Select("SELECT * FROM users WHERE id = #{id}")
        User getUserById(int id);
    }
    ```

- 另一個功能是讓有`@mapper` annotation 的 mapper 成為一個 Bean，spring boot 一啟動後會自動配置這些有者`@mapper`的 mapper 成為一個 Bean。

- 若`@Mapper` interface 過多，可不用逐一配置`@Mapper`註解，可以使用`@MapperScan`指定 package path 掃描。

    ```java
    @SpringBootApplication
    @MapperScan("com.atguigu.hellomp.mapper")
    public class HelloMpApplication {

        public static void main(String[] args) {
            SpringApplication.run(HelloMpApplication.class, args);
        }
    }
    ```

## 通用 Service

MyBatis 不只提供了 dao 層的通用Mapper，也封裝了service層，提供了一個通用 Service。

### 通用 Service 實現步驟

#### Step.1 創建 Service Interface

主要是要 `extends IService<User>`

```java
public interface UserService extends IService<User> {
}
```

#### Step.2 創建 Service 實現類

當 implements `UserService` 後，應該要實現`IService<User>`的 Method 才對，那當然不可能是我們自己還要寫 Method 具體邏輯，因此是透過`extends ServiceImpl`來幫我們實現，而`ServiceImpl`傳入的泛型為 dao 層使用的 mapper 與 obj。

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
```

### JUnitTest 測試類

觀看`userService.getById()`等 source code，會看到就是去調用 mapper 執行操作。

```java
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Test
    public void testGetById() {
        User byId = userService.getById(1);
        System.out.println("byId:" + byId);
    }

    @Test
    public void testSaveOrUpdate() {
        User user1 = userService.getById(2);
        user1.setName("xiaohu");
        userService.saveOrUpdate(user1);

        User user2 = new User();
        user2.setName("lisi");
        user2.setAge(24);
        user2.setEmail("lisi@gmail.com");
        userService.saveOrUpdate(user2);
    }
    
    @Test
    public void testSaveBatch(){
        User user1 = new User();
        user1.setName("dongdong");
        user1.setAge(49);
        user1.setEmail("dongdong@email.com");

        User user2 = new User();
        user2.setName("nannan");
        user2.setAge(29);
        user2.setEmail("nannan@email.com");

        List<User> userList = List.of(user1, user2);
        userService.saveBatch(userList);
    }
}
```

## 條件構造器 `QueryWrapper`、`UpdateWrapper`

條件構造器是用來構造複雜的查詢條件的，譬如獲取 name = "zhangsan" 的數據，像前面介紹 MyBatis 所提供的查詢語句，都只能根據 id 去查詢，若想根據其他條件查詢，就得使用條件構造器，而 MyBatis 提供了兩種構造器，分別為`QueryWrapper`、`UpdateWrapper`。

### QueryWrapper

- 主要用於查詢、刪除操作。
- 只能構建 sql 中的 where 語句。

```java
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
        List<User> list = userService.list(queryWrapper5);
        list.forEach(System.out::println);
    }
```

### UpdateWrapper

- 主要用於更新操作。
- 可以構建 sql where 語句外，還能構建 set 語句。

```java
    @Test
    public void testUpdateWrapper() {
        // 將 name = Tom 的 User 的 email 改為 Tom@baobidou.com
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name", "Tom");
        updateWrapper.set("email", "Tom@baomidou.com");
        userService.update(updateWrapper);
    }
```

### LambdaWrapper

MyBatis 還提供了 Lambda 版本的 QueryWrapper、UpdateWrapper，與原先的版本相比功能是相同的，只是使用上更簡潔。

```java
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
```

## 分頁插件

MyBatis-Plus 提供了 分頁插件，可以快速的完成分頁查詢功能。

### 分頁插件配置

只要配置以下即可

```java
@Configuration
public class MPConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 根據所使用的資料庫傳遞不同的類型，譬如用 MySQL 就傳 DbType.MYSQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 分頁插件使用說明

MyBatis 提供了一個分頁物件`Page`，這個物件包含了分頁的各種訊息，其中核心的屬性有:

|屬性名|資料類型|default value|描述|
|------|-------|-------------|----|
|records|List|emptyList|查詢數據列表|
|total  |Long|0        |查詢的數據列表總數量|
|size   |Long|10       |每頁顯示的數據筆數|
|current|Long|1        |當前頁數|

分頁物件既可以當作分頁查詢的條件參數，也會當作分頁查詢的結果，若是當作條件參數，通常只需提供 `size`、`current` 屬性即可。

for example

```java
// Ipage 為一個 interface，Page 則為 implement class
IPage<T> page = new Page<>(current, size);
```

而 MyBatis-Plus 的 `BaseMapper`、`ServiceImpl`均提供了常用的分頁查詢的方法，例如:

- `BaseMapper`的分頁查詢

    ```java
    IPage<T> selectPage(IPage<T> page,Wrapper<T> queryWrapper);
    ```

- `ServiceImpl`的分頁查詢

    ```java
    // 無條件分頁查詢
    IPage<T> page(IPage<T> page);
    // 條件分頁查詢
    IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);
    ```

- 自定義 mapper

    分頁功能也允許在自定義的 sql 上使用。

    只要在 mapper interface 提供分頁物件作為參數及返回對象即可

    ```java
    IPage<UserVo> selectPageVo(IPage<?> page, Integer state);
    ```

    而在 Mapper.xml 裡的 sql 語句則無需關心分頁相關的邏輯

    ```xml
    <select id="selectPageVo" resultType="xxx.xxx.xxx.UserVo">
    SELECT id,name FROM user WHERE state=#{state}
    </select>
    ```

### JUnitTest 測試類

#### Service 分頁查詢使用

```java
    @Test
    public void testPageService() {
        // 查詢第 2 頁，每頁有 3 條數據
        Page<User> page = new Page<>(2, 3);
        Page<User> result = userService.page(page);
        result.getRecords().forEach(System.out::println);
    }
```

#### Mapper 分頁查詢使用

```java
    @Test
    public void testPageMapper(){
        IPage<User> page = new Page<>(2, 3);
        IPage<User> result = userMapper.selectPage(page, null);
        result.getRecords().forEach(System.out::println);
    }
```

#### 自定義 Mapper 分頁查詢使用

##### step1. 首先在 `UserMapper` 新增自定義分頁方法

```java
IPage<User> selectUserPage(IPage<User> page);
```

##### step2. 創建 `resources/mapper/UserMapper.xml` 文件

sql 語句也可直接在`UserMapper`透過 annotation 編寫。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.atguigu.springbootmybatisintegration.mapper.UserMapper">
    <select id="selectUserPage" resultType="com.atguigu.springbootmybatisintegration.entity.User">
        select * from user
    </select>
</mapper>
```

另外，spring boot 默認會去偵測路徑為 `classpath*:/mapper/**/*.xml` 的 Mapper.xml 文件，可以在 application.yml 中進行修改

```yml
mybatis-plus:
mapper-locations: classpath*:/mapper/**/*.xml
```

##### JUnitTest測試類

```java
    @Test
    public void testCustomMapper(){
        IPage<User> page = new Page<>(2, 3);
        IPage<User> result = userMapper.selectUserPage(page);
        result.getRecords().forEach(System.out::println);
    }
```

## MyBatisX 插件

這是 IDEA 一個可以根據資料庫快速生成 entity、mapper.xml、mapper、service 等的插件工具，使用方式不複雜，若要使用時有不懂在查就好。
