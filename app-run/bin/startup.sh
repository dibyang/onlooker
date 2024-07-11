#!/bin/bash
name=@app_name@
oldpath=$(pwd)
basepath=$(cd `dirname $0`; pwd)

approot=`dirname $basepath`
cd $approot

daemon="false"
if [ "$1" == "--daemon" ]; then
    daemon="true"
fi

instance=`ps -ef | grep app.home=$approot | sed '/grep/d'`
if [ -n "$instance" ]; then
    echo "$name is running."
	exit 0
fi

# 检查Java版本是否符合要求
check_java_version() {
    required_version=$1

    # 获取Java版本信息
    java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    echo "$java_version"
    # 解析版本号
    IFS='.' read -r -a java_version_parts <<< "$java_version"
    IFS='.' read -r -a required_version_parts <<< "$required_version"

    # 比较版本号
    for i in {0..1}; do
        java_version_part=${java_version_parts[i]}
        required_version_part=${required_version_parts[i]}

        if [ -z "$java_version_part" ]; then
            java_version_part=0
        fi
        if [ -z "$required_version_part" ]; then
            required_version_part=0
        fi

        if [ "$java_version_part" -gt "$required_version_part" ]; then
            return 0 # Java版本高于要求
        fi
        if [ "$java_version_part" -lt "$required_version_part" ]; then
            return 1 # Java版本低于要求
        fi
    done

    return 0 # Java版本符合要求
}


required_java_version="1.8"

check_java_version "$required_java_version"

if [ $? -eq 0 ]; then
    echo "Java version is acceptable."
else
    echo "The JDK requires 1.8.x or above."
    exit 1
fi


JAVA_OPTS="-Dfile.encoding=utf-8 -Dsun.jnu.encoding=utf-8 -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2"
JAVA_OPTS="$JAVA_OPTS -Xmx128m -Xms64m -XX:MaxDirectMemorySize=32m -XX:MetaspaceSize=32M -XX:MaxMetaspaceSize=64m"
JAVA_OPTS="$JAVA_OPTS -cp :$approot/lib/* -Dapp.home=$approot -Dname=$name"
#JAVA_OPTS="$JAVA_OPTS -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnErrorConsoleStatusListener"
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5007"

sysctl -w net.core.rmem_max=5242880
sysctl -w net.core.rmem_default=5242880
sysctl -p

if [ "$daemon" == "true" ];
then
    # shellcheck disable=SC2069
    eval java  "$JAVA_OPTS"  net.xdob.onlooker.Onlooker 2>&1 1>/var/log/onlooker.log &
else
    eval java  "$JAVA_OPTS"  net.xdob.onlooker.Onlooker
fi


