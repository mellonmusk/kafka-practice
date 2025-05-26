# kafka-practice

## 1. kafka download

로컬에서는 개발 편의를 위해 두 방식으로 모두 다운로드함

- [https://kafka.apache.org/downloads](https://kafka.apache.org/downloads)   
위 링크에서 scala 2.13 다운로드 <- 디렉토리 구조가 개발환경 통합을 위해 편리함

- brew 다운로드 <-환경변수 설정할 필요 없음
```
brew install kafka

ls /opt/homebrew/bin

ls /opt/homebrew/Cellar
```

## 2. 실행하기
```
# random uuid 생성
~/kafka_2.13-3.9.1/bin/kafka-storage.sh random-uuid
# 위의 명령어로 나온 결과를 넣어서 실행
kafka-storage format -t {random uuid} -c ~/kafka_2.13-3.9.1/config/kraft/server.properties

(참고 - brew로 설치한 경우:
cd /opt/homebrew/bin
kafka-storage random-uuid
kafka-storage format -t {random uuid} -c /opt/homebrew/etc/kafka/server.properties)

# Kraft 방식으로 실행
kafka-server-start ~/kafka_2.13-3.9.1/config/kraft/server.properties

```

## 3. topic 생성하기
```
kafka-topics --bootstrap-server localhost:9092 --create --topic secondtopic --partitions 3

(Optional)--partitions 3 --replication-factor 1

# list topics
kafka-topics --bootstrap-server localhost:9092 --list

# describe topics
kafka-topics --bootstrap-server localhost:9092 --describe --topic example

# delete topics
kafka-topics --bootstrap-server localhost:9092 --delete --topic example
```

## 4. producer
```
# producing 
kafka-console-producer --bootstrap-server localhost:9092 --topic example

# producing with properties
kafka-console-producer --bootstrap-server localhost:9092 --topic example --producer-property acks=all

# producing with keys
kafka-console-producer --bootstrap-server localhost:9092 --topic example --property parse.key=true --property key.separator=:

```


## 5. consumer
```
# consuming
kafka-console-consumer --bootstrap-server localhost:9092 --topic secondtopic
(Optional) --group example-group

# iterm에서 cmd+shift+d로 새로운 터미널 open
kafka-console-producer --bootstrap-server localhost:9092 --producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner --topic secondtopic (DO NOT USE THIS PARTITONER IN PRODUCTION)

# consuming from beginning 
kafka-console-consumer --bootstrap-server localhost:9092 --topic secondtopic --from-beginning
—
# consumers in groups 
# create a topic with 3 partitions, then start one consumer
kafka-console-consumer --bootstrap-server localhost:9092 --topic thirdtopic --group example-group-2 

# start one producer and start producing. Then start another consumer part of the same group and see how messages are being spread.
kafka-console-consumer --bootstrap-server localhost:9092 --topic thirdtopic --group example-group-2 

—
# list consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list

# describe one specific group
kafka-consumer-groups --bootstrap-server localhost:9092 --group example-group-2

# start a consumer
kafka-console-consumer --bootstrap-server localhost:9092 --topic thirdtopic --group example-group-2 
—
# dry run: reset the offsets to the beginning of each partition
kafka-consumer-groups --bootstrap-server localhost:9092 --group example-group-2 --reset-offsets --to-earliest --topic thirdtopic --dry-run

# to actually run: use execute flag
kafka-consumer-groups --bootstrap-server localhost:9092 --group example-group-2 --reset-offsets --to-earliest --topic thirdtopic —execute

# see lag to check if resetting offset worked properly
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group example-group-2



```
