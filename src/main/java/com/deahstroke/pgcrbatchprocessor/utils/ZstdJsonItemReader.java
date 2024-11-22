package com.deahstroke.pgcrbatchprocessor.utils;

import com.deahstroke.pgcrbatchprocessor.dto.PostGameCarnageReport;
import com.deahstroke.pgcrbatchprocessor.exception.FileProcessingException;
import com.github.luben.zstd.ZstdInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

@Slf4j
public class ZstdJsonItemReader extends FlatFileItemReader<PostGameCarnageReport> {

  @Override
  public void setResource(Resource resource) {
    try {
      InputStream decompressedStream = new ZstdInputStream(resource.getInputStream());
      super.setResource(new InputStreamResource(decompressedStream));
    } catch (IOException e) {
      throw new FileProcessingException("Something went wrong when reading the .zstd file", e);
    }
  }

}
