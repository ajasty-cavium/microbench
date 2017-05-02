SHELL:=/bin/bash

CFLAGS = -O3 
SRCS = stats.c stats.h

.PHONY: build test checks test-lib test-sendmsg test-dgram test-stream clean quickres

test: checks test-port test-lib test-sendmsg java-tests niotest quickres

test-java: java-tests niotest quickres

test-port:
	@netstat -tulpn 2>&1 | grep 8080 2> /dev/null && ( echo "Please make sure port 8080 is free!" && false ) || true

checks:
	@if [ ! -d /usr/include/event2 ] ; then echo "missing libevent-dev, trying to install with apt" ; sudo apt install libevent-dev -y ; fi
	@if [ ! -f test.txt ] ; then base64 /dev/urandom | head -c 10000000 > test.txt ; fi


quickres:
	if [ -f stream_client.log ] ; then grep type stream_client.log  &&  ( for f in *.log ; do line=`egrep "\((ns|us)" $$f` ; if [ ! -z "$$line" ] ; then echo "$$f,$$line," ; fi ; done ) ; fi
	cat java-tests.csv | sed -e 's/,/\t/g'	


test-lib: libevent-server libevent-client basic-event
	-@killall libevent-server 2>/dev/null || true
	taskset -c 10 ./basic-event > basic_event.log
	taskset -c 11 ./libevent-server > libevent_server.log &
	sleep 2
	taskset -c 10 ./libevent-client > libevent_client.log
	-@killall libevent-server 2>/dev/null || true

test-sendmsg: test-dgram test-stream

test-dgram: sendmsg
	-@killall sendmsg 2>/dev/null || true
	taskset -c 11 ./sendmsg s u > dgram_server.log &
	sleep 2
	grep listening dgram_server.log > /dev/null || ( cat dgram_server.log && netstat -tulpn | grep 8080 && false )
	taskset -c 10 ./sendmsg c u n 100000 > dgram_client.log
	-@killall sendmsg 2>/dev/null || true

test-stream: sendmsg
	-@killall sendmsg 2>/dev/null || true
	taskset -c 11 ./sendmsg s t > stream_server.log &
	sleep 2
	grep listening stream_server.log > /dev/null || ( cat stream_server.log && netstat -tulpn | grep 8080 && false )
	taskset -c 10 ./sendmsg c t n 100000 > stream_client.log
	-@killall sendmsg 2>/dev/null || true

SRCS += bench_sendmsg.c 
BINS += sendmsg

sendmsg: Makefile bench_sendmsg.c stats.c stats.h
	gcc $(CFLAGS) -o sendmsg bench_sendmsg.c stats.c -lm

SRCS += bench_httpserver.c bench_httpclient.c
BINS += libevent-server libevent-client

libevent-server: Makefile bench_httpserver.c
	gcc $(CFLAGS) -o libevent-server bench_httpserver.c -levent

libevent-client: Makefile bench_httpclient.c stats.c stats.h
	gcc $(CFLAGS) -o libevent-client bench_httpclient.c stats.c -levent -lm

SRCS += basic_event.c
BINS += basic-event

basic-event: basic_event.c stats.c stats.h
	gcc basic_event.c stats.c -levent -lm -o basic-event

JAVA_TEST_SRCS = HashCodeTest.java HashTest.java HashTestS.java LoopTest.java ArrayCopyTest.java LinkedListExample.java TreeTest.java ChannelsTest.java FileInputStreamTest.java FileOutputStreamTest.java
SRCS += ArrayCopy.java JAVA_TEST_SRCS

.PHONY: java-tests
java-tests:
	echo "Test,time,time/it,it/s" > java-tests.csv
	for f in $(JAVA_TEST_SRCS) ; do TN=`basename $$f .java` ; javac $$f ; echo running $$TN ; time ( java $$TN > $$TN.log ) 2>$$TN.time ; grep ","  $$TN.log >> java-tests.csv ; done 

niotest:
	if [ ! -d /tmp/ramdisk ] ; then sudo mkdir /tmp/ramdisk ; sudo mount -t tmpfs -o size=20M tmpfs /tmp/ramdisk ; fi
	if [ ! -f /tmp/ramdisk/test.txt ] ; then base64 /dev/urandom | head -c 10000000 > /tmp/ramdisk/test.txt ; fi
	javac ChannelsTest.java && cp ChannelsTest.class /tmp/ramdisk
	javac FileInputStreamTest.java && cp FileInputStreamTest.class /tmp/ramdisk
	if [ ! -f /tmp/ramdisk/Report.class ] ; then cp Report.class /tmp/ramdisk ; fi
	cd /tmp/ramdisk && time ( java ChannelsTest > nio_ChannelTest.log ) 2> nio_ChannelTest.time
	cd /tmp/ramdisk && time ( java FileInputStreamTest > nio_FileInputStreamTest.log ) 2> nio_FileInputStreamTest.time
	cp /tmp/ramdisk/nio* .
	grep "," nio_ChannelTest.log | sed -e 's/^/nio-/' >> java-tests.csv
	grep "," nio_FileInputStreamTest.log | sed -e 's/^/nio-/' >> java-tests.csv
	

zip:
	tar cvzf test.tgz $(SRCS) Makefile

build: $(BINS)

clean: 
	rm -f $(BINS) *.class *.log *.time

