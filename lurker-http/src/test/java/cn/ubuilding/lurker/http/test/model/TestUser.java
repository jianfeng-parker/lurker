package cn.ubuilding.lurker.http.test.model;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 2017/1/1 21:09
 */

public class TestUser {
    private String name;

    private int age;

    private List<String> list;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
