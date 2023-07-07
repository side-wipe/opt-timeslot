package com.example.timeslot.shift.solver;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

import com.example.timeslot.shift.config.ShiftConstraintPenalizeConfiguration;
import com.example.timeslot.shift.enums.ShiftTypeEnum;
import com.example.timeslot.shift.model.DayForecast;
import com.example.timeslot.shift.model.Shift;
import com.example.timeslot.shift.model.ShiftTimeslot;
import com.example.timeslot.shift.model.TimePeriodForecast;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.springframework.stereotype.Component;

/**
 * @author zhifeng
 */
@Component
public class ShiftConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
            // Hard constraints
            servicerConflict(constraintFactory),
            pickupRateAvgPenaltyConstraint(constraintFactory),
            atLeastHoursBetweenTwoShifts(constraintFactory),
            atMostHoursWorkingPerWeek(constraintFactory),
            atLeastHoursBetweenLastShift(constraintFactory),

            // Soft constraints
            pickupRatePenaltyConstraint(constraintFactory),
            //servicerSubsidyContraint(constraintFactory),
            //shiftSatisfactionContraint(constraintFactory),
        };
    }

    /**
     * 每个小二每天只能有一个班
     */
    public Constraint servicerConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
            .fromUniquePair(Shift.class,
                Joiners.equal(Shift::getServicer),
                Joiners.equal(Shift::getWorksetDate),
                Joiners.filtering((firstShift, secondShift) -> firstShift.getServicer() != null && secondShift.getServicer() != null))
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.SERVICER_CONFLICT);
    }

    /**
     * 两个班次之间的最小休息时间
     * @param constraintFactory
     * @return
     */
    public Constraint atLeastHoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUniquePair(Shift.class,
                Joiners.equal(Shift::getServicer),
                Joiners.lessThanOrEqual(s->s.getShiftInfo().getEndTime(s.getWorksetDate()), s->s.getShiftInfo().getStartTime(s.getWorksetDate())),
                Joiners.filtering((firstShift, secondShift) -> firstShift.getShiftInfo() != null && secondShift.getShiftInfo() != null))
            .filter((firstShift, secondShift) ->{
                if (ShiftTypeEnum.REST.equals(firstShift.getShiftInfo().getShiftType())
                    || ShiftTypeEnum.REST.equals(secondShift.getShiftInfo().getShiftType())) {
                    return false;
                }
                return Duration.between(firstShift.getShiftInfo().getEndTime(firstShift.getWorksetDate()), secondShift.getShiftInfo().getStartTime(
                    secondShift.getWorksetDate())).toHours() < 11;
            })
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.REST_HOURS_BETWEEN_SHIFTS);
    }

    /**
     * 距离小二最近一次上班时间的最小休息时间
     * @param constraintFactory
     * @return
     */
    public Constraint atLeastHoursBetweenLastShift(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shift.class)
                .filter(shift->{
                if (shift.getServicer() == null
                    || ShiftTypeEnum.REST.equals(shift.getShiftInfo().getShiftType())
                    || !DayOfWeek.MONDAY.equals(shift.getDayOfWeek())
                    || shift.getServicer().getLastShiftTime() == null) {
                    return false;
                }
                return Duration.between(shift.getServicer().getLastShiftTime(),shift.getShiftInfo().getStartTime(
                    shift.getWorksetDate())).toHours() < 11;
            })
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.REST_HOURS_BETWEEN_LAST_SHIFT);
    }

    /**
     * 一周最大工作时长
     * @param constraintFactory
     * @return
     */
   public  Constraint atMostHoursWorkingPerWeek(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shift.class)
            .groupBy(Shift::getServicer, ConstraintCollectors.sum(s -> s.getShiftInfo().getWorkHours()))
            .filter((servicer, totalHours) -> totalHours > 40 || totalHours < 32)
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.MAX_HOURS_A_WEEK);
    }

   public  Constraint datumHoursWorkingPerWeek(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shift.class)
            .groupBy(Shift::getServicer, ConstraintCollectors.sum(s -> s.getShiftInfo().getWorkHours()))
            .filter((servicer, totalHours) ->{
                if (totalHours > 40) {
                    return true;
                }
                return false;
            })
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.DATUM_HOURS_A_WEEK);
    }


    /**
     * 保障时段接起率
     * @param constraintFactory
     * @return
     */
    public Constraint pickupRatePenaltyConstraint(ConstraintFactory constraintFactory) {

        return constraintFactory.from(Shift.class)
            .filter(s -> s.getShiftInfo() != null)
            .filter(s -> !ShiftTypeEnum.REST.equals(s.getShiftInfo().getShiftType()))
            .join(ShiftTimeslot.class,
                Joiners.equal(s -> s.getShiftInfo().getWorkshiftId(), ShiftTimeslot::getWorkshiftId),
                Joiners.equal(Shift::getWorksetDate, ShiftTimeslot::getDate))
            .groupBy((shift, shiftTimeslot) -> shiftTimeslot.getDateHour(),
                ConstraintCollectors.sumBigDecimal((shift, shiftTimeslot) ->
                    shift.getServicer() == null ? new BigDecimal("0") : shift.getServicer().getCph()))
            .join(TimePeriodForecast.class,
                Joiners.equal((dateHour, cph) -> dateHour, TimePeriodForecast::getDateHour))
            .filter((dateHour, cph,timePeriodForecast) ->
            {
                Long touchs = timePeriodForecast == null ? 0L : timePeriodForecast.getTouchCnt();
                BigDecimal most = BigDecimal.valueOf(touchs).multiply(new BigDecimal("1.3"));
                BigDecimal least = BigDecimal.valueOf(touchs).multiply(new BigDecimal("0.9"));
                return cph.compareTo(least) < 0 || cph.compareTo(most) > 0;
            })
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.DATUM_PICKUP_RATE_OF_HOUR, (dateHour, cph,timePeriodForecast) ->
            {
                Long touchs = timePeriodForecast == null ? 0L : timePeriodForecast.getTouchCnt();
                BigDecimal most = BigDecimal.valueOf(touchs).multiply(new BigDecimal("1.3"));
                BigDecimal least = BigDecimal.valueOf(touchs).multiply(new BigDecimal("0.92"));
                return cph.compareTo(least) < 0 ?
                    cph.subtract(least).abs().intValue() : cph.subtract(most).abs().intValue();
            });

    }


    /**
     * 全天接起率
     * @param constraintFactory
     * @return
     */
    Constraint pickupRateAvgPenaltyConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shift.class)
            .filter(s -> s.getShiftInfo() != null)
            .filter(s -> !ShiftTypeEnum.REST.equals(s.getShiftInfo().getShiftType()))
            .join(ShiftTimeslot.class,
                Joiners.equal(s -> s.getShiftInfo().getWorkshiftId(), ShiftTimeslot::getWorkshiftId),
                Joiners.equal(Shift::getWorksetDate, ShiftTimeslot::getDate))
            .groupBy((shift, shiftTimeslot) -> shiftTimeslot.getDateHour(),
                ConstraintCollectors.sumBigDecimal((shift, shiftTimeslot) ->
                    shift.getServicer() == null ? new BigDecimal("0") : shift.getServicer().getCph()))
            .join(TimePeriodForecast.class,
                Joiners.equal((dateHour, cph) -> dateHour, TimePeriodForecast::getDateHour))
            .groupBy((dateHour, cph, timePeriodForecast) -> dateHour,
                (dateHour, cph, timePeriodForecast) -> {
                    Long touchs = timePeriodForecast == null ? 0L : timePeriodForecast.getTouchCnt();
                    return cph.compareTo(new BigDecimal(String.valueOf(touchs))) > 0 ?  new BigDecimal(String.valueOf(touchs)): cph ;
                })
            .groupBy((dateHour, pickupCnt) -> dateHour.substring(0,8),
                ConstraintCollectors.sumBigDecimal((dateHour, pickupCnt) -> pickupCnt))
            .join(DayForecast.class,
                Joiners.equal((date, pickupCnt) -> date, DayForecast::getDate))
            .filter((date, pickupCnt, dayForecast) ->
            {
                if (dayForecast.getTouchCnt() == null || dayForecast.getTouchCnt() == 0) {
                    return false;
                }
                BigDecimal touchs = new BigDecimal(String.valueOf(dayForecast.getTouchCnt())).multiply(new BigDecimal("0.92"));
                return pickupCnt.compareTo(touchs) < 0;
            })
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.AVG_PICKUP_RATE_OF_DAY, (date, pcikupCnt,dayForecast) ->
            {
                BigDecimal touchs = new BigDecimal(String.valueOf(dayForecast.getTouchCnt())).multiply(new BigDecimal("0.92"));
                return touchs.subtract(pcikupCnt).intValue();
            });
    }

    /**
     * 小二满意度
     * @param constraintFactory
     * @return
     */
    Constraint shiftSatisfactionContraint(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shift.class)
            .filter(s -> s.getShiftInfo().getScore() < 0 && s.getServicer()!=null)
            .penalizeConfigurable(
                ShiftConstraintPenalizeConfiguration.SATISFACTION, (shift) -> shift.getShiftInfo().getScore() * -1);

    }

    /**
     * 小二补贴金额均衡约束
     * @param constraintFactory
     * @return
     */
    Constraint servicerSubsidyContraint(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shift.class)
            .filter(s -> !ShiftTypeEnum.REST.equals(s.getShiftInfo().getShiftType()) && s.getServicer()!=null)
            .groupBy(Shift::getServicer,
                ConstraintCollectors.sum((shift) -> shift.getShiftInfo().getSubsidy()))
            .filter((servicer, subsidy) -> subsidy + servicer.getSubsidy() > 900)
            .penalizeConfigurable(ShiftConstraintPenalizeConfiguration.SUBSIDY, (servicer, subsidy) -> (subsidy + servicer.getSubsidy() - 900));

    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(2023, 6, 9, 0, 0, 0);
        LocalDateTime last = LocalDateTime.of(2023, 6, 8, 0, 0, 0);
        long l = Duration.between(start, last).toHours();
        System.out.println(l);
    }

}
