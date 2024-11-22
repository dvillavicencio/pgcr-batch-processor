package com.deahstroke.pgcrbatchprocessor.utils;

import com.deahstroke.pgcrbatchprocessor.exception.ObjectToByteException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectBytesUtils {

  private ObjectBytesUtils() {
  }

  public static byte[] objectToByteArray(Object obj) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);
      oos.flush();
      return baos.toByteArray();
    } catch (IOException e) {
      throw new ObjectToByteException("Unable to turn object into byte array", e);
    }
  }

}
