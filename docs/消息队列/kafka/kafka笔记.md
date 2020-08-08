http://reader.epubee.com/books/mobile/8e/8edc4cf50e13c82ec637ad3d4eb244a6/text00004.html

# Hello World

## 启动

1.启动zookeeper 可以直接使用kafka自带的

zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties

2.启动kafka

kafka-server-start /usr/local/etc/kafka/server.properties 

控制台输出结尾处的“[Kafka Server 0],started”标志Kafka服务器启动成功，默认的服务端口是9092。

## 创建topic

 kafka-topics --create --zookeeper localhost:2181 --topic test --partitions 1 --replication-factor 1

成功会显示：Created topic test.

创建一个主题（topic）用于消息的发送与接收。这一步将创建一个名为test的topic，该topic只有一个分区（partition），且该partition也只有一个副本（replica）处理消息。

## 查看topic状态

指令： kafka-topics --describe --zookeeper localhost:2181 --topic test

![image-20200803164855603](kafka笔记.assets/image-20200803164855603.png)

这段命令的输出非常清晰地表明我们创建的 topic名为 test，分区数（PartitionCount）是1，副本数（ReplicationFactor）也是1。



## 发送与接收消息

发送：

kafka-console-producer --broker-list localhost:9092 --topic test

之后可以不断使用回车来发送消息



接收：

另起一个窗口 

kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning

在发送窗口输入消息回车 短暂的延迟后接收窗口会显示这条信息



# 