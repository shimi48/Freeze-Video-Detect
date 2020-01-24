package com.amir.utils;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JUnit 5 Example")
public class TestVideoMap {

  @Test
  @DisplayName("Add and remove from VideoMap")
  void testAddToVideoMap() {
    VideoMap.getInstance().add("1");
    assertEquals("pending", VideoMap.getInstance().getStatus("1"));
    VideoMap.getInstance().remove("1");
    assertEquals("not-found", VideoMap.getInstance().getStatus("1"));
  }
}
