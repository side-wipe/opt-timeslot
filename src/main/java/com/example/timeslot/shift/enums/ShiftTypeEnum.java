package com.example.timeslot.shift.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhifeng
 * @Description: 班次类型
 */
public enum ShiftTypeEnum {


    MORNING("MORNING", "早班"),
    LUNCH("LUNCH", "午班"),
    NIGHT("NIGHT", "晚班"),
    REST("REST", "休息"),
    TIANDI("TIANDI", "天地班");

    private String code;

    private String desc;

    ShiftTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    static Map<String, ShiftTypeEnum> map = new HashMap<>();

    static {
        for (ShiftTypeEnum value : ShiftTypeEnum.values()) {
            map.put(value.getCode(), value);
        }
    }

    public static ShiftTypeEnum getByCode(String code) {
        return map.get(code);
    }

    public static List<ShiftTypeEnum> getList(){
        return Arrays.asList(ShiftTypeEnum.values());
    }
}
