package com.example.timeslot.shift.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhifeng
 * @Description: 智能排班请求
 * @Date: 2023/5/30 17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {

    private LocalDate startDate;

    private Integer days = 7;

    private String catagoryId;

    private String catagoryName;

    private String tripBizAname;

    private String tripBizName;

    private String channelType;

}
