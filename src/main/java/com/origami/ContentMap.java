package com.origami;

import java.util.Date;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ContentMap {

  private final ConcurrentHashMap<String, MapData> store;
  private final ConcurrentHashMap<String, Integer> tally;
  private final int mapSize;
  private final Logger logger = Logger.getLogger(ContentMap.class.getName());

  /**
   * @param maxSize The maximum size of the map. Entries will be purged if request to add to the map take it beyond this size
   */
  public ContentMap(int maxSize) {
    store = new ConcurrentHashMap<>(maxSize);
    tally = new ConcurrentHashMap<>();
    mapSize = maxSize;
  }

  /**
   * Add content to the map with a unique key
   *
   * @param key Unique key for content
   * @return true if key is unique,  false if key already exists
   */
  public synchronized boolean add(String key, String content) {
    if (store.get(key) != null) {
      logger.warning("Key " + key + " already in store");
      return false;
    }
    logger.info("Adding key " + key);
    ensureMapSize();
    MapData mapData = new MapData();
    mapData.setContent(content);
    mapData.setTime((new Date()).getTime());
    store.put(key, mapData);
    tally.put(key, 0);
    return true;
  }

  /**
   * get content identified by the key
   *
   * @param key for the the content
   * @return content or null if no content matches the key
   */
  public synchronized String get(String key) {
    MapData mapData = store.get(key);
    if (mapData == null) {
      return null; // do not keep a tally if not present
    }
    Integer cnt = tally.get(key);
    cnt++;
    tally.put(key, cnt);
    return mapData.getContent();
  }

  public Integer getSize() {
    return store.size();
  }

  private void ensureMapSize() {
    if (store.size() < mapSize) {
      return;
    }
    OptionalInt minCount = tally.values().stream().mapToInt(v -> v).min();
    String keyToEject = null;
    Long minTime = Long.MAX_VALUE;
    // if no content has been requested tally map will be empty
    if (minCount.isPresent()) {
      System.out.println("Min " + minCount.getAsInt());
      for (String key : tally.keySet()) {
        // find the oldest entry that has minCount requests
        if (tally.get(key) == minCount.getAsInt() && store.get(key).getTime() < minTime) {
          minTime = store.get(key).getTime();
          keyToEject = key;
        }
      }
    }

    if (keyToEject != null) {
      store.remove(keyToEject);
      tally.remove(keyToEject);
      logger.info("Ejecting key " + keyToEject);

    }
  }
}
