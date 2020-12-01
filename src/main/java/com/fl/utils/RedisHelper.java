package com.fl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Set;

public class RedisHelper {
    protected static  final Logger logger = LoggerFactory.getLogger(RedisHelper.class);


    /**
     * jedis 连接池管理
     */
    private static int MAX_WAIT = 15 * 1000;
    private static JedisPoolConfig config = null;
    private static JedisPool jedisPool = null;

    private static void initJedisPool(){
        if(jedisPool != null){
            return;
        }
        try {
            config = new JedisPoolConfig();
            config.setMaxTotal(2000);
            config.setMaxIdle(32);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(config, "127.0.0.1",6379);
        }
        catch (Exception e){
            logger.error("jedispool failed:" + e.getMessage());
            if(jedisPool != null){
                jedisPool.close();
                jedisPool = null;
            }
        }
    }


    /**
     * 初始化
     */
    public static synchronized void setupJedisPool(){
        if(jedisPool == null) {
            initJedisPool();
        }
    }

    /**
     * 关闭jedis连接上池
     */
    public static void shutdownJedisPool(){
        if(jedisPool != null && !jedisPool.isClosed()){
            jedisPool.close();
        }
    }


    /**
     * 获取jedis
     *
     * @return
     */
    public static Jedis getJedis() {

        if(jedisPool == null){
            setupJedisPool();
        }

        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
        }
        catch (Exception e){

        }
        return jedis;
    }

    /**
     * jedis放回连接池
     *
     * @param jedis
     */
    public static void closeJedis(Jedis jedis) {

        try {
            if (jedis != null) {
                jedis.close();
            }
        }
        catch (Exception e){

        }
    }

    /**
     * get
     * @param key
     * @return
     */
    public static String get(String key) {

        String ret = null;

        Jedis jedis = getJedis();
        if(jedis == null) {
            return null;
        }

        try {
            ret = jedis.get(key);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();

            return  ret;
        }

    }


    /**
     * 设置键值
     * @param key
     * @param value
     */
    public static void set(String key, String value) {

        Jedis jedis = getJedis();

        if(jedis == null)
            return;

        try {
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();
        }
    }


    /**
     * 设置键值
     * @param key
     * @param value
     */
    public static void hset(String key, String field, String value) {

        Jedis jedis = getJedis();

        if(jedis == null)
            return;

        try {
            jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();
        }
    }

    /**
     * 链表做
     * @param key
     * @param value
     */
    public static long lpush(String key, String value) {

        Jedis jedis = getJedis();
        long ret = 0;
        if(jedis == null)
            return ret;

        try {
            ret = jedis.lpush(key, value);
        } catch (Exception e) {
            logger.info(e.getMessage());
            ret = 0;
        } finally {
            if(jedis != null)
                jedis.close();
            return  ret;
        }
    }

    /**
     * 链表做
     * @param key
     * @param value
     */
    public static long rpush(String key, String value) {

        Jedis jedis = getJedis();
        long ret = 0;
        if(jedis == null)
            return ret;

        try {
            ret = jedis.rpush(key, value);
        } catch (Exception e) {
            logger.info(e.getMessage());
            ret = 0;
        } finally {
            if(jedis != null)
                jedis.close();
            return  ret;
        }
    }

    public static void lrem(String key, String value){
        Jedis jedis = getJedis();
        if(jedis != null){

            try {
                jedis.lrem(key, 0, value);
            } catch (Exception e) {
                logger.info(e.getMessage());

            } finally {
                if(jedis != null)
                    jedis.close();
            }
        }
    }
    /**
     * 链表做
     * @param key
     */
    public static List<String> lrange(String key, long start, long end) {

        Jedis jedis = getJedis();
        List<String> ret = null;
        if(jedis == null)
            return ret;

        try {
            ret = jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.info(e.getMessage());
            ret = null;
        } finally {
            if(jedis != null)
                jedis.close();
            return  ret;
        }
    }

    /**
     * 链表做
     * @param key
     */
    public static long llen(String key) {

        Jedis jedis = getJedis();
        long ret = 0;
        if(jedis == null)
            return ret;

        try {
            ret = jedis.llen(key);
        } catch (Exception e) {
            logger.info(e.getMessage());
            ret = 0;
        } finally {
            if(jedis != null)
                jedis.close();
            return  ret;
        }
    }

    /**
     * 链表做
     * @param key
     */
    public static String lpop(String key) {

        String ret = null;
        Jedis jedis = getJedis();

        if(jedis == null)
            return ret;

        try {
            ret = jedis.lpop(key);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();

            return ret;
        }
    }


    /**
     * 链表做
     * @param key
     */
    public static String rpop(String key) {

        String ret = null;
        Jedis jedis = getJedis();

        if(jedis == null)
            return ret;

        try {
            ret = jedis.rpop(key);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();

            return ret;
        }
    }


    /**
     * 判断hash字段是否存在
     * @param key
     * @param field
     * @return
     */
    public  static  boolean hexists(String key, String field){

        Jedis jedis = getJedis();

        boolean e = false;

        if(jedis == null)
            return e;

        try{
            e = jedis.hexists(key, field);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return e;
        }
    }

    /**
     * 设置键值
     * @param key
     * @param field
     */
    public static String hget(String key, String field) {

        String ret = null;

        Jedis jedis = getJedis();

        if(jedis == null)
            return ret;

        try {
            ret = jedis.hget(key, field);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();
            return  ret;
        }
    }

    /**
     * 设置键值
     * @param key
     * @param value
     */
    public static long hlen(String key, String field, String value) {

        long len = -1;
        Jedis jedis = getJedis();

        if(jedis == null)
            return len;

        try {
            len = jedis.hlen(key);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();

            return  len;
        }

    }

    /**
     * 集合添加元素
     * @param key
     * @param members
     * @return
     */
    public  static  long sadd(String key, String ...members){

        Jedis jedis = getJedis();

        long e = 0;

        if(jedis == null)
            return e;

        try{
            e = jedis.sadd(key, members);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return e;
        }
    }

    /**
     * 获取集合数量
     * @param key
     * @return
     */
    public static long scard(String key){
        Jedis jedis = getJedis();

        long count = 0;

        if(jedis == null)
            return count;

        try{
            count = jedis.scard(key);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return count;
        }
    }

    /**
     * 弹出count个元素，并从集合中删除
     * @param key
     * @param count
     * @return
     */
    public static Set<String> spop(String key, int count ){
        Jedis jedis = getJedis();

        Set<String> e = null;

        if(jedis == null)
            return e;

        try{
            e = jedis.spop(key, count);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return e;
        }
    }



    /**
     * 获取集合内容
     * @param key
     * @return
     */
    public  static Set<String> smembers(String key){

        Jedis jedis = getJedis();

        Set<String> e = null;

        if(jedis == null)
            return e;

        try{
            e = jedis.smembers(key);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return e;
        }
    }

    /**
     * 判断集合是否包含此值
     * @param key
     * @param val
     * @return
     */
    public  static boolean contains(String key, String val){

        Jedis jedis = getJedis();

        if(jedis == null)
            return  false;

        Set<String> e = null;
        try{
            e = jedis.smembers(key);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();

            if(e == null){
                return false;
            }
            else{
                return e.contains(val);
            }
        }
    }

    /**
     * 有序集合中添加元素
     * @param key
     * @param score
     * @param val
     * @return
     */
    public static boolean zadd(String key, double score, String val){
        Jedis jedis = getJedis();

        if(jedis == null)
            return  false;

        boolean ok = false;
        try{
           jedis.zadd(key, score, val );

           ok = true;
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return ok;
        }
    }

    /**
     * 有序集合中更新元素的score
     * @param key
     * @param score
     * @param val
     * @return
     */
    public static boolean zupdate(String key, double score, String val){
        Jedis jedis = getJedis();

        if(jedis == null)
            return  false;

        boolean ok = false;
        try{
            jedis.zadd(key, score, val );
            ok = true;
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return ok;
        }
    }

    /**
     * 删除多个内容
     * @param key
     * @param members
     * @return
     */
    public static boolean zrem(String key, String... members){
        Jedis jedis = getJedis();

        if(jedis == null)
            return  false;

        boolean ok = false;
        try{
            jedis.zrem(key, members);
            ok = true;
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return ok;
        }
    }
