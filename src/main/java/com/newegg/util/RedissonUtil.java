package com.newegg.util;

import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedissonUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedissonUtil.class);
    public static RedissonClient redissonClient;
    private static int static_retries = 3;
    static {
        try {
            File file2 = new ClassPathResource("redis.yaml").getFile();
            Config config = Config.fromYAML(file2);
            redissonClient = Redisson.create(config);
        } catch (IOException e) {
            logger.error("redissonConfig build failed");
        }
    }

    /**
     * 不设置过期时间，extimes设置为0
     * @param key
     * @param value
     * @param extimes
     */
    public static void set(String key,String value,int extimes){
        set(key,value,extimes,static_retries);
    }

    /**
     * set失败默认重试三次
     * @param key
     * @param value
     * @param extimes
     * @param retries
     */
    public static void set(String key,String value,int extimes,int retries){
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (retries == 0) {
            logger.error("Redis set operation failure. [key=" + key + " value=" + value + "]");
            return ;
        }
        try {
            if (extimes>0){
                bucket.set(value,extimes, TimeUnit.MINUTES);
            }else {
                bucket.set(value);
            }
        }catch (Exception e){
            set(key,value,extimes,--retries);
        }
    }

    public static void set(String key,byte[] value,int extimes){
        set(key,value,extimes,static_retries);
    }

    public static void set(String key,byte[] value,int extimes,int retries){
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (retries == 0) {
            logger.error("Redis set operation failure. key=" + key );
            return ;
        }
        try {
            if (extimes>0){
                bucket.set(value,extimes, TimeUnit.MINUTES);
            }else {
                bucket.set(value);
            }
        }catch (Exception e){
            set(key,value,extimes,--retries);
        }
    }

    public static boolean pingAll(){
        NodesGroup<Node> nodesGroup = redissonClient.getNodesGroup();
        boolean result = nodesGroup.pingAll();
        return result;
    }

    public static String get(String key){
       return get(key, static_retries);
    }

    public static String get(String key,int retries){
        if (retries == 0) {
            logger.error("Redis set operation failure. key=" + key );
            return null;
        }
        try{
            RBucket<Object> bucket = redissonClient.getBucket(key);
            String result = (String) bucket.get();
            return result;
        } catch (Exception ex){
            return get(key, --retries);
        }

    }
    public static byte[] getBytes(String key) {
        return getBytes(key, static_retries);
    }

    public static byte[] getBytes(String key ,int retries) {
        if (retries == 0) {
            logger.error("Redis set operation failure. key=" + key );
            return null;
        }
        try{
            RBucket<Object> bucket = redissonClient.getBucket(key);
            byte[] value = (byte[]) bucket.get();
            return value;
        }catch (Exception ex){
            return getBytes(key, --retries);
        }
    }

    public static void expire(String key,long extimes){
        expire(key,extimes,static_retries);
    }
    public static void expire(String key,long extimes,int retries){
        if (retries == 0) {
            logger.error("Redis set expire failure. key=" + key );
            return ;
        }
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            bucket.expire(extimes, TimeUnit.MINUTES);
        }catch (Exception EX){
            expire(key,extimes,--retries);
        }
    }
    public static boolean delete(String key){
        return delete(key,static_retries);
    }
    public static boolean delete(String key,int retries){
        if (retries == 0) {
            logger.error("Redis del failure. key=" + key );
            return false;
        }
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            boolean delete = bucket.delete();
            return delete;
        }catch (Exception ex){
            return delete(key, --retries);
        }
    }
    public static int removeAll(){
        RBatch batch = redissonClient.createBatch(BatchOptions.defaults());
        RKeys keys = redissonClient.getKeys();
        Iterator<String> iterator = keys.getKeys().iterator();
        for (; iterator.hasNext();){
            String key = iterator.next();
            System.out.println(key);
            batch.getKeys().deleteAsync(key);
            iterator.remove();
        }
        BatchResult<?> execute = batch.execute();
        int size = execute.getResponses().size();
        return size;
    }

    public static  int batchDel(List<String> keys){
        RBatch batch = redissonClient.createBatch(BatchOptions.defaults());
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            batch.getKeys().deleteAsync(iterator.next());
        }
        BatchResult<?> execute = batch.execute();
        int size = execute.getResponses().size();
        return size;
    }
    public static int batchSet(Map<String, String> values){
        RBatch batch = redissonClient.createBatch(BatchOptions.defaults());
        values.forEach((k,v)->{
            batch.getBucket(k).setAsync(v);
        });
        BatchResult<?> execute = batch.execute();
        int size = execute.getResponses().size();
        return size;
    }

    /**
     * 批量写入数据()
     * @param values
     * @param extimes
     * @return
     */
    public static int batchSet(Map<String, String> values , long extimes){
        RBatch batch = redissonClient.createBatch(BatchOptions.defaults());
        if (extimes == 0){
            values.forEach((k,v)->{
                batch.getBucket(k).setAsync(v);
            });
        }else {
            values.forEach((k,v)->{
                batch.getBucket(k).setAsync(v,extimes,TimeUnit.MINUTES);
            });
        }
        BatchResult<?> execute = batch.execute();
        int size = execute.getResponses().size();
        return size;
    }

    /**
     * 批量获取
     * @param keys
     * @return
     */
    public static List<String> batchGet(List<String> keys) {
        RBatch batch = redissonClient.createBatch(BatchOptions.defaults());
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            batch.getBucket(iterator.next()).getAsync();
        }
        BatchResult execute = batch.execute();
        List<String> responses = execute.getResponses();
        execute.getSyncedSlaves();
        return responses;
    }

    /**
     * 获取所有节点
     * @return
     */
    public static Collection<Node> getAllNodes(){
        NodesGroup<Node> nodesGroup = redissonClient.getNodesGroup();
        Collection<Node> nodes = nodesGroup.getNodes();
        return nodes;
    }

    /**
     * 获得master节点
     * @return
     */
    public static Collection<Node> getMasterNodes(){
        NodesGroup<Node> nodesGroup = redissonClient.getNodesGroup();
        Collection<Node> nodes = nodesGroup.getNodes(NodeType.MASTER);
        return nodes;
    }
    public static boolean setNX(String key,int ttl, TimeUnit timeUnit){
        RBucket<Object> bucket = redissonClient.getBucket(key);
        boolean result = bucket.trySet("code", ttl, timeUnit);
        return result;
    }

    public static void hset(String key,Map hash){
        RMap<Object, Object> map = redissonClient.getMap(key);
        map.putAll(hash);
    }
    public static Object hget(String key, String field ){
        RMap<Object, Object> map = redissonClient.getMap(key);
        Object o = map.get(field);
        return o;
    }
    public static Set<Map.Entry<Object, Object>> hget(String key){
        RMap<Object, Object> map = redissonClient.getMap(key);
        Set<Map.Entry<Object, Object>> entries = map.readAllEntrySet();
        return entries;
    }

    public static long hdel(String key,List<String> fields){
        RMap<Object, Object> map = redissonClient.getMap(key);
        long l = map.fastRemove( fields.toArray(new String[] {}));
        return l;
    }


    /**
     *将多个值 按从左到右的顺序依次插入到表头
     * @param key
     * @param values
     * @return
     */
    public static boolean lpush(String key, List<String> values){
        RDeque<Object> deque = redissonClient.getDeque(key);
        boolean b = deque.addAll(values);
        return b;
    }

    /**
     * 将值 value 插入到列表 key 的表头
     * @param key
     * @param value
     */
    public static void lpush(String key, String value){
        RDeque<Object> deque = redissonClient.getDeque(key);
        deque.addFirst(value);
    }

    /**
     * 将值 value 插入到列表 key 的表尾
     * @param key
     * @param value
     * @return
     */
    public static boolean Rpush(String key, String value){
        RList<Object> list = redissonClient.getList(key);
        boolean add = list.add(value);
        return add;
    }

    /**
     * 将多个 value 插入到列表 key 的表尾
     * @param key
     * @param values
     * @return
     */
    public static boolean Rpush(String key, List<String> values){
        RList<Object> list = redissonClient.getList(key);
        boolean b = list.addAll(values);
        return b;
    }

    /**
     * 返回列表 key 中指定区间内的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static List<Object> lrange(String key, int start, int end){
        RList<Object> list = redissonClient.getList(key);
        List<Object> range = list.range(start, end);
        return range;
    }

    /**
     * 返回列表 key 中所有的元素
     * @param key
     * @return
     */
    public static List lrange(String key) {
        RList<Object> list = redissonClient.getList(key);
        List<Object> objects = list.readAll();
        return objects;
    }

    /**
     *根据参数 count 的值，移除列表中与参数 value 相等的元素
     * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
     * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
     * count = 0 : 移除表中所有与 value 相等的值。
     * @param key
     * @param value
     * @param count
     * @return
     */
    public static boolean lremove(String key, String value, int count){
        RList<Object> list = redissonClient.getList(key);
        boolean remove = list.remove(value, count);
        return remove;
    }
    /**
     * 添加集合
     * @param key
     * @param member
     * @return
     */
    public static boolean sadd(String key, List<String> member) {
        RSet<Object> set = redissonClient.getSet(key);
        boolean b = set.addAll(member);
        return b;
    }

    /**
     * 判断 member 元素是否属于集合 key 的成员
     * @param key
     * @param member
     * @return
     */
    public static boolean sismember(String key, String member){
        RSet<Object> set = redissonClient.getSet(key);
        boolean contains = set.contains(member);
        return contains;
    }

    /**
     * 返回集合 key 的基数(集合中元素的数量)
     * @param key
     * @return
     */
    public static int scard(String key){
        RSet<Object> set = redissonClient.getSet(key);
        int size = set.size();
        return size;
    }

    /**
     * 返回集合 key 中的所有成员
     * @param key
     * @return
     */
    public static Set smembers(String key){
        RSet<Object> set = redissonClient.getSet(key);
        Set<Object> result = set.readAll();
        return result;
    }

    /**
     * 移除集合 key 中的一个或多个 member 元素
     * @param key
     * @param member
     * @return
     */
    public static boolean sRemvoe(String key , List<String> member){
        RSet<Object> set = redissonClient.getSet(key);
        boolean b = set.removeAll(member);
        return b;
    }

    /**
     *一个或多个 member 元素及其 score 值加入到有序集 key 当中
     * @param key
     * @param scoreMembers
     * @return
     */
    public static int zadd(String key, Map<Object,Double> scoreMembers){
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(key);
        int i = scoredSortedSet.addAll(scoreMembers);
        return i;
    }

    /**
     * 一个 member 元素及其 score 值加入到有序集 key 当中
     * @param key
     * @param value
     * @param score
     * @return
     */
    public static boolean zadd(String key, String value, double score){
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(key);
        boolean add = scoredSortedSet.add(score, value);
        return add;
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值
     * @param key
     * @param value
     * @return
     */
    public static Double zScore(String key, String value){
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(key);
        Double score = scoredSortedSet.getScore(value);
        return score;
    }

    /**
     * 返回有序集 key 的基数。
     * @param key
     * @return
     */
    public static int zCard(String key){
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(key);
        int size = scoredSortedSet.size();
        return size;
    }

    /**
     * 返回有序集 key 中，指定区间内的成员  (0,-1)表示整个有序集
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Collection<Object> zRange(String key, int start, int end){
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(key);
        Collection<Object> objects = scoredSortedSet.valueRange(start, end);
        return objects;
    }

    /**
     * 移除有序集 key 中的元素
     * @param key
     * @return
     */
    public static boolean zRemove(String key, List<String> members){
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(key);
        boolean b = scoredSortedSet.removeAll(members);
        return b;
    }


    public static long getTTL(String key) throws Exception {
        RType rtype = redissonClient.getKeys().getType(key);
        long ttl;
        if (null == rtype) {
            throw new Exception("NOT FOUND TYPE");
        }
        switch (rtype) {
            case OBJECT:
                ttl = redissonClient.getBucket(key).remainTimeToLive();
                break;
            case MAP:
                ttl = redissonClient.getMap(key).remainTimeToLive();
                break;
            case LIST:
                ttl = redissonClient.getList(key).remainTimeToLive();
                break;
            case SET:
                ttl = redissonClient.getSet(key).remainTimeToLive();
                break;
            case ZSET:
                ttl = redissonClient.getScoredSortedSet(key).remainTimeToLive();
                break;
            default:
                throw new Exception("NOT FOUND TYPE");
        }
        return ttl;
    }

    public static long setTTL(String key,long ttl) throws Exception {
        RType rtype = redissonClient.getKeys().getType(key);
        if (null == rtype) {
            throw new Exception("NOT FOUND TYPE");
        }
        switch (rtype) {
            case OBJECT:
                redissonClient.getBucket(key).expire(ttl,TimeUnit.MILLISECONDS);
                break;
            case MAP:
                redissonClient.getMap(key).expire(ttl,TimeUnit.MILLISECONDS);
                break;
            case LIST:
                redissonClient.getList(key).expire(ttl,TimeUnit.MILLISECONDS);
                break;
            case SET:
                redissonClient.getSet(key).expire(ttl,TimeUnit.MILLISECONDS);
                break;
            case ZSET:
                 redissonClient.getScoredSortedSet(key).expire(ttl,TimeUnit.MILLISECONDS);
                break;
            default:
                throw new Exception("NOT FOUND TYPE");
        }
        return ttl;
    }
}
