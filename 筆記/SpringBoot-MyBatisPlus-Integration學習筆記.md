# MyBatis Plus 快速入門

對應的git repository Spring-Boot-Mybatis-Integration

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

- 當 spring-boot 啟動時，會去自動配置這些帶有`@Mapper`的 interface 成為 spring 容器。

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
