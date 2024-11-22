package com.deahstroke.pgcrbatchprocessor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

/**
 * Custom-made buffered reader factory that only reads until the file input stream reaches 15MB. If
 * the PGCR read in the line is greater than this amount, then we know the PGCR itself is of a
 * checkpoint bot instead of a legitimate raid run, therefore we should ignore it.
 */
public class CustomBufferedReaderFactory implements BufferedReaderFactory {

  private static final Integer BUFFER_SIZE = 32 * 1024 * 1024;

  @Override
  public BufferedReader create(Resource resource, String encoding) throws IOException {
    return new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding),
        BUFFER_SIZE);
  }

}
