package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class LineTest {

    Line line;
    Station firstStation;
    Station secondStation;
    Station thirdStation;

    @BeforeEach
    void setUp() {
        line = new Line();
        firstStation = new Station("강남역");
        secondStation = new Station("역삼역");
        thirdStation = new Station("선릉역");
    }

    @Test
    void addSection() {
        line.addSection(firstStation, secondStation, 10);

        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void getStations() {
        line.addSection(firstStation, secondStation, 10);

        List<Station> stations = line.getStations();

        assertThat(stations).containsExactly(firstStation, secondStation);
    }

    @Test
    void removeSection() {
        line.addSection(firstStation, secondStation, 10);
        line.addSection(secondStation, thirdStation, 10);

        line.removeSection(thirdStation);

        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void removeSection_Fail() {
        line.addSection(firstStation, secondStation, 10);
        line.addSection(secondStation, thirdStation, 10);

        assertThatIllegalArgumentException().isThrownBy(
                () -> line.removeSection(secondStation)
        );
    }
}
