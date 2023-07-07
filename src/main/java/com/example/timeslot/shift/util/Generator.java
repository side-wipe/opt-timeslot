package com.example.timeslot.shift.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.serialize.JSONWriter;
import com.example.timeslot.shift.enums.ShiftTypeEnum;
import com.example.timeslot.shift.model.DayForecast;
import com.example.timeslot.shift.model.Servicer;
import com.example.timeslot.shift.model.Shift;
import com.example.timeslot.shift.model.ShiftInfo;
import com.example.timeslot.shift.model.ShiftTable;
import com.example.timeslot.shift.model.ShiftTimeslot;
import com.example.timeslot.shift.model.TimePeriodForecast;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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


    public static void output(Object object,String jsonFileName){
        // 创建一个 ObjectMapper 对象
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        try {
            // 创建一个 JsonGenerator 对象，用于将 JSON 写入文件
            JsonFactory jsonFactory = objectMapper.getFactory();
            JsonGenerator jsonGenerator = jsonFactory.createGenerator(new File(jsonFileName), JsonEncoding.UTF8);

            // 使用流式 API 将 BigObject 对象写入到文件中
            objectMapper.writeValue(jsonGenerator, object);

            // 关闭 JsonGenerator 对象
            jsonGenerator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ShiftTable shiftTable = Generator.generateData();
        Generator.output(shiftTable,"shiftTable111.json");
    }

}
