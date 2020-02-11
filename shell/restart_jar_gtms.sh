#!/bin/bash

sys(){
pid=`ps -ef|grep sys-gtms|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ijep-service-sys.jar >> /home/ijep/logs/sys-gtms.txt --spring.config.location=./config/application-sys-gtms.yml --spring.profiles.active=sys-gtms &
}

eureka(){
pid=`ps -ef|grep eureka-gtms|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xmx1024m -Xms1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ijep-registry-eureka-6.2.0-SNAPSHOT.jar >> /home/ijep/logs/eureka-gtms.txt --spring.config.location=./config/application-eureka-gtms.yml --spring.profiles.active=eureka-gtms &

}


zuul(){
pid=`ps -ef|grep ijep-router-zuul|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ijep-router-zuul.jar >> /home/ijep/logs/zuul-gtms.txt &
}


oauth2(){
pid=`ps -ef|grep ijep-service-oauth2|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ijep-service-oauth2.jar >> /home/ijep/logs/oauth2-gtms.txt --spring.config.location=./config/application-oauth2-gtms.yml --spring.profiles.active=oauth2-gtms &
}



attachment(){
pid=`ps -ef|grep ijep-service-attachment|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ijep-service-attachment.jar >> /home/ijep/logs/attachment-gtms.txt --spring.config.location=./config/application-attachment-gtms.yml --spring.profiles.active=attachment-gtms.yml &
}


config(){
pid=`ps -ef|grep gtms-service-config|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar gtms-service-config.jar >> /home/ijep/logs/gtms-config.log --spring.config.location=./config/application-config-gtms.yml --spring.profiles.active=config-gtms &
}

baffle(){
pid=`ps -ef|grep gtms-service-baffle|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar gtms-service-baffle.jar >> /home/ijep/logs/baffle.log --spring.config.location=./config/application-baffle-gtms.yml --spring.profiles.active=service-baffle-gtms.yml &
}

gtms(){
pid=`ps -ef|grep gtms-service-enterprise|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar gtms-service-enterprise.jar >> /home/ijep/logs/gtms.log --spring.config.location=./config/application-dev-gtms.yml --spring.profiles.active=dev-gtms.yml &
}


serial(){
pid=`ps -ef|grep ijep-service-serial|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -Xmx1024m -Xms1024m -Xmn356m -Xss256k -jar ijep-service-serial.jar >> /home/ijep/logs/serial.txt --spring.config.location=./config/application-serial-gtms.yml --spring.profiles.active=serial-gtms.yml &
}

funds(){
pid=`ps -ef|grep gtms-service-funds|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar gtms-service-funds.jar >> /home/ijep/logs/funds.log --spring.config.location=./config/application-funds-gtms.yml --spring.profiles.active=funds-gtms &
}

bpm(){
pid=`ps -ef|grep ijep-service-bpm|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ijep-service-bpm-6.2.0-SNAPSHOT.jar >> /home/ijep/logs/bpm.log --spring.config.location=./config/application-bpm-gtms.yml --spring.profiles.active=bpm-gtms.yml &
}

job(){
pid=`ps -ef|grep gtms-service-job|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar gtms-service-job.jar >> /home/ijep/logs/job.log --spring.config.location=./config/application-dev-job.yml --spring.profiles.active=dev-job.yml &
}

gateway(){
pid=`ps -ef|grep gtms-service-gateway|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar gtms-service-gateway.jar >> /home/ijep/logs/gateway.log --spring.config.location=./config/application-gateway-gtms.yml --spring.profiles.active=gateway-gtms.yml &
}

schedule(){
pid=`ps -ef|grep ijep-service-schedule|grep -v grep|awk '{print $2}'`
for i in $pid;do kill -9 $i;done
cd /home/ijep/domain/java/gtms
nohup java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms1024m -Xmx1024m -Xmn356m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ijep-service-schedule.jar >> /home/ijep/logs/schedule.log --spring.config.location=./config/application-schedule-gtms.yml --spring.profiles.active=schedule-gtms.yml &
}

if [ -z "$1" ];then
   echo "usage $0 sys|chabei_console|schedule|chabei_manage|rule|eureka|zuul|spider|all"
   exit 0
fi

if [[ "$1" == "eureka" ]];then
   eureka
   exit 0
fi

if [[ "$1" == "sys" ]];then
   sys
   exit 0
fi

if [[ "$1" == "oauth2" ]];then
   oauth2
   exit 0
fi


if [[ "$1" == "zuul" ]];then
   zuul
   exit 0
fi

if [[ "$1" == "gtms" ]];then
   gtms
   exit 0
fi

if [[ "$1" == "config" ]];then
   config
   exit 0
fi


if [[ "$1" == "attachment" ]];then
   attachment
   exit 0
fi

if [[ "$1" == "funds" ]];then
   funds
   exit 0
fi

if [[ "$1" == "baffle" ]];then
   baffle
   exit 0
fi


if [[ "$1" == "serial" ]];then
   serial
   exit 0
fi

if [[ "$1" == "bpm" ]];then
   bpm
   exit 0
fi

if [[ "$1" == "gateway" ]];then
   gateway
   exit 0
fi

if [[ "$1" == "schedule" ]];then
   schedule
   exit 0
fi

if [[ "$1" == "job" ]];then
   job
   exit 0
fi

if [[ "$1" == "all" ]];then
   eureka
   sleep 30
   sys
   sleep 30
   zuul
   sleep 30
   oauth2
   sleep 30
   serial
   sleep 30
   gtms
   sleep 30
   attachment
   sleep 30
   baffle
   sleep 30
   config
   sleep 30
   funds
   sleep 30
   bpm
   sleep 30
   gateway
   sleep 30
   schedule
   sleep 30
   job
   sleep 30
   exit 0
fi
