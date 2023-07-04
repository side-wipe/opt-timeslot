package com.example.timeslot.shift.model;

import java.util.List;

import com.example.timeslot.shift.config.ShiftConstraintPenalizeConfiguration;
import com.example.timeslot.shift.enums.ShiftTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

/**
 * @Author: zhifeng
 * @Description: 排班表
 * @Date: 2023/5/20 14
 */

@PlanningSolution
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftTable {

    @PlanningEntityCollectionProperty
    private List<Shift> shifts;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "shiftInfoRange")
    private List<ShiftInfo> shiftInfos;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "servicerRange")
    private List<Servicer> servicers;

    @ProblemFactCollectionProperty
    private List<ShiftTimeslot> shiftTimeslots;

    @ProblemFactCollectionProperty
    private List<TimePeriodForecast> timePeriodForecasts;

    @ProblemFactCollectionProperty
    private List<DayForecast> dayForecasts;

    @ProblemFactCollectionProperty
    private List<ShiftTypeEnum> shiftTypeEnums;

    @PlanningScore
    private HardSoftScore score;

    @ConstraintConfigurationProvider
    private ShiftConstraintPenalizeConfiguration constraintConfiguration = new ShiftConstraintPenalizeConfiguration();


    public ShiftTable(List<Shift> shifts,List<Servicer> servicers){
        this.shifts = shifts;
        this.servicers = servicers;
    }

    public ShiftTable(List<Shift> shifts,List<ShiftInfo> shiftInfos,List<Servicer> servicers,List<ShiftTimeslot> shiftTimeslots,List<TimePeriodForecast> timePeriodForecasts, List<DayForecast> dayForecasts,List<ShiftTypeEnum> shiftTypeEnums){
        this.shifts = shifts;
        this.servicers = servicers;
        this.shiftInfos = shiftInfos;
        this.shiftTimeslots = shiftTimeslots;
        this.shiftTypeEnums = shiftTypeEnums;
        this.timePeriodForecasts = timePeriodForecasts;
        this.dayForecasts = dayForecasts;
    }

}
