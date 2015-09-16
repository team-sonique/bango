package sonique.bango.domain.sorter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;

public final class JsonSortParameterParser implements Converter<String, Sorter> {
    private final ObjectMapper objectMapper;

    public JsonSortParameterParser(ObjectMapper jsonObjectMapper) {
        this.objectMapper = jsonObjectMapper;
    }

    @Override
    public Sorter convert(String source) {
        try {
            JsonNode jsonNode = objectMapper.readTree(source);
            return new Sorter(jsonNode.get("property").asText(), asDirection(jsonNode.get("direction").asText()));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private static Sorter.Direction asDirection(String direction) {
        return Sorter.Direction.from(direction);
    }
}