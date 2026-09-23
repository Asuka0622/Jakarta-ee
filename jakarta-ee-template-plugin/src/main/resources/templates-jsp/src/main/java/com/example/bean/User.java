package com.example.bean;

import java.io.Serializable;

/**
 * JavaBean 示例：用户信息
 *
 * JavaBean 三要素：
 *   1. 私有属性（private）
 *   2. 公开的 getter / setter 方法
 *   3. 无参构造方法（不写任何构造方法时默认就有）
 *
 * 可以实现 Serializable 接口（可选，但框架通常要求）。
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private int age;
    private String email;

    // 无参构造（JavaBean 必须有）
    public User() {
    }

    // 全参构造（方便使用，但不是 JavaBean 必须的）
    public User(String username, int age, String email) {
        this.username = username;
        this.age = age;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 自定义方法：判断是否成年
     * 注意：is 开头的 boolean 方法也是一种特殊的 getter
     */
    public boolean isAdult() {
        return age >= 18;
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', age=" + age + ", email='" + email + "'}";
    }
}
