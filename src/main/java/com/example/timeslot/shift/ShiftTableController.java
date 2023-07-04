package com.example.timeslot.shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.example.timeslot.shift.enums.ShiftTypeEnum;
import com.example.timeslot.shift.model.Servicer;
import com.example.timeslot.shift.model.Shift;
import com.example.timeslot.shift.model.ShiftInfo;
import com.example.timeslot.shift.model.ShiftTable;
import com.example.timeslot.shift.util.Generator;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhifeng
 * @Description:
 * @Date: 2023/5/22 22
 */
@RestController
@RequestMapping("/shiftTable")
public class ShiftTableController {

    @Autowired
    private SolverManager<ShiftTable, UUID> solverManager;

    @PostMapping("/shift")
    public Map<Servicer, List<Shift>> solve() {
        ShiftTable timeTable = Generator.generateData();
        UUID problemId = UUID.randomUUID();
        // Submit the problem to start solving
        SolverJob<ShiftTable, UUID> solverJob = solverManager.solveAndListen(problemId,
            s -> Generator.generateData(), this::save);
        //ShiftTable solution;
        //long start = System.currentTimeMillis();
        //System.out.println("startTime:"+new Date(start));
        //try {
        //    // Wait until the solving ends
        //    solution = solverJob.getFinalBestSolution();
        //    long e = System.currentTimeMillis();
        //    System.out.println("endTime:"+new Date(e));
        //
        //} catch (InterruptedException | ExecutionException e) {
        //    throw new IllegalStateException("Solving failed.", e);
        //}

        //按servicer分组
        return null;

    }

    private void save(ShiftTable solution){
        LocalDate day = LocalDate.of(2023, 5, 29);
        List<LocalDate> days = Arrays.asList(day, day.plusDays(1), day.plusDays(2), day.plusDays(3),
            day.plusDays(4), day.plusDays(5), day.plusDays(6));
        List<String> dayStrs = days.stream().map(d -> d.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).collect(
            Collectors.toList());
        List<Shift> shifts = solution.getShifts();
        Map<Servicer, List<Shift>> collect = shifts.stream().filter(shift -> shift.getServicer() != null).collect(Collectors.groupingBy(Shift::getServicer));

        //遍历补齐
        for (Servicer servicer : collect.keySet()) {
            List<Shift> shiftList = collect.get(servicer);
            List<String> dates = shiftList.stream().map(Shift::getWorksetDate).collect(
                Collectors.toList());
            //取差集
            List<String> diffs = dayStrs.stream().filter(d -> !dates.contains(d)).collect(Collectors.toList());
            diffs.forEach(d -> {
                LocalDate local = LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyyMMdd"));
                Shift shift = new Shift();
                shift.setServicer(servicer);
                shift.setDayOfWeek(local.getDayOfWeek());
                shift.setWorksetDate(d);
                ShiftInfo shiftInfo = new ShiftInfo();
                shiftInfo.setShiftType(ShiftTypeEnum.REST);
                shiftInfo.setWorkHours(0);
                shiftInfo.setWorkshiftId(-1L);
                shiftInfo.setWorkshiftName("休息");
                shift.setShiftInfo(shiftInfo);
                shiftList.add(shift);
            });
            List<Shift> res = shiftList;
            res = res.stream().sorted(Comparator.comparing(Shift::getWorksetDate)).collect(Collectors.toList());
            collect.put(servicer,res);
        }

    }

}
