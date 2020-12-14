package ie.amach.stubsExample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ie.amach.stubsExample.models.DataModel;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import io.restassured.http.ContentType;

import java.util.Arrays;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.apache.http.HttpStatus.SC_OK;

@Slf4j
public class WiremockExampleAT {
    private RequestSpecification requestSpecification;
    private String getUrl = "/employees/%s/location/dublin";
    private String responseId;
    private int wiremockPort = 8089;
    private DataModel dataModel;
    private String uuidRegexMatcher = ".*[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(wiremockPort), false);

    @Before
    public void setUp() throws JsonProcessingException {
        dataModel = DataModel.builder().build();
        responseId = dataModel.getId();

        ObjectMapper mapper = new ObjectMapper();

        wireMockRule.stubFor(get(urlMatching("/employees/[a-zA-Z0-9]+/location/dublin"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody(mapper.writeValueAsString(dataModel))));

        wireMockRule.stubFor(post(urlMatching("/employees/[a-zA-Z0-9]+/location/dublin"))
                .withRequestBody(matchingJsonPath("$.id", matching(uuidRegexMatcher)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody(mapper.writeValueAsString(dataModel))));

        requestSpecification = new RequestSpecBuilder()
                .setRelaxedHTTPSValidation()
                .addFilters(Arrays.asList(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri("http://localhost:" + Integer.toString(wiremockPort))
                .build();
    }

    @Test
    public void verifyGetRequest() {
        JsonPath response = given()
                    .spec(requestSpecification)
                .when()
                    .get(String.format(getUrl, RandomStringUtils.random(8, true, true)))
                .then()
                    .statusCode(SC_OK)
                .extract().jsonPath();

        assertThat(response.getString("id")).isEqualTo(responseId);

    }

    @Test
    public void verifyPostRequest() {
        JsonPath response = given()
                    .spec(requestSpecification.body(dataModel))
                .when()
                    .post(String.format(getUrl, RandomStringUtils.random(8, true, true)))
                .then()
                    .assertThat()
                    .statusCode(SC_OK)
                .extract().jsonPath();

        assertThat(response.getString("id")).isEqualTo(responseId);
    }

    @Test
    public void verifyPostRequestPassthru() {
        given()
                    .spec(requestSpecification.body(DataModel.builder().id("notuuid").build()))
                .when()
                    .post(String.format(getUrl, RandomStringUtils.random(8, true, true)))
                .then()
                    .assertThat()
                    .statusCode(SC_NOT_FOUND);
    }
}
