package com.redis.wrapper;
//Create a new file, e.g., CacheWrapper.java

import java.io.Serializable;
import java.time.Instant;

//This object will be stored in Redis.
//It must implement Serializable.
public record CacheWrapper<T>(
 T data,
 Instant creationTime
) implements Serializable { }