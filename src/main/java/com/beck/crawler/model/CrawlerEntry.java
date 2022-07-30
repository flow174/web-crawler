package com.beck.crawler.model;

import java.util.Map.Entry;

public class CrawlerEntry<K, V> implements Entry<K, V> {

  final K key;

  V value;

  public CrawlerEntry(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public V setValue(V value) {
    if (value == null) {
      throw new NullPointerException();
    }

    V oldValue = this.value;
    this.value = value;
    return oldValue;
  }

  public String toString() {
    return key.toString() + ":" + value.toString();
  }
}
