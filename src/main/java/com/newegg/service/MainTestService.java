package com.newegg.service;

import org.redisson.api.Node;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.newegg.util.RedissonUtil.*;

@Service
public class MainTestService {

    public void testsetString(String key, String value){
        set(key, value, 0);
    }

    public String testGetString(String key) {
        String s = get(key);
        return s;
    }

    public boolean testDelString(String key){
        boolean b = delete(key);
        return b;
    }
    public void testhset(String key, Map map){
        hset(key, map);
    }
    public int testbatchSet() {
        Map<String, String> map = new HashMap<>();
        map.put("aiden_test1", "txt_value1");
        map.put("aiden_test2", "txt_value2");
        map.put("aiden_test3", "txt_value3");
        map.put("aiden_test4", "txt_value4");
        map.put("aiden_test5", "txt_value5");
        map.put("aiden_test6", "txt_value6");
        int i = batchSet(map, 0);
        return i;
    }
    public List<String> testbatchGet() {
        List<String> list = new ArrayList<>();
        list.add("aiden_test1");
        list.add("aiden_test2");
        list.add("aiden_test3");
        list.add("aiden_test4");
        list.add("aiden_test5");
        list.add("aiden_test6");
        List<String> list1 = batchGet(list);
        return list1;
    }

    public Collection<Node> testgetAllNodes() {
        Collection<Node> nodes = getAllNodes();
        List list = new ArrayList();
        nodes.forEach(node -> {
            list.add(node.getAddr());
        });
        return list;
    }
    public int testbatchDel() {
        List<String> list = new ArrayList<>();
        list.add("aiden_test1");
        list.add("aiden_test2");
        list.add("aiden_test3");
        list.add("aiden_test4");
        list.add("aiden_test5");
        list.add("aiden_test6");
        int i = batchDel(list);
        return i;
    }
    public Collection<Node> testgetMasterNodes() {
        Collection<Node> nodes = getMasterNodes();
        List list = new ArrayList();
        nodes.forEach(node -> {
            list.add(node.getAddr());
        });
        return list;
    }
    public void testHset() {
        Map map = new HashMap();
        map.put("filed1", "value1");
        map.put("filed2", "value2");
        map.put("filed3", "value3");
        map.put("filed4", "value4");
        map.put("filed5", "value5");
        hset("xaecbd:testHash", map);
    }
    public Set<Map.Entry<Object, Object>> testHget() {

        Set<Map.Entry<Object, Object>> result = hget("xaecbd:testHash");
        return result;
    }
    public Set<Map.Entry<Object, Object>> testHget(String key) {

        Set<Map.Entry<Object, Object>> result = hget(key);
        return result;
    }
    public long testHdel(String key , List<String> list) {
        long hdel = hdel(key, list);
        return hdel;
    }
    public long testHdel() {
        List<String> list = new ArrayList<>();
        list.add("filed1");
        list.add("filed2");
        list.add("filed3");
        list.add("filed4");
        list.add("filed5");
        long hdel = hdel("xaecbd:testHash", list);
        return hdel;
    }
    public boolean testLpush() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        boolean lpush = lpush("xaecbd:testList", list);
        return lpush;
    }
    public List testLrange() {
        List lrange2 = lrange("xaecbd:testList", 0, -1);
        return lrange2;
    }
    public void testLrem() {
        lremove("xaecbd:testList", "a", 0);
        lremove("xaecbd:testList", "b", 0);
        lremove("xaecbd:testList", "c", 0);
    }

    public boolean testSadd() {
        List list = new ArrayList();
        list.add("d");
        list.add("e");
        list.add("f");
        boolean sadd = sadd("xaecbd:testSet", list);
        return sadd;
    }
    public Set testSmembers() {
        Set smembers = smembers("xaecbd:testSet");
        return smembers;
    }
    public boolean testSremove() {
        List list = new ArrayList();
        list.add("d");
        list.add("e");
        list.add("f");
        boolean b = sRemvoe("xaecbd:testSet", list);
        return b;
    }
    public int testZadd() {
        Map map = new HashMap();
        map.put("x", 8D);
        map.put("y", 7D);
        map.put("z", 6D);
        int zadd = zadd("xaecbd:testSortedSet", map);
        return zadd;
    }
    public Collection<Object> testZrange(){
        Collection<Object> objects = zRange("xaecbd:testSortedSet", 0, -1);
        return objects;
    }
    public boolean testzRemove(){
        List list = new ArrayList();
        list.add("x");
        list.add("y");
        list.add("z");
        boolean b = zRemove("xaecbd:testSortedSet", list);
        return b;
    }

    public long testsetTTl(String key, long ttl)throws Exception{
        long l = setTTL(key, ttl);
        return l;
    }

    public long testgetTTl(String key)throws Exception{
        long l = getTTL(key);
        return l;
    }
}
