package org.orangesoft.behave.generators.integrations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.orangesoft.behave.generators.TagsOverviewPage;
import org.orangesoft.behave.generators.integrations.helpers.DocumentAssertion;
import org.orangesoft.behave.generators.integrations.helpers.LeadAssertion;
import org.orangesoft.behave.generators.integrations.helpers.SummaryAssertion;
import org.orangesoft.behave.generators.integrations.helpers.TableRowAssertion;
import org.orangesoft.behave.generators.integrations.helpers.WebAssertion;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TagsOverviewPageIntegrationTest extends PageTest {

    @Test
    public void generatePage_generatesTitle() throws Exception {

        // given
        setUpWithJson(SAMPLE_JSON);
        page = new TagsOverviewPage(reportResult, configuration);
        final String titleValue = String.format("Behave Html Reports  - Tags Overview",
                configuration.getBuildNumber());

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        String title = document.getHead().getTitle();

        assertThat(title).isEqualTo(titleValue);
    }

    @Test
    public void generatePage_generatesLead() throws Exception {

        // given
        setUpWithJson(SAMPLE_JSON);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        LeadAssertion lead = document.getLead();

        assertThat(lead.getHeader()).isEqualTo("Tags Statistics");
        assertThat(lead.getDescription()).isEqualTo("The following graph shows passing and failing statistics for tags");
    }

    @Test
    public void generatePage_generatesCharts() throws Exception {

        // given
        setUpWithJson(SAMPLE_JSON);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());

        assertThat(document.byId("charts", WebAssertion.class)).isNotNull();
    }

    @Test
    public void generatePage_generatesStatsTableHeader() throws Exception {

        // given
        setUpWithJson(SAMPLE_JSON);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        TableRowAssertion[] headerRows = document.getSummary().getTableStats().getHeaderRows();

        assertThat(headerRows).hasSize(2);

        TableRowAssertion firstRow = headerRows[0];
        firstRow.hasExactValues("", "Scenarios", "Steps", "", "");

        TableRowAssertion secondRow = headerRows[1];
        secondRow.hasExactValues("Tag", "Total", "Passed", "Failed", "Total", "Passed", "Failed", "Skipped", "Pending",
                "Undefined", "Missing", "Duration", "Status");
    }

    @Test
    public void generatePage_generatesStatsTableBody() throws Exception {

        // given
        setUpWithJson(SAMPLE_JSON);
        configuration.setStatusFlags(true, false, false, true);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        TableRowAssertion[] bodyRows = document.getSummary().getTableStats().getBodyRows();

        assertThat(bodyRows).hasSize(2);

        TableRowAssertion firstRow = bodyRows[0];
        firstRow.hasExactValues("@checkout", "1", "1", "0", "7", "7", "0", "0", "0", "0", "0", "000ms", "Passed");
        firstRow.hasExactCSSClasses("tagname", "", "", "", "", "", "", "", "", "", "", "duration", "passed");
        firstRow.hasExactDataValues("", "", "", "", "", "", "", "", "", "", "", "0", "");
        firstRow.getReportLink().hasLabelAndAddress("@checkout", "checkout.html");

        TableRowAssertion secondRow = bodyRows[1];
        secondRow.hasExactValues("@slow", "1", "1", "0", "7", "7", "0", "0", "0", "0", "0", "000ms", "Passed");
        secondRow.hasExactCSSClasses("tagname", "", "", "", "", "", "", "", "", "", "", "duration", "passed");
        secondRow.hasExactDataValues("", "", "", "", "", "", "", "", "", "", "", "0", "");
        secondRow.getReportLink().hasLabelAndAddress("@slow", "slow.html");
    }

    @Test
    public void generatePage_generatesStatsTableFooter() throws Exception {

        // given
        setUpWithJson(SAMPLE_JSON);
        configuration.setStatusFlags(true, false, false, true);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        TableRowAssertion footerCells = document.getSummary().getTableStats().getFooterRow();
        footerCells.hasExactValues("2", "2", "2", "0", "14", "14", "0", "0", "0", "0", "0", "000ms", "Totals");
    }

    @Test
    public void generatePage_onEmptyJsons_generatesProperMessage() throws Exception {

        // given
        setUpWithJson(EMPTY_JSON);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        SummaryAssertion summary = document.getSummary();
        assertThat(summary.getEmptyReportMessage()).isEqualTo("You have no tags in your cucumber report");
    }
}