//
//    public static Set<String> zrange(String key,long min,long max){
//        Jedis jedis = getJedis();
//
//        Set<String> e = null;
//
//        if(jedis == null)
//            return  e;
//
//        boolean ok = false;
//        try{
//            e = jedis.zrange(key,min,max);
//        }
//        catch (Exception ex){
//            logger.info(ex.getMessage());
//        }
//        finally {
//            if(jedis != null)
//                jedis.close();
//            return e;
//        }
//    }
    /**
     * 获取多个内容
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public static Set<String> zrangebyscore(String key, double min, double max, int offset, int count){
        Jedis jedis = getJedis();

        Set<String> e = null;

        if(jedis == null)
            return  e;

        boolean ok = false;
        try{
            e = jedis.zrangeByScore(key, min, max, offset, count);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return e;
        }
    }


    public static void expire(String key, int seconds){
        Jedis jedis = getJedis();

        if(jedis == null)
            return;

        try {
            jedis.expire(key, seconds);

        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();
        }
    }
    /**
     * 设置，
     * @param key
     * @param seconds 超时时间
     * @param value
     */
    public static void setex(String key, String value, int seconds) {

        Jedis jedis = getJedis();

        if(jedis == null)
            return;

        try {
            jedis.setex(key, seconds, value);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(jedis != null)
                jedis.close();
        }
    }

    /**
     *  delete
     * @param key
     */
    public static void del(String key) {

        Jedis jedis = getJedis();

        if(jedis == null)
            return;

        try {
            jedis.del(key);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public  static  boolean exists(String key){

        Jedis jedis = getJedis();

        boolean e = false;

        if(jedis == null)
            return e;

        try{
            e = jedis.exists(key);
        }
        catch (Exception ex){
            logger.info(ex.getMessage());
        }
        finally {
            if(jedis != null)
                jedis.close();
            return e;
        }
    }
}
