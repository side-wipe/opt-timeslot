package com.example.timeslot.shift.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhifeng
 * @Description:
 * @Date: 2023/5/25 13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftTimeslot {

    /**
     * 班次id
     */
    private Long workshiftId;

    /**
     * 班次日期 yyyyMMdd
     */
    private String date;

    /**
     * 时段 yyyyMMddHH
     */
    private String dateHour;


}
