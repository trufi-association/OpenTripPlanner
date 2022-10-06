package org.opentripplanner.graph_builder.module.osm;

import static org.opentripplanner.graph_builder.module.osm.WayPropertiesBuilder.withModes;
import static org.opentripplanner.routing.edgetype.StreetTraversalPermission.NONE;

import org.opentripplanner.graph_builder.module.osm.specifier.ExactMatchSpecifier;

/**
 * OSM way properties for the Houston, Texas, USA area.
 * <p>
 * The differences compared to the default property set are:
 * <p>
 * 1. In Houston we want to disallow usage of downtown pedestrian tunnel system.
 */

public class HoustonWayPropertySetSource implements WayPropertySetSource {

  @Override
  public void populateProperties(WayPropertySet props) {
    // Disallow any use of underground indoor pedestrian tunnels
    props.setProperties(
      // we use an exact match since the default specifier would match more than we want since the
      // many key/value pairs can lead to high scores
      new ExactMatchSpecifier("highway=footway;layer=-1;tunnel=yes;indoor=yes"),
      withModes(NONE)
    );
    // Read the rest from the default set
    new DefaultWayPropertySetSource().populateProperties(props);
  }
}
