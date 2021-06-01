//package com.example.timeslot.solver;
//
//import com.example.timeslot.domain.Lesson;
//import com.example.timeslot.domain.TimeTable;
//import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
//import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
//
//import java.util.List;
//
///**
// * @program: timeslot
// * @description: ${description}
// * @author: qing.ye
// * @create: 2021-06-01 10:28
// **/
//public class TimeTableEasyScoreCalculator implements EasyScoreCalculator<TimeTable, HardSoftScore> {
//
//    @Override
//    public HardSoftScore calculateScore(TimeTable timeTable) {
//        List<Lesson> lessonList = timeTable.getLessonList();
//        int hardScore = 0;
//        for (Lesson a : lessonList) {
//            for (Lesson b : lessonList) {
//                if (a.getTimeslot() != null && a.getTimeslot().equals(b.getTimeslot())
//                        && a.getId() < b.getId()) {
//                    // A room can accommodate at most one lesson at the same time.
//                    if (a.getRoom() != null && a.getRoom().equals(b.getRoom())) {
//                        hardScore--;
//                    }
//                    // A teacher can teach at most one lesson at the same time.
//                    if (a.getTeacher().equals(b.getTeacher())) {
//                        hardScore--;
//                    }
//                    // A student can attend at most one lesson at the same time.
//                    if (a.getStudentGroup().equals(b.getStudentGroup())) {
//                        hardScore--;
//                    }
//                }
//            }
//        }
//        int softScore = 0;
//        // Soft constraints are only implemented in the "complete" implementation
//        return HardSoftScore.of(hardScore, softScore);
//    }
//
//}
