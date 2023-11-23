## rocketmq

### 1.安装、启动

按照官方文档安装、启动 ``https://rocketmq.apache.org``

## 2.创建topic

```
sh bin/mqadmin updatetopic -n localhost:9876 -t lokiTopic -c DefaultCluster
```

nohup sh bin/mqnamesrv &
nohup sh bin/mqbroker -n localhost:9876 --enable-proxy &

bin/mqshutdown broker

bin/mqshutdown namesrv