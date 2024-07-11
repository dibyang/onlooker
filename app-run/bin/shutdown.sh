#!/bin/bash

oldpath=$(pwd)
basepath=$(cd `dirname $0`; pwd)

name=@app_name@
approot=`dirname $basepath`
cd $approot

instance=`ps -ef | grep app.home=$approot | sed '/grep/d' | awk '{print $2}'`
if [ -z "$instance" ]; then
      echo "$name is not running."
	exit 0
fi
pkill  -f  app.home=$approot
echo -n "stopping"
for i in {1..10}
do
  echo -n "."
  sleep 2s
  instance=`ps -ef | grep app.home=$approot | sed '/grep/d' | awk '{print $2}'`
  if [ -z "$instance" ]; then
    break
  fi
done
echo ""

if [ -n "$instance" ]; then
  pkill -9  -f  app.home=$approot
fi
echo "$name stopped"

