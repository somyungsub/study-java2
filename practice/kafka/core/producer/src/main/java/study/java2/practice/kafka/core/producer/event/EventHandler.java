package study.java2.practice.kafka.core.producer.event;

import java.util.concurrent.ExecutionException;

public interface EventHandler {
  void onMessage(MessageEvent messageEvent) throws InterruptedException, ExecutionException;
}
