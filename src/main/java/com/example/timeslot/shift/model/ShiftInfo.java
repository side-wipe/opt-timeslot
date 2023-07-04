package com.example.timeslot.shift.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateUtil;
import com.example.timeslot.shift.enums.ShiftTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhifeng
 * @Description: 排班数据
 * @Date: 2023/1/13 15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftInfo {

    /**
     * 班次id
     */
    private Long workshiftId;

    /**
     * 班次名称
     */
    private String workshiftName;

    /**
     * 工作时长
     */
    private Integer workHours;

    /**
     * 分值/权重
     */
    private Integer score = 0;

    /**
     * 补贴金额
     */
    private Integer subsidy = 0;

    /**
     * 班次类型
     */
    private ShiftTypeEnum shiftType;

    /**
     * 时段列表
     */
    private List<WorkshiftTime> workshiftTimeList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WorkshiftTime implements Serializable {

        /**
         * 开始时间
         */
        private LocalTime startTime;

        /**
         * 结束时间
         */
        private LocalTime endTime;

    }

    public LocalDateTime getStartTime(String workSetDate){
        List<WorkshiftTime> sort = this.workshiftTimeList.stream().sorted(
            Comparator.comparing(WorkshiftTime::getStartTime)).collect(Collectors.toList());
        LocalTime startTime = sort.get(0).getStartTime();
        return LocalDateTime.of(LocalDate.parse(workSetDate, DateTimeFormatter.ofPattern("yyyyMMdd")),startTime);
    }

    public LocalDateTime getEndTime(String workSetDate){
        List<WorkshiftTime> sort = this.workshiftTimeList.stream().sorted(
            Comparator.comparing(WorkshiftTime::getStartTime).reversed()).collect(Collectors.toList());
        LocalTime startTime = sort.get(0).getStartTime();
        LocalTime endTime = sort.get(sort.size()-1).getStartTime();
        if (startTime.isAfter(endTime)){
            return LocalDateTime.of(LocalDate.parse(workSetDate, DateTimeFormatter.ofPattern("yyyyMMdd")),endTime).plusDays(1);
        }
        return LocalDateTime.of(LocalDate.parse(workSetDate, DateTimeFormatter.ofPattern("yyyyMMdd")),endTime);
    }
}
