package com.origami;

public class MapData {

  private String content;
  private Long time;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  @Override
  public String toString() {
    return "MapData{" +
        "content='" + content + '\'' +
        ", time=" + time +
        '}';
  }
}
