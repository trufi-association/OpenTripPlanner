package org.opentripplanner.raptor._data.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.opentripplanner.framework.time.DurationUtils.durationInSeconds;
import static org.opentripplanner.framework.time.TimeUtils.time;
import static org.opentripplanner.model.transfer.TransferConstraint.REGULAR_TRANSFER;
import static org.opentripplanner.raptor._data.stoparrival.BasicPathTestCase.COST_CALCULATOR;
import static org.opentripplanner.raptor._data.transit.TestAccessEgress.walk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentripplanner.raptor._data.RaptorTestConstants;
import org.opentripplanner.raptor._data.stoparrival.BasicPathTestCase;

/**
 * Test the PathBuilder to be sure that it works properly before using it in other tests.
 */
public class TestPathBuilderTest implements RaptorTestConstants {

  private final TestPathBuilder subject = new TestPathBuilder(COST_CALCULATOR);

  @Test
  public void testSimplePathWithOneTransit() {
    int transitDuration = durationInSeconds("5m");

    var path = subject
      .access(time("10:00:15"), STOP_A, D1m)
      .bus("L1", time("10:02"), transitDuration, STOP_B)
      .egress(D2m);

    var transitLeg = path.accessLeg().nextLeg().asTransitLeg();
    int boardCost = COST_CALCULATOR.boardingCost(
      true,
      path.accessLeg().toTime(),
      STOP_A,
      transitLeg.fromTime(),
      transitLeg.trip(),
      REGULAR_TRANSFER
    );

    int transitCost = COST_CALCULATOR.transitArrivalCost(
      boardCost,
      ALIGHT_SLACK,
      transitDuration,
      BasicPathTestCase.TRIP_1,
      STOP_B
    );

    int accessEgressCost = COST_CALCULATOR.costEgress(walk(STOP_B, D2m + D1m));

    assertEquals(accessEgressCost + transitCost, path.generalizedCost());
    assertEquals(
      "Walk 1m 10:00:15 10:01:15 $120 ~ A 45s " +
      "~ BUS L1 10:02 10:07 5m $438 ~ B 15s " +
      "~ Walk 2m 10:07:15 10:09:15 $210 " +
      "[10:00:15 10:09:15 9m 0tx $768]",
      path.toStringDetailed(this::stopIndexToName)
    );
  }

  @Test
  public void testBasicPath() {
    var path = subject
      .access(BasicPathTestCase.ACCESS_START, STOP_A, BasicPathTestCase.ACCESS_DURATION)
      .bus(
        BasicPathTestCase.LINE_11,
        BasicPathTestCase.L11_START,
        BasicPathTestCase.L11_DURATION,
        STOP_B
      )
      .walk(BasicPathTestCase.TX_DURATION, STOP_C, BasicPathTestCase.TX_COST)
      .bus(
        BasicPathTestCase.LINE_21,
        BasicPathTestCase.L21_START,
        BasicPathTestCase.L21_DURATION,
        STOP_D
      )
      .bus(
        BasicPathTestCase.LINE_31,
        BasicPathTestCase.L31_START,
        BasicPathTestCase.L31_DURATION,
        STOP_E
      )
      .egress(BasicPathTestCase.EGRESS_DURATION);
    Assertions.assertEquals(
      BasicPathTestCase.BASIC_PATH_AS_STRING,
      path.toString(this::stopIndexToName)
    );
    Assertions.assertEquals(
      BasicPathTestCase.BASIC_PATH_AS_DETAILED_STRING,
      path.toStringDetailed(this::stopIndexToName)
    );
    Assertions.assertEquals(BasicPathTestCase.TOTAL_COST, path.generalizedCost());
  }
}
