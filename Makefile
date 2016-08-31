SHELL:=/bin/bash

CFLAGS = -O3 
SRCS = stats.c stats.h

.PHONY: build test test-lib test-sendmsg test-dgram test-stream clean quickres

test: test-port test-lib test-sendmsg quickres

test-port:
	@netstat -tulpn 2>&1 | grep 8080 2> /dev/null && ( echo "Please make sure port 8080 is free!" && false ) || true

quickres:
	@grep type stream_client.log  && for f in *.log ; do egrep "\((ns|us)" $$f ; done

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

SRCS += ArrayCopy.java ChannelsTest.java HashCodeTest.java HashTestS.java LoopTest.java ArrayCopyTest.java FileInputStreamTest.java HashTest.java LinkedListExample.java TreeTest.java

.PHONY: java-tests
java-tests:
	for f in *Test.java ; do TN=`basename $$f .java` ; javac $$f ; echo running $$TN ; time ( java $$TN > $$TN.log ) 2>$$TN.time ; done 

zip:
	tar cvzf test.tgz $(SRCS) Makefile


build: $(BINS)

clean: 
	rm -f $(BINS) *.class *.log *.time

