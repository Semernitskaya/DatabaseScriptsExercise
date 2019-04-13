package com.ol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
public class ExecutionPlanBuilderTest {

    final Logger logger = LoggerFactory.getLogger(ExecutionPlanBuilderTest.class);

    private ExecutionPlanBuilder organizer = new ExecutionPlanBuilder();

    @DataProvider
    public Object[][] getTestData() {
        return new Object[][]{
                {
                        newArrayList(
                                getVulnerabilityScript(1, 2, 3),
                                getVulnerabilityScript(3, 4)),
                        new Integer[][][]{{{4}, {2, 3}, {1}}}
                },
                {
                        newArrayList(
                                getVulnerabilityScript(1, 2, 3, 4, 5, 8, 9),
                                getVulnerabilityScript(2, 4, 7, 11, 12),
                                getVulnerabilityScript(3, 4, 7),
                                getVulnerabilityScript(7, 30),
                                getVulnerabilityScript(16, 21, 20),
                                getVulnerabilityScript(15, 9, 1)),
                        new Integer[][][]{
                                {{30}, {4, 12, 11, 7}, {9, 2, 3, 5, 8}, {1}, {15}},
                                {{21, 20}, {16}}
                        }
                },
                {
                        newArrayList(getVulnerabilityScript(1, 2)),
                        new Integer[][][]{{{2}, {1}}}
                },
                {
                        newArrayList(getVulnerabilityScript(1)),
                        new Integer[][][]{{{1}}}
                },
        };
    }

    @Test(dataProvider = "getTestData")
    public void testIsAnagram(List<VulnerabilityScript> scripts,
                              Integer[][][] expectedExecutionPlanBlocksList) {
        List<Integer> actualExecutionPlan = organizer.buildExecutionPlan(scripts);
        logger.info("Expected execution plan {}, actual execution plan {}",
                expectedExecutionPlanBlocksList,
                actualExecutionPlan);
        for (Integer[][] expectedExecutionPlanBlocks : expectedExecutionPlanBlocksList) {
            assertForExecutionPlanBlock(actualExecutionPlan, expectedExecutionPlanBlocks);
        }
    }

    private void assertForExecutionPlanBlock(List<Integer> actualExecutionPlan,
                                             Integer[][] expectedExecutionPlanBlocks) {
        if (expectedExecutionPlanBlocks.length == 1) {
            assertThat(actualExecutionPlan).containsExactlyInAnyOrder(expectedExecutionPlanBlocks[0]);
            return;
        }
        for (Integer[] expectedExecutionPlanBlock : expectedExecutionPlanBlocks) {
            replaceValuesWithPositions(actualExecutionPlan, expectedExecutionPlanBlock);
        }
        for (int i = 0; i < expectedExecutionPlanBlocks.length - 1; i++) {
            assertArrayIsBefore(expectedExecutionPlanBlocks[i], expectedExecutionPlanBlocks[i + 1]);
        }

    }

    private void assertArrayIsBefore(Integer[] array1, Integer[] array2) {
        for (Integer val1 : array1) {
            for (Integer val2 : array2) {
                assertThat(val1).isLessThan(val2);
            }
        }

    }

    private void replaceValuesWithPositions(List<Integer> actualExecutionPlan,
                                            Integer[] expectedExecutionPlanBlock) {
        for (int i = 0; i < expectedExecutionPlanBlock.length; i++) {
            expectedExecutionPlanBlock[i] = actualExecutionPlan.indexOf(expectedExecutionPlanBlock[i]);
        }

    }

    private VulnerabilityScript getVulnerabilityScript(Integer scriptId, Integer... dependenciesIds) {
        return new VulnerabilityScript(scriptId, newArrayList(dependenciesIds));
    }

}