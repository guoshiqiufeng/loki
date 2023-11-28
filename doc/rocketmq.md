## rocketmq

### 1.安装、启动

按照官方文档安装、启动 ``https://rocketmq.apache.org``

## 2.创建topic

```shell
sh bin/mqadmin updatetopic -n localhost:9876 -t lokiTopic -c DefaultCluster
```
```shell
nohup sh bin/mqnamesrv &
```
```shell
nohup sh bin/mqbroker -n localhost:9876 --enable-proxy &
```
```shell
bin/mqshutdown broker
```
```shell
bin/mqshutdown namesrv
```