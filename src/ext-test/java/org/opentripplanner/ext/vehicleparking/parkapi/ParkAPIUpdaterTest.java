package org.opentripplanner.ext.vehicleparking.parkapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opentripplanner._support.time.ZoneIds;
import org.opentripplanner.model.calendar.openinghours.OpeningHoursCalendarService;
import org.opentripplanner.model.calendar.openinghours.OsmOpeningHoursSupport;
import org.opentripplanner.transit.model.framework.Deduplicator;

public class ParkAPIUpdaterTest {

  @Test
  void parseCars() {
    var url = "file:src/ext-test/resources/vehicleparking/parkapi/parkapi-reutlingen.json";
    var timeZone = ZoneIds.BERLIN;
    var parameters = new ParkAPIUpdaterParameters(
      "",
      url,
      "park-api",
      30,
      null,
      List.of(),
      null,
      timeZone
    );
    var openingHoursCalendarService = new OpeningHoursCalendarService(
      new Deduplicator(),
      LocalDate.of(2022, Month.JANUARY, 1),
      LocalDate.of(2023, Month.JANUARY, 1)
    );
    var updater = new CarParkAPIUpdater(parameters, openingHoursCalendarService);

    assertTrue(updater.update());
    var parkingLots = updater.getUpdates();

    assertEquals(30, parkingLots.size());

    var first = parkingLots.get(0);
    assertEquals("Parkplatz Alenberghalle", first.getName().toString());
    assertEquals(
      "OHCalendar{zoneId: Europe/Berlin, openingHours: [Mo-Su 0:00-23:59:59]}",
      first.getOpeningHours().toString()
    );
    assertEquals("Mo-Su 0:00-23:59", OsmOpeningHoursSupport.osmFormat(first.getOpeningHours()));
    assertTrue(first.hasAnyCarPlaces());
    assertNull(first.getCapacity());

    var last = parkingLots.get(29);
    assertEquals("Zehntscheuer Kegelgraben", last.getName().toString());
    assertNull(last.getOpeningHours());
    assertTrue(last.hasAnyCarPlaces());
    assertTrue(last.hasWheelchairAccessibleCarPlaces());
    assertEquals(1, last.getCapacity().getWheelchairAccessibleCarSpaces());
  }

  @Test
  void parseCarsWithoutTimeZone() {
    var url = "file:src/ext-test/resources/vehicleparking/parkapi/parkapi-reutlingen.json";
    ZoneId timeZone = null;
    var parameters = new ParkAPIUpdaterParameters(
      "",
      url,
      "park-api",
      30,
      null,
      List.of(),
      null,
      timeZone
    );
    var openingHoursCalendarService = new OpeningHoursCalendarService(
      new Deduplicator(),
      LocalDate.of(2022, Month.JANUARY, 1),
      LocalDate.of(2023, Month.JANUARY, 1)
    );
    var updater = new CarParkAPIUpdater(parameters, openingHoursCalendarService);

    assertTrue(updater.update());
    var parkingLots = updater.getUpdates();

    assertEquals(30, parkingLots.size());

    var first = parkingLots.get(0);
    assertEquals("Parkplatz Alenberghalle", first.getName().toString());
    assertNull(first.getOpeningHours());
    assertTrue(first.hasAnyCarPlaces());
    assertNull(first.getCapacity());
  }

  @Test
  void parseHerrenbergOpeningHours() {
    var url = "file:src/ext-test/resources/vehicleparking/parkapi/herrenberg.json";
    var parameters = new ParkAPIUpdaterParameters(
      "",
      url,
      "park-api",
      30,
      null,
      List.of(),
      null,
      ZoneIds.BERLIN
    );
    var openingHoursCalendarService = new OpeningHoursCalendarService(
      new Deduplicator(),
      LocalDate.of(2022, Month.JANUARY, 1),
      LocalDate.of(2023, Month.JANUARY, 1)
    );
    var updater = new CarParkAPIUpdater(parameters, openingHoursCalendarService);

    assertTrue(updater.update());

    var openingHours = updater
      .getUpdates()
      .stream()
      .filter(p -> p.getOpeningHours() != null)
      .map(p -> OsmOpeningHoursSupport.osmFormat(p.getOpeningHours()))
      .toList();

    assertEquals(4, openingHours.size());

    assertEquals("Mo-Su 8:00-17:00", openingHours.get(0));
    assertEquals("Mo-Su 0:00-23:59", openingHours.get(1));
    assertEquals(
      "Tu 0:00-2:00; Sa 0:00-2:00; Mo 0:00-23:59; We 0:00-23:59; Th 0:00-23:59; Fr 0:00-23:59; Su 0:00-23:59; Tu 14:30-23:59; Sa 14:30-23:59",
      openingHours.get(2)
    );
    assertEquals("Sa 7:30-18:00; Mo-Fr 7:30-22:00", openingHours.get(3));
  }
}
