package com.yiziton.dataweb.core.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-05-09 11:45
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class LazyMap<K,V> implements Map<K,V> {

    private HashMap<K,V> hashMap;
    private Map<String,Callback> callbackMap;

    public LazyMap(Map<String,Callback> callbackMap) {
        this.hashMap = new HashMap<>();
        this.callbackMap = callbackMap;
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    @Override
    public V get(Object key) {
        if(hashMap.get(key)==null && callbackMap.containsKey(key) ){
            callbackMap.get(key).loadData(key.toString());
        }
        return hashMap.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    @Override
    public V put(K key, V value) {
        return hashMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        hashMap.putAll(m);
    }

    @Override
    public V remove(Object key) {
        return hashMap.remove(key);
    }

    @Override
    public void clear() {
        hashMap.clear();
    }

    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    @Override
    public Set<K> keySet() {
        return hashMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return hashMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return hashMap.entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return hashMap.getOrDefault(key, defaultValue);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return hashMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return hashMap.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return hashMap.replace(key, oldValue, newValue);
    }
    @Override
    public V replace(K key, V value) {
        return hashMap.replace(key, value);
    }
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return hashMap.computeIfAbsent(key, mappingFunction);
    }
    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return hashMap.computeIfPresent(key, remappingFunction);
    }
    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return hashMap.compute(key, remappingFunction);
    }
    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return hashMap.merge(key, value, remappingFunction);
    }
    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        hashMap.forEach(action);
    }
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        hashMap.replaceAll(function);
    }

}