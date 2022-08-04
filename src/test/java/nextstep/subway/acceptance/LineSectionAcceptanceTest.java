package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 새로운 역을 상행역, 기존의 상행 종점을 하행역으로 하는 구간을 지하철 노선에 추가 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 새로운_역을_상행_종점으로_등록할_경우_구간_추가_성공() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // then
        구간_추가_검증(정자역, 강남역, 양재역);
    }

    /**
     * When 기존의 하행 종점을 상행역, 새로운 역을 하행역으로 하는 구간을 지하철 노선에 추가 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 새로운_역을_하행_종점으로_등록할_경우_구간_추가_성공() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        구간_추가_검증(강남역, 양재역, 정자역);
    }

    /**
     * given 하행 종점이 아닌 기존 역을 상행역으로하는 구간에 대해
     * 해당 구간 상행역을 상행역, 새로운 지하철 역을 하행역, 해당 구간의 길이보다 짧은 길이로 하는 구간을
     * When 지하철 노선에 추가 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 하행_종점이_아닌_기존_역을_상행역으로_하는_구간_사이에_새로운_역을_등록할_경우_구간_추가_성공() {

    }

    /**
     * given 상행 종점이 아닌 기존 역을 히행역으로하는 구간에 대해
     * 새로운 지하철 역을 상행역, 해당 구간 하행역을 하행역, 해당 구간의 길이보다 짧은 길이로 하는 구간을
     * When 지하철 노선에 추가 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @Test
    void 상행_종점이_아닌_기존_역을_하행역으로_하는_구간_사이에_새로운_역을_등록할_경우_구간_추가_성공() {

    }

    /**
     * given 기존 구간 사이에 새로운 지하철 역을 생성하는 기존 구간의 길이를 초과하는 새로운 구간을
     * When 지하철 노선에 추가 요청하면
     * Then 구간 길이 초과 예외가 발생한다.
     */
    @Test
    void 구간_사이에_기존_구간_길이를_초과하는_새로운_역을_등록하는_경우_예외() {

    }

    /**
     * given 기존에 등록된 구간을
     * When 지하철 노선에 추가 요청하면
     * Then 이미 등록된 구간 예외가 발생한다.
     */
    @Test
    void 이미_등록된_구간_예외() {

    }

    /**
     * given 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간을
     * When 지하철 노선에 추가 요청하면
     * Then 노선에 포함되지 않은 구간 예외가 발생한다.
     */
    @Test
    void 노선에_포함되지_않은_구간_예외() {

    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }

    private void 구간_추가_검증(Long... stationId) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(stationId);
    }
}
