package com.example.timeslot.shift.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import com.example.timeslot.shift.enums.ShiftTypeEnum;
import com.example.timeslot.shift.model.DayForecast;
import com.example.timeslot.shift.model.Servicer;
import com.example.timeslot.shift.model.Shift;
import com.example.timeslot.shift.model.ShiftInfo;
import com.example.timeslot.shift.model.ShiftTable;
import com.example.timeslot.shift.model.ShiftTimeslot;
import com.example.timeslot.shift.model.TimePeriodForecast;

/**
 * @Author: zhifeng
 * @Description:
 * @Date: 2023/5/23 14
 */
public class Generator {

    private static final String classPath = "/Users/xin/IdeaProjects/opt-timeslot/target/classes/";

    public static ShiftTable generateData(){
        try {
            String jsonDayForest = new String(Files.readAllBytes(Paths.get(classPath+"dayForest.json")));
            String jsonServicers = new String(Files.readAllBytes(Paths.get(classPath+"servicers.json")));
            String jsonShiftInfo = new String(Files.readAllBytes(Paths.get(classPath+"shiftInfo.json")));
            String jsonTimeForest = new String(Files.readAllBytes(Paths.get(classPath+"timePeridoForest.json")));
            String jsonTimeslots = new String(Files.readAllBytes(Paths.get(classPath+"timeslots.json")));
            List<DayForecast> dayForecast = JSONUtil.toList(JSONUtil.parseArray(jsonDayForest), DayForecast.class);
            List<Servicer> servicers = JSONUtil.toList(JSONUtil.parseArray(jsonServicers), Servicer.class);
            List<ShiftInfo> shiftInfos = JSONUtil.toList(JSONUtil.parseArray(jsonShiftInfo), ShiftInfo.class);
            List<TimePeriodForecast> timePeriodForecast = JSONUtil.toList(JSONUtil.parseArray(jsonTimeForest), TimePeriodForecast.class);
            List<ShiftTimeslot> timeslots = JSONUtil.toList(JSONUtil.parseArray(jsonTimeslots), ShiftTimeslot.class);
            List<ShiftTypeEnum> list = ShiftTypeEnum.getList();

            LocalDate day = LocalDate.of(2023, 5, 29);
            List<LocalDate> days = Arrays.asList(day, day.plusDays(1), day.plusDays(2), day.plusDays(3),
                day.plusDays(4), day.plusDays(5), day.plusDays(6));
            List<Shift> shifts = new ArrayList<>();

            Long k=0L;
            int l=0;
            for (int i=0;i<days.size();i++){
                LocalDate localDate = days.get(i);
                for (int j=0;j<servicers.size();j++){
                    Shift shift = new Shift();
                    shift.setId(k);
                    shift.setDayOfWeek(localDate.getDayOfWeek());
                    shift.setWorksetDate(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    shift.setServicer(servicers.get(j));
                    shift.setShiftInfo(shiftInfos.get(l));
                    shifts.add(shift);
                    k++;
                    l = l >= shiftInfos.size()-1 ? 0 : l+1;
                }
            }

            return new ShiftTable(shifts,shiftInfos, servicers, timeslots, timePeriodForecast, dayForecast, list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
