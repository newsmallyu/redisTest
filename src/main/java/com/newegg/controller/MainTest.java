package com.newegg.controller;


import com.newegg.service.MainTestService;
import org.redisson.api.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/main")
public class MainTest {
    @Autowired
    MainTestService mainTestService;

    @PostMapping("/setString")
    public RestResponse setString(@RequestParam String key,@RequestParam String value){
        mainTestService.testsetString(key, value);
        return SUCCESS();
    }
    @GetMapping("/getString")
    public RestResponse getString(@RequestParam String key){
        String string = mainTestService.testGetString(key);
        return SUCCESS_DATA(string);
    }
    @GetMapping("/delString")
    public RestResponse delString(@RequestParam String key){
        boolean b = mainTestService.testDelString(key);
        return SUCCESS_DATA(b);
    }
    @PostMapping("/startHset")
    public RestResponse hset(@RequestParam String key,@RequestParam Map<String,String> map){
        mainTestService.testhset(key, map);
        return SUCCESS();
    }
    @GetMapping("/startHgetWithK")
    @ResponseBody
    public RestResponse startHgetWithK(@RequestParam String key){
        Set<Map.Entry<Object, Object>> entries = mainTestService.testHget(key);
        return SUCCESS_DATA(entries);
    }
    @PostMapping("/startHdel")
    @ResponseBody
    public RestResponse startHdel(@RequestParam String key , @RequestBody List<String> list){
        long l = mainTestService.testHdel(key, list);
        return SUCCESS("Del  " + l);
    }

    @GetMapping("/startSet")
    @ResponseBody
    public RestResponse startBatchSetTest(){
        try{
            //测试字符串写入
            mainTestService.testbatchSet();
            //测试哈希表写入
            mainTestService.testHset();
            //测试列表写入
            mainTestService.testLpush();
            //测试集合写入
            mainTestService.testSadd();
            //测试有序集合写入
            mainTestService.testZadd();
            return SUCCESS("success");
        } catch (Exception e){
            return ERROR(e.getMessage());
        }

    }
    @GetMapping("/startStringGet")
    @ResponseBody
    public RestResponse startStringGet(){
        List<String> list = mainTestService.testbatchGet();
        return SUCCESS_DATA(list);
    }
    @GetMapping("/startHget")
    @ResponseBody
    public RestResponse startHget(){
        Set<Map.Entry<Object, Object>> entries = mainTestService.testHget();
        return SUCCESS_DATA(entries);
    }
    @GetMapping("/startLrange")
    @ResponseBody
    public RestResponse startLrange(){
        List list = mainTestService.testLrange();
        return SUCCESS_DATA(list);
    }

    @GetMapping("/startSmembers")
    @ResponseBody
    public RestResponse startSmembers(){
        Set set = mainTestService.testSmembers();
        return SUCCESS_DATA(set);
    }

    @GetMapping("/startZrange")
    @ResponseBody
    public RestResponse startZrange(){
        Collection<Object> objects = mainTestService.testZrange();
        return SUCCESS_DATA(objects);
    }

    @GetMapping("/startDelAll")
    public RestResponse startDel(){
        try{
            mainTestService.testbatchDel();
            mainTestService.testHdel();
            mainTestService.testLrem();
            mainTestService.testSremove();
            mainTestService.testzRemove();
            return SUCCESS();
        }catch (Exception e){
            return ERROR(e.getMessage());
        }
    }

    @GetMapping("/getAllNodes")
    public RestResponse getAllNodes(){
        Collection<Node> nodes = mainTestService.testgetAllNodes();
        return SUCCESS_DATA(nodes);
    }

    @GetMapping("/getMasterNodes")
    public RestResponse getMasterNodes(){
        Collection<Node> nodes = mainTestService.testgetMasterNodes();
        return SUCCESS_DATA(nodes);
    }

    @PostMapping("/setTTL")
    public RestResponse setTTl(@RequestParam String key,@RequestParam long ttl){
        try{
            mainTestService.testsetTTl(key, ttl);
        }catch (Exception e){
            return ERROR(e.getMessage());
        }
        return SUCCESS("setTTL success");

    }

    @GetMapping("/getTTL")
    public RestResponse getTTL(@RequestParam String key){
        long l;
        try {
            l= mainTestService.testgetTTl(key);
        }catch (Exception e){
            return ERROR(e.getMessage());
        }
        return SUCCESS(key + "   TTL:" + l);
    }


    public RestResponse SUCCESS() {
        return new RestResponse();
    }

    public RestResponse SUCCESS(String message) {
        return new RestResponse(200, message);
    }

    public RestResponse SUCCESS_DATA(Object data) {
        return new RestResponse(200, null, data);
    }

    public RestResponse SUCCESS(String message, Object data) {
        return new RestResponse(200, message, data);
    }

    public RestResponse ERROR(String message) {
        return new RestResponse(500, message);
    }
}
