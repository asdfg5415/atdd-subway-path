package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getStationsInOrder() {

        List<Station> result = new ArrayList<>();

        Station firstUpStation = getFirstUpStation();
        result.add(firstUpStation);

        Optional<Station> connectedDownStation = getConnectedDownStation(firstUpStation);
        while (connectedDownStation.isPresent()) {
            Station downStation = connectedDownStation.get();
            result.add(downStation);
            connectedDownStation = getConnectedDownStation(downStation);
        }

        return result;
    }

    private Station getFirstUpStation() {
        Set<Station> allDownStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toUnmodifiableSet());

        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> !allDownStations.contains(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행 종점을 찾을 수 없습니다."));
    }

    private Optional<Station> getConnectedDownStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .map(Section::getDownStation)
                .findFirst();
    }

    private Section getLastSection() {
        Set<Station> allUpStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toUnmodifiableSet());

        return sections.stream()
                .filter(section -> !allUpStations.contains(section.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("하행 종점을 찾을 수 없습니다."));
    }

    public void removeSection(Station removeStation) {
        Section lastSection = getLastSection();
        if (isDifferentStation(lastSection.getDownStation(), removeStation)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastSection);
    }

    private boolean isDifferentStation(Station firstStation, Station secondStation) {
        return !firstStation.equals(secondStation);
    }
}
