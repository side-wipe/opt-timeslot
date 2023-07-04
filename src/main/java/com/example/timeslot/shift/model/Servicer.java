package com.example.timeslot.shift.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.timeslot.shift.enums.ShiftTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhifeng
 * @Description: 客服小二排班模型
 * @Date: 2023/5/20 14
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Servicer {

    /**
     * 小二id
     */
    private Long id;

    /**
     * 小二姓名
     */
    private String name;

    /**
     * 小二花名
     */
    private String nick;

    /**
     * 小二工号
     */
    private String empId;

    /**
     * cph
     */
    private BigDecimal cph = new BigDecimal("5");

    /**
     * 最近一次上班时间
     */
    private LocalDateTime lastShiftTime;

    /**
     * 本月补贴金
     */
    private Integer subsidy = 0;

    /**
     * 自定义诉求
     */
    private CustomAppeal customAppeal;

    public Servicer(Long id, String name, String nick, String empId) {
        this.id = id;
        this.name = name;
        this.nick = nick;
        this.empId = empId;
    }

    @Override
    public String toString() {
        return id + "" + name;
    }

    @Override
    public boolean equals(Object other) {

        if(other == this) {
            return true;
        }
        if(!(other instanceof Servicer)) {
            return false;
        }

        Servicer o = (Servicer)other;
        return o.id.equals(id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + id.hashCode();
        return result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CustomAppeal{

        /**
         * 日期
         */
        private String worksetDate;

        /**
         * 接受的班次类型
         */
        private List<ShiftTypeEnum> receiveShiftTypes;

        /**
         * 拒绝的班次类型
         */
        private List<ShiftTypeEnum> mutexShiftTypes;

        /**
         * 指定班次
         */
        private Long workShiftId;
    }

}
