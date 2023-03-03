package study.java2.practice.kafka.core.producer.event;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.java2.practice.kafka.core.producer.practice.EventHandlerMain;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutionException;

public class FileEventSource implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(FileEventSource.class);
  private boolean keepRunning;
  private long updateInterval;
  private File file;
  private long filePointer = 0;
  private EventHandler eventHandler;

  public FileEventSource(boolean keepRunning, long updateInterval, File file, EventHandler eventHandler) {
    this.keepRunning = keepRunning;
    this.updateInterval = updateInterval;
    this.file = file;
    this.eventHandler = eventHandler;
  }

  @Override
  public void run() {
    try {
      while (this.keepRunning) {
        Thread.sleep(this.updateInterval);
        // file size 계산
        long size = this.file.length();
        if (size < this.filePointer) {
          log.info("file was reset as filePointer is longer tah file length");
          filePointer = size;
        } else if (size > this.filePointer) {
          readAppendAndSend();
        } else {
          continue;
        }
      }
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private void readAppendAndSend() throws IOException, ExecutionException, InterruptedException {
    RandomAccessFile raf = new RandomAccessFile(this.file, "r");
    raf.seek(this.filePointer);
    String line = null;
    while ((line = raf.readLine()) != null) {
      sendMessage(line);
    }
    // file이 변경되었으므로 file의 filePointer를 현재 file의 마지막으로 재 설정함
    this.filePointer = raf.getFilePointer();
  }

  private void sendMessage(String line) throws ExecutionException, InterruptedException {
    String[] split = StringUtils.split(line, ",", 2);
    String key = split[0];
    String value = split[1];
    MessageEvent messageEvent = new MessageEvent(key, value);
    eventHandler.onMessage(messageEvent);
  }
}
