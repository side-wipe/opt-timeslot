package com.example.timeslot.solver;

import com.example.timeslot.domain.Lesson;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

/**
 * @program: timeslot
 * @description: ${description}
 * @author: qing.ye
 * @create: 2021-06-01 10:29
 **/
public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                roomConflict(constraintFactory),
                teacherConflict(constraintFactory),
                studentGroupConflict(constraintFactory),
                // Soft constraints are only implemented in the "complete" implementation
        };
    }

    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        // 一个房间最多可以同时容纳一节课。

        // 选择一个课程...
        return constraintFactory.from(Lesson.class)
                // ......并与另一课程配对......
                .join(Lesson.class,
                        // ... 在同一时间段内 ...
                        Joiners.equal(Lesson::getTimeslot),
                        // ...在同一个房间里...
                        Joiners.equal(Lesson::getRoom),
                        // ...... 而且这一对是唯一的（不同的id，没有反向的对）。
                        Joiners.lessThan(Lesson::getId))
                //然后用一个硬权重来惩罚每一对。
                .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint teacherConflict(ConstraintFactory constraintFactory) {
        // 一个教师在同一时间最多可以教一门课。
        return constraintFactory.from(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getTeacher),
                        Joiners.lessThan(Lesson::getId))
                .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        // 一个学生在同一时间最多只能上一节课。
        return constraintFactory.from(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getStudentGroup),
                        Joiners.lessThan(Lesson::getId))
                .penalize("Student group conflict", HardSoftScore.ONE_HARD);
    }

}
