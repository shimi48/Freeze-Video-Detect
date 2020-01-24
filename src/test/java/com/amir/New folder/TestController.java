package com.amir.controller;

import org.junit.jupiter.api.*;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JUnit 5 Example")
public class TestController {

  @Test
  @DisplayName("test status")
  void testStatus() {
    MyController controller = new MyController(new ThreadPoolTaskExecutor());
    assertEquals("not-found", controller.status("3").getStatus());
  }
}
