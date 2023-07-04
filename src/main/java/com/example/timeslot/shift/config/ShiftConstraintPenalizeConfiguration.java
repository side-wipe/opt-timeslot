package com.example.timeslot.shift.config;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@ConstraintConfiguration
public class ShiftConstraintPenalizeConfiguration {

    public static final String SERVICER_CONFLICT = "servicer_conflict";

    public static final String REST_HOURS_BETWEEN_SHIFTS = "rest_hours_between_shifts";

    public static final String REST_HOURS_BETWEEN_LAST_SHIFT = "rest_hours_between_last_shift";

    public static final String MAX_HOURS_A_WEEK = "max_hours_a_week";

    public static final String DATUM_HOURS_A_WEEK = "datum_hours_a_week";

    public static final String DATUM_PICKUP_RATE_OF_HOUR = "datum_pickup_rate_of_hour";

    public static final String AVG_PICKUP_RATE_OF_DAY = "avg_pickup_rate_of_day";

    public static final String MANPOWER_LOSS = "manpower_loss";

    public static final String SATISFACTION = "satisfaction";

    public static final String SUBSIDY = "subsidy";


    @ConstraintWeight(SERVICER_CONFLICT)
    HardSoftScore servicerConflict = HardSoftScore.ofHard(1000);

    @ConstraintWeight(REST_HOURS_BETWEEN_SHIFTS)
    HardSoftScore restHoursBetweenShifts = HardSoftScore.ofHard(1000);

    @ConstraintWeight(REST_HOURS_BETWEEN_LAST_SHIFT)
    HardSoftScore restHoursBetweenLastShift = HardSoftScore.ofHard(1000);

    @ConstraintWeight(MAX_HOURS_A_WEEK)
    HardSoftScore maxHoursAweek = HardSoftScore.ofHard(1000);

    @ConstraintWeight(DATUM_HOURS_A_WEEK)
    HardSoftScore datumHoursAweek = HardSoftScore.ofHard(100);

    @ConstraintWeight(AVG_PICKUP_RATE_OF_DAY)
    HardSoftScore avgPickupRateOfDay = HardSoftScore.ofHard(2);

    @ConstraintWeight(DATUM_PICKUP_RATE_OF_HOUR)
    HardSoftScore datumPickupRateOfHour = HardSoftScore.ofHard(1);

    @ConstraintWeight(MANPOWER_LOSS)
    HardSoftScore manpowerLoss = HardSoftScore.ofSoft(10);

    @ConstraintWeight(SATISFACTION)
    HardSoftScore satisfaction = HardSoftScore.ofSoft(1);

    @ConstraintWeight(SUBSIDY)
    HardSoftScore subsidy = HardSoftScore.ofSoft(5);

}
