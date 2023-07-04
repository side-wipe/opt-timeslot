package com.example.timeslot.shift.config;

import java.util.Collections;
import java.util.UUID;

import com.example.timeslot.shift.model.Shift;
import com.example.timeslot.shift.model.ShiftTable;
import com.example.timeslot.shift.solver.ShiftConstraintProvider;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zhifeng
 * @Description:
 * @Date: 2023/5/29 16
 */
@Configuration
public class OptaPlannerConfig {

    @Bean
    public SolverFactory<ShiftTable> solverFactory() {

        SolverConfig solverConfig = new SolverConfig()
            .withSolutionClass(ShiftTable.class)
            .withConstraintProviderClass(ShiftConstraintProvider.class)
            .withEntityClasses(Shift.class);

        // 配置搜索算法
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setBestScoreFeasible(true);
        terminationConfig.setUnimprovedMinutesSpentLimit(20L);
        terminationConfig.setBestScoreLimit("0hard/*soft");
        terminationConfig.setMinutesSpentLimit(60L);
        solverConfig.setTerminationConfig(terminationConfig);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        // 指定搜索算法类型
        localSearchPhaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);
        // 指定搜索终止条件
        localSearchPhaseConfig.setTerminationConfig(terminationConfig);
        // 将搜索算法添加到搜索计划中
        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        return SolverFactory.create(solverConfig);
    }

    @Bean
    public SolverManager<ShiftTable, UUID> solverManager() {
        return SolverManager.create(solverFactory());
    }
}