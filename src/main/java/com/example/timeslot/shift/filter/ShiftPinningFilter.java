package com.example.timeslot.shift.filter;

import com.example.timeslot.shift.model.Shift;
import com.example.timeslot.shift.model.ShiftTable;
import org.optaplanner.core.api.domain.entity.PinningFilter;

public class ShiftPinningFilter implements PinningFilter<ShiftTable, Shift> {

    @Override
    public boolean accept(ShiftTable employeeSchedule, Shift shift) {

        return true;
    }
}