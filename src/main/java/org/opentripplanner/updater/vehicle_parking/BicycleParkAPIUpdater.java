package org.opentripplanner.updater.vehicle_parking;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.opentripplanner.routing.vehicle_parking.VehicleParkingSpaces;
import java.util.Collection;

/**
 * Vehicle parking updater class that extends the {@link ParkAPIUpdater}. Meant for reading bicycle
 * parks from https://github.com/offenesdresden/ParkAPI format APIs.
 */
public class BicycleParkAPIUpdater extends ParkAPIUpdater {

    public BicycleParkAPIUpdater(
            String url,
            String feedId,
            Map<String, String> httpHeaders,
            Collection<String> staticTags
    ) {
        super(url, feedId, httpHeaders, staticTags);
    }

    @Override
    protected VehicleParkingSpaces parseCapacity(JsonNode jsonNode) {
        return parseVehicleSpaces(jsonNode, "total", null, null);
    }

    @Override
    protected VehicleParkingSpaces parseAvailability(JsonNode jsonNode) {
        return parseVehicleSpaces(jsonNode, "free", null, null);
    }
}
