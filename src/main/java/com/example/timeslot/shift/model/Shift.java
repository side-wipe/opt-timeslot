package com.example.timeslot.shift.model;


import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * @Author: zhifeng
 * @Description: 排班计划
 * @Date: 2023/5/20 14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity
public class Shift {

    @PlanningId
    private Long id;

    private String worksetDate;

    private DayOfWeek dayOfWeek;

    private Servicer servicer;

    @PlanningVariable(valueRangeProviderRefs = "shiftInfoRange")
    private ShiftInfo shiftInfo;

    @Override
    public boolean equals(Object other) {

        if(other == this) {
            return true;
        }
        if(!(other instanceof Shift)) {
            return false;
        }

        Shift o = (Shift)other;
        return o.id.equals(id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + id.hashCode();
        return result;
    }
}
