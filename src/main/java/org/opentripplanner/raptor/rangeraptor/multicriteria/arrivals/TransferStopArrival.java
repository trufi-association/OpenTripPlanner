package org.opentripplanner.raptor.rangeraptor.multicriteria.arrivals;

import org.opentripplanner.raptor.api.model.RaptorTransfer;
import org.opentripplanner.raptor.api.model.RaptorTripSchedule;
import org.opentripplanner.raptor.api.model.TransitArrival;
import org.opentripplanner.raptor.api.view.TransferPathView;

/**
 * @param <T> The TripSchedule type defined by the user of the raptor API.
 */
public final class TransferStopArrival<T extends RaptorTripSchedule>
  extends AbstractStopArrival<T> {

  private final RaptorTransfer transfer;

  public TransferStopArrival(
    AbstractStopArrival<T> previousState,
    RaptorTransfer transferPath,
    int arrivalTime
  ) {
    super(
      previousState,
      1,
      transferPath.stop(),
      arrivalTime,
      previousState.cost() + transferPath.generalizedCost()
    );
    this.transfer = transferPath;
  }

  @Override
  public TransitArrival<T> mostRecentTransitArrival() {
    return previous().mostRecentTransitArrival();
  }

  @Override
  public boolean arrivedByTransfer() {
    return true;
  }

  @Override
  public TransferPathView transferPath() {
    return () -> transfer;
  }
}
