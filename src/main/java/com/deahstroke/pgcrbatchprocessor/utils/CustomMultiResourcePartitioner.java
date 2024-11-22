package com.deahstroke.pgcrbatchprocessor.utils;

import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class CustomMultiResourcePartitioner implements Partitioner {

  private Resource[] resources;

  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
    Map<String, ExecutionContext> contexts = new HashMap<>(gridSize);
    int i = 0;
    for (Resource resource : resources) {
      if (!StringUtils.endsWithIgnoreCase(resource.getFilename(), ".del")) {
        ExecutionContext context = new ExecutionContext();
        Assert.state(resource.exists(), "Resource does not exist: " + resource);
        context.putString("filename", resource.getFilename());
        contexts.put(String.valueOf(i), context);
        i++;
      }
    }
    return contexts;
  }

  public void setResources(Resource[] resources) {
    this.resources = resources;
  }
}
