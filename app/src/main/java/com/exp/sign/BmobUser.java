package com.exp.sign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class BmobUser extends BmobObject {

    private String name;
    private String username;
    private String password;
    private String sex;
    private String height;
    private String email;
    private String weight;
    private String match;
    private String url;
    private String bio;
    private String partners;

    public List<String> getPartnersList() {
        List<String> c = JSONArray.parseArray(partners, String.class);
        return c == null ? new ArrayList<String>() : c;
    }

    public String getPartners() {
        return partners;
    }

    public void setPartners(String partners) {
        this.partners = partners;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Match getMatchObejct() {
        return JSONObject.parseObject(match, Match.class);
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
