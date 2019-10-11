## redisTest是用来测试redis新集群是否正常工作
### reids Client
使用了Redisson作为client连接redis
[Redisson官方文档地址](https://github.com/redisson/redisson/wiki/1.-Overview)
### Api使用

1.使用内置的数据进行五种数据类型的写入测试（String、hash、list、set、sortedSet）
```
GET
    http://ip:port/main/startSet

Response
{
    "code": 200,
    "message": "success"
}
```

2.获取内置key的字符串类型数据（自定义存入的数据需要使用下面获取自定义数据的api）

```
GET
    http://ip:port/main/startStringGet

Response
{
    "code": 200,
    "data": [
        "txt_value1",
        "txt_value2",
        "txt_value3",
        "txt_value4",
        "txt_value5",
        "txt_value6"
    ]
}
```
3.获取内置写入测试中的hash数据

```
GET
    http://ip:port/main/startHget

Response
{
    "code": 200,
    "data": [
        {
            "filed1": "value1"
        },
        {
            "filed2": "value2"
        },
        {
            "filed3": "value3"
        },
        {
            "filed4": "value4"
        },
        {
            "filed5": "value5"
        }
    ]
}
```
4.获取内置写入测试中的list数据

```
GET
    http://ip:port/main/startLrange

Response
{
    "code": 200,
    "data": [
        "a",
        "b",
        "c"
    ]
}
```
5.获取内置写入测试中的set数据

```
GET
    http://ip:port/main/startSmembers

Response
{
    "code": 200,
    "data": [
        "f",
        "e",
        "d"
    ]
}
```
6.获取内置写入测试中的sortedSet数据

```
GET
    http://ip:port/main/startZrange

Response
{
    "code": 200,
    "data": [
        "z",
        "y",
        "x"
    ]
}
```
7.删除所有写入测试产生的数据

```
GET
    http://ip:port/main/startDelAll

Response
{
    "code": 200
}
```

8.自定义写入String类型数据

```
POST
    http://ip:port/main/setString?key=$data&value=$data
Response
{
    "code": 200
}
```
9.获取自定义写入的String数据

```
GET
    http://ip:port/main/getString?key=$data

Response
{
    "code": 200,
    "data": "value"
}
```
10.删除自定义写入的String数据
```
GET
    http://ip:port/main/delString?key=$data

Response
{
    "code": 200,
    "data": true
}
```
11.获取redis集群的所有node
```
GET
    http://ip:port/main/getAllNodes

Response
{
    "code": 200,
    "data": [
        "10.16.236.125:8307",
        "10.16.236.125:8306",
        "10.16.236.125:8305",
        "10.16.236.125:8309",
        "10.16.236.125:8308",
        "10.16.236.125:8304"
    ]
}
```

12.获取redis集群的所有master
```
GET
    http://ip:port/main/getMasterNodes

Response
{
    "code": 200,
    "data": [
        "10.16.236.125:8307",
        "10.16.236.125:8309",
        "10.16.236.125:8304"
    ]
}
```

13.写入自定义MAP类型数据

```
POST
    http://ip:port/main/startHset?key=$data

Response
{
    "code": 200
}
```
14.获取自定义MAP类型数据

```
GET
    http://ip:port/main/startHgetWithK?key=$data

Response
{
    "code": 200,
    "data": [
        {
            "key": "xaecbd:testUserMap"
        },
        {
            "test1": "value1"
        },
        {
            "test2": "value2"
        }
    ]
}
```

15删除自定义MAP类型数据

```
POST
    http://ip:port/main/startHdel?key=$data
body
    ["test1","test2"]
Response
{
    "code": 200,
    "message": "Del  2"
}
```


