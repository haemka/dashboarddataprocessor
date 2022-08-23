/*
 * Copyright (C) 2021 University Hospital Bonn - All Rights Reserved You may use, distribute and
 * modify this code under the GPL 3 license. THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT
 * PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR
 * OTHER PARTIES PROVIDE THE PROGRAM “AS IS” WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH
 * YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR
 * OR CORRECTION. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL ANY
 * COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MODIFIES AND/OR CONVEYS THE PROGRAM AS PERMITTED ABOVE,
 * BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES
 * ARISING OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA
 * OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF THE
 * PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES. You should have received a copy of the GPL 3 license with *
 * this file. If not, visit http://www.gnu.de/documents/gpl-3.0.en.html
 */
package de.ukbonn.mwtek.dashboard.misc;

import de.ukbonn.mwtek.dashboard.interfaces.QuerySuffixBuilder;
import de.ukbonn.mwtek.dashboard.services.AbstractDataRetrievalService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Building the templates of the individual REST requests to the Acuwaveles server.
 */
public class FhirServerQuerySuffixBuilder implements QuerySuffixBuilder {

  private static final String COUNT_EQUALS = "&_count=";
  private static final String DELIMITER = ",";

  public String getObservations(AbstractDataRetrievalService dataRetrievalService, Integer month) {
    return "Observation?code=" + String.join(DELIMITER,
        dataRetrievalService.getLabPcrCodes()) + DELIMITER
        + String.join(DELIMITER,
        dataRetrievalService.getLabVariantCodes()) + "&_pretty=false" + COUNT_EQUALS
        + dataRetrievalService.getBatchSize();
  }

  public String getConditions(AbstractDataRetrievalService dataRetrievalService, Integer month) {
    return "Condition?code=" + String.join(DELIMITER,
        dataRetrievalService.getIcdCodes()) + "&_pretty=false" + COUNT_EQUALS
        + dataRetrievalService.getBatchSize();
  }

  public String getPatients(AbstractDataRetrievalService dataRetrievalService,
      List<String> patientIdList) {
    return "Patient?_id=" + String.join(DELIMITER,
        patientIdList) + COUNT_EQUALS + dataRetrievalService.getBatchSize();
  }

  @Override
  public String getEncounters(AbstractDataRetrievalService dataRetrievalService,
      List<String> patientIdList) {
    StringBuilder suffixBuilder = new StringBuilder();
    suffixBuilder.append("Encounter?subject=").append(String.join(DELIMITER, patientIdList));

    /* For this project, theoretically only cases with an intake date after a cut-off date
    (27/01/2020) are needed. To reduce the resource results and make the queries more
     streamlined, a "&location-period=gt2020-27-01" is added on demand to the fhir search, as
     we cannot assume that every location stores the transfer history in the Encounter
     resource.*/
    if (dataRetrievalService.getFilterEncounterByDate()) {
      suffixBuilder.append("&date=gt2020-01-27");
    }
    suffixBuilder.append(COUNT_EQUALS).append(dataRetrievalService.getMaxCountSize());

    return suffixBuilder.toString();
  }

  @Override
  public String getProcedures(AbstractDataRetrievalService dataRetrievalService,
      List<String> patientIdList) {
    return "Procedure?code=" + String.join(DELIMITER,
        dataRetrievalService.getProcedureVentilationCodes()) + DELIMITER
        + String.join(DELIMITER,
        dataRetrievalService.getProcedureEcmoCodes()) + "&subject="
        + StringUtils.join(
        patientIdList,
        ',') + COUNT_EQUALS + dataRetrievalService.getMaxCountSize();
  }

  @Override
  public String getLocations(AbstractDataRetrievalService dataRetrievalService,
      List<?> locationIdList) {
    return "Location?_id=" + StringUtils.join(locationIdList,
        ',') + COUNT_EQUALS + dataRetrievalService.getMaxCountSize();
  }

}
