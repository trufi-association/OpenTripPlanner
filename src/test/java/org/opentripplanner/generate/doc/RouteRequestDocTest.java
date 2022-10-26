package org.opentripplanner.generate.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.opentripplanner.framework.io.FileUtils.readFile;
import static org.opentripplanner.framework.io.FileUtils.writeFile;
import static org.opentripplanner.framework.text.MarkdownFormatter.HEADER_3;
import static org.opentripplanner.generate.doc.framework.TemplateUtil.replaceParametersDetails;
import static org.opentripplanner.generate.doc.framework.TemplateUtil.replaceParametersTable;
import static org.opentripplanner.standalone.config.framework.JsonSupport.jsonNodeFromResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.opentripplanner.generate.doc.framework.MarkDownDocWriter;
import org.opentripplanner.generate.doc.framework.ParameterDetailsList;
import org.opentripplanner.generate.doc.framework.ParameterSummaryTable;
import org.opentripplanner.generate.doc.framework.SkipNodes;
import org.opentripplanner.standalone.config.RouterConfig;
import org.opentripplanner.standalone.config.framework.json.NodeAdapter;

public class RouteRequestDocTest {

  private static final File FILE = new File("docs", "RouteRequest.md");
  private static final String BUILD_CONFIG_FILENAME = "standalone/config/router-config.json";
  private static final SkipNodes SKIP_NODES = SkipNodes.of(
    "vectorTileLayers",
    "/docs/sandbox/MapboxVectorTilesApi.md"
  );

  /**
   * NOTE! This test updates the {@code docs/Configuration.md} document based on the latest
   * version of the code. The following is auto generated:
   * <ul>
   *   <li>The configuration type table</li>
   *   <li>The list of OTP features</li>
   * </ul>
   */
  @Test
  public void updateBuildConfigurationDoc() {
    NodeAdapter node = readBuildConfig();

    // Read and close inout file (same as output file)
    String doc = readFile(FILE);

    doc = replaceParametersTable(doc, getParameterSummaryTable(node));
    doc = replaceParametersDetails(doc, getParameterDetailsTable(node));

    writeFile(FILE, doc);

    assertEquals(doc, readFile(FILE));
  }

  private NodeAdapter readBuildConfig() {
    var json = jsonNodeFromResource(BUILD_CONFIG_FILENAME);
    var conf = new RouterConfig(json, BUILD_CONFIG_FILENAME, false);
    return conf.asNodeAdapter().child("routingDefaults");
  }

  private String getParameterSummaryTable(NodeAdapter node) {
    return new ParameterSummaryTable(SKIP_NODES).createTable(node).toMarkdownTable();
  }

  private String getParameterDetailsTable(NodeAdapter node) {
    var stream = new ByteArrayOutputStream();
    var out = new MarkDownDocWriter(new PrintStream(stream));
    ParameterDetailsList.listParametersWithDetails(node, out, SKIP_NODES, HEADER_3);
    return stream.toString(StandardCharsets.UTF_8);
  }
}
