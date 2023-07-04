package com.example.timeslot.shift.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhifeng
 * @Description: 天预测数据
 * @Date: 2023/5/23 11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayForecast {

    private String date;

    private Long touchCnt;
}
