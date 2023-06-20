package org.opentripplanner.inspector.raster;

import java.awt.Color;
import java.util.Optional;
import org.opentripplanner.inspector.raster.EdgeVertexTileRenderer.EdgeVertexRenderer;
import org.opentripplanner.inspector.raster.EdgeVertexTileRenderer.EdgeVisualAttributes;
import org.opentripplanner.inspector.raster.EdgeVertexTileRenderer.VertexVisualAttributes;
import org.opentripplanner.service.vehiclerental.street.StreetVehicleRentalLink;
import org.opentripplanner.service.vehiclerental.street.VehicleRentalPlaceVertex;
import org.opentripplanner.street.model.edge.Edge;
import org.opentripplanner.street.model.edge.StreetEdge;
import org.opentripplanner.street.model.vertex.IntersectionVertex;
import org.opentripplanner.street.model.vertex.Vertex;
import org.opentripplanner.street.search.TraverseMode;

/**
 * Render bike safety for each edge using a color palette. Display the bike safety factor as label.
 *
 * @author laurent
 */
public class BikeSafetyEdgeRenderer implements EdgeVertexRenderer {

  private static final Color VEHICLE_RENTAL_COLOR_VERTEX = new Color(0.0f, 0.7f, 0.0f);
  private final ScalarColorPalette palette = new DefaultScalarColorPalette(1.0, 3.0, 10.0);

  public BikeSafetyEdgeRenderer() {
  }

  public Color getSafetyColor(double bikeSafety, int bikeSafetyOpacity) {
    Color bsc = palette.getColor(bikeSafety);
    int rgba = (bikeSafetyOpacity << 24) | (bsc.getRGB() & 0xFFFFFF);
    return new Color(rgba, true);
  }

  public String buildLabel(double bikeSafety, int bikeSafetyOpacity) {
    StringBuilder sb = new StringBuilder();
    sb
      .append(String.format("%.02f", bikeSafety))
      .append(" - ")
      .append(bikeSafetyOpacity);

    return sb.toString();
  }

  @Override
  public Optional<EdgeVisualAttributes> renderEdge(Edge e) {
    if (e instanceof StreetEdge pse) {
      if (pse.getPermission().allows(TraverseMode.BICYCLE)) {
        double bikeSafety = pse.getBicycleSafetyFactor();
        int bikeSafetyOpacity = pse.getBicycleSafetyFactorOpacity();
        return EdgeVisualAttributes.optional(
          getSafetyColor(bikeSafety, bikeSafetyOpacity),
          buildLabel(bikeSafety, bikeSafetyOpacity)
        );
      } else {
        return EdgeVisualAttributes.optional(Color.LIGHT_GRAY, "no bikes");
      }
    } else if (e instanceof StreetVehicleRentalLink) {
      return EdgeVisualAttributes.optional(palette.getColor(1.0f), "link");
    }
    return Optional.empty();
  }

  @Override
  public Optional<VertexVisualAttributes> renderVertex(Vertex v) {
    if (v instanceof VehicleRentalPlaceVertex) {
      return VertexVisualAttributes.optional(VEHICLE_RENTAL_COLOR_VERTEX, v.getDefaultName());
    } else if (v instanceof IntersectionVertex) {
      return VertexVisualAttributes.optional(Color.DARK_GRAY, null);
    }
    return Optional.empty();
  }

  @Override
  public String getName() {
    return "Bike safety";
  }
}
