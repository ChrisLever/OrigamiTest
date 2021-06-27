package com.origami;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContentMapTest {

  private final static Integer MAP_SIZE = 20;
  private final Integer NUM_THREADS = 100;

  @Test
  public void shouldAddContent(){
    // Given
    ContentMap contentMap = new ContentMap(MAP_SIZE);
    // When
    contentMap.add("ID1", "SomeContent");
    // Then
    assertThat(contentMap.get("ID1"), equalTo("SomeContent"));
  }

  @Test
  public void shouldHoldMaxContent() {
    // Given
    ContentMap contentMap = new ContentMap(MAP_SIZE);
    // When
    for ( int cnt = 0; cnt < MAP_SIZE; cnt++) {
      contentMap.add("ID"+cnt, "SomeContent"+cnt);
    }
    // Then
    for ( int cnt = 0; cnt < MAP_SIZE; cnt++) {
      assertThat(contentMap.get("ID"+cnt), equalTo("SomeContent"+cnt));
    }
  }

  @Test
  public void shouldDiscardIfOverMaxContent() throws InterruptedException {
    // Given
    ContentMap contentMap = new ContentMap(MAP_SIZE);
    // When
    for ( int cnt = 0; cnt < MAP_SIZE + 1; cnt++) {
      Thread.sleep(10);
      contentMap.add("ID"+cnt, "SomeContent"+cnt);
    }
    // Then
    assertThat(contentMap.get("ID0"), equalTo(null));
    for ( int cnt = 1; cnt < MAP_SIZE + 1; cnt++) {
      assertThat(contentMap.get("ID"+cnt), equalTo("SomeContent"+cnt));
    }
  }

  @Test
  public void shouldAcceptMultipleThreads() throws InterruptedException, ExecutionException {
    // Given
    ContentMap contentMap = new ContentMap(MAP_SIZE);
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

    Callable<Integer> task = () -> {
      try {
        Integer numAdded = 0;
        for ( int cnt = 0; cnt < MAP_SIZE + 1; cnt++) {
          Thread.sleep((long)(100 * Math.random()));
          if (contentMap.add("ID"+(int)(Math.random() * 1000), "SomeContent"+cnt)){
            numAdded++;
          }
        }
        return numAdded;
      }
      catch (InterruptedException e) {
        throw new IllegalStateException("task interrupted", e);
      }
    };
    ArrayList<Future<Integer>> futures = new ArrayList<>();
    // When
    for ( int threadCnt = 0; threadCnt < NUM_THREADS; threadCnt++) {
          futures.add(executor.submit(task));
    }

    // Then
    for ( int threadCnt = 0; threadCnt < NUM_THREADS; threadCnt++) {
      futures.get(threadCnt).get();
    }
    assertThat("Map size",contentMap.getSize(), equalTo(MAP_SIZE));
  }

  @Test
  public void shouldDiscardLeastViewed() throws InterruptedException {
    // Given
    ContentMap contentMap = new ContentMap(MAP_SIZE);
    // When
    for ( int cnt = 0; cnt < MAP_SIZE + 1; cnt++) {
      Thread.sleep(10);
      contentMap.add("ID"+cnt, "SomeContent"+cnt);
      if ( cnt < 10){
        contentMap.get("ID"+cnt);
      }
    }
    // Then
    assertThat(contentMap.get("ID10"), equalTo(null));
    for ( int cnt = 1; cnt < MAP_SIZE + 1; cnt++) {
      if ( cnt != 10) {
        assertThat(contentMap.get("ID" + cnt), equalTo("SomeContent" + cnt));
      }
    }
  }
  }

