package com.hex.common.cache;

import java.util.Set;

/**
 * @author guohs
 * @date 2021/9/15
 */
public interface IExpireCache<K, V> {

    V put(K key, V value);

    V get(K key);

    Set<K> keySet();

    void invalidate(K key);

}
