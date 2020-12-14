package ie.amach.stubsExample.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@JsonDeserialize(builder = NamesModel.NamesModelBuilder.class)
public class NamesModel {

    @JsonProperty
    @Builder.Default
    String name = RandomStringUtils.random(8, true, false);

    @JsonProperty
    @Builder.Default
    List<String> positions = Arrays.asList("boss", "manager", "grunt");

    @JsonProperty
    @Builder.Default
    String city = "Dublin";
}

