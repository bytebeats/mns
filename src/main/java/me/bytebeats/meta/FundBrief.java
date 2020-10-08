package me.bytebeats.meta;

import java.util.Objects;

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2020/10/8 14:03
 * @Version 1.0
 * @Description FundBrief means brief info of funds.
 */

public class FundBrief {
    private String code = "";
    private String name = "";
    private String type = "";

    public FundBrief(String code, String name, String type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean contains(String keyword) {
        if (keyword != null && !"".equals(keyword)) {
            return code.contains(keyword) || name.contains(keyword) || type.contains(keyword);
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundBrief fundBrief = (FundBrief) o;
        return Objects.equals(code, fundBrief.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "FundBrief{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
