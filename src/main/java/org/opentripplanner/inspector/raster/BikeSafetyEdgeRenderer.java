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
  private final ScalarColorPalette customPalette = new DefaultScalarColorPalette(0.0, 100.0, 200.0);

  public BikeSafetyEdgeRenderer() {
  }

  public Color getSafetyColor(double bikeSafety, int bikeSafetyOpacity) {
    int red = (int) (2.55 * bikeSafety);
    return new Color(red, 255 - red, 0, bikeSafetyOpacity);
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
    if (!(e instanceof StreetEdge pse))
      return Optional.empty();

    if (!pse.getPermission().allows(TraverseMode.BICYCLE))
      return Optional.empty();

    double bikeSafety = pse.getBicycleSafetyFactor();
    int bikeSafetyOpacity = pse.getBicycleSafetyFactorOpacity();

    if (bikeSafetyOpacity == 0)
      return Optional.empty();

    return EdgeVisualAttributes.optional(
      getSafetyColor(bikeSafety, bikeSafetyOpacity),
      buildLabel(bikeSafety, bikeSafetyOpacity)
    );
  }

  @Override
  public Optional<VertexVisualAttributes> renderVertex(Vertex v) {
    return Optional.empty();
  }

  @Override
  public String getName() {
    return "Bike safety";
  }
}
