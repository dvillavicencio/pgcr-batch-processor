package com.deahstroke.pgcrbatchprocessor.deserializers;

import com.deahstroke.pgcrbatchprocessor.dto.StatsEntry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class WrapperOrBasicDeserializer extends JsonDeserializer<Object> {

  @Override
  public Object deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    if (node.isDouble()) {
      return node.asDouble();
    } else if (node.canConvertToInt()) {
      return node.asInt();
    } else if (node.canConvertToLong()) {
      return node.asLong();
    } else if (node.isObject()) {
      return p.getCodec().treeToValue(node, StatsEntry.class);
    } else if (node.isBoolean()) {
      return node.asBoolean();
    } else {
      throw new JsonProcessingException("Could not decode node with value [%s]".formatted(node)) {
      };
    }
  }
}
