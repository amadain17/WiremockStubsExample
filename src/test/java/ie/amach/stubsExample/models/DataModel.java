package ie.amach.stubsExample.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Arrays;
import java.util.List;

import static java.util.UUID.randomUUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = DataModel.DataModelBuilder.class)
@XmlAccessorType(XmlAccessType.FIELD)
public class DataModel {

    @XmlAttribute
    @JsonProperty
    @Builder.Default
    String id = randomUUID().toString();

    @JsonProperty
    @Builder.Default
    String position = randomUUID().toString();

    @JsonProperty
    @Builder.Default
    List<NamesModel> names = Arrays.asList(NamesModel.builder().build(), NamesModel.builder().build());
}
