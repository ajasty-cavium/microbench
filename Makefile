SHELL:=/bin/bash

CFLAGS = -O3 
SRCS = stats.c stats.h

.PHONY: build test checks test-lib test-sendmsg test-dgram test-stream clean quickres archive results_show test-fileio

ifndef $(LOGDIR)
LOGDIR=logs
endif


test: checks test-port test-lib test-sendmsg java-tests niotest quickres

test-java: java-tests quickres

test-fileio: java-file-tests c-file-tests

test-port:
	@netstat -tulpn 2>&1 | grep 8080 2> /dev/null && ( echo "Please make sure port 8080 is free!" && false ) || true

checks:
	@if [ ! -d /usr/include/event2 ] ; then echo "missing libevent-dev, trying to install with apt" ; sudo apt install libevent-dev -y ; fi
	@if [ ! -f test.txt ] ; then base64 /dev/urandom | head -c 10000000 > test.txt ; fi
	@if [ ! -d $(LOGDIR) ] ; then mkdir -p $(LOGDIR) ; fi

archive: checks
	mv *.log $(LOGDIR)
	mv *.time $(LOGDIR)

quickres: results_show archive

results_show:
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

SRCS += bench_write.c bench_read.c
BINS += bench_write bench_read 

bench_write: bench_write.c
	gcc -D_GNU_SOURCE -O2 -g bench_write.c -o bench_write

bench_read: bench_read.c
	gcc -D_GNU_SOURCE -O2 -g bench_read.c -o bench_read

JAVA_TEST_SRCS = HashCodeTest.java HashTest.java HashTestS.java LoopTest.java ArrayCopyTest.java LinkedListExample.java TreeTest.java ArrayCopyTest.java
JAVA_FILE_TEST_SRCS = ChannelsTest.java FileInputStreamTest.java FileOutputStreamTest.java
SRCS += $(JAVA_TEST_SRCS) $(JAVA_FILE_TEST_SRCS)

.PHONY: java-tests niotest ramdisk-check
java-tests: checks java-file-tests c-file-tests
	echo "Test,time,time/it,it/s" > java-tests.csv
	for f in $(JAVA_TEST_SRCS) ; do TN=`basename $$f .java` ; javac $$f ; echo running $$TN ; time ( java $$TN > $$TN.log ) 2>$$TN.time ; grep ","  $$TN.log >> java-tests.csv ; done 
	cat java-file-tests.csv >> java-tests.csv

SRCS += UnsafeCopyTest.java

UnsafeCopyTest: UnsafeCopyTest.java
	javac UnsafeCopyTest.java

ArrayCopyTest: ArrayCopyTest.java
	javac ArrayCopyTest.java

CopyTest.csv : UnsafeCopyTest ArrayCopyTest
	java UnsafeCopyTest 10   10000000 >> CopyTest.csv
	java UnsafeCopyTest 100  10000000 >> CopyTest.csv
	java UnsafeCopyTest 1000 10000000 >> CopyTest.csv
	java UnsafeCopyTest 10000 1000000 >> CopyTest.csv
	java UnsafeCopyTest 100000 100000 >> CopyTest.csv
	java ArrayCopyTest 10   10000000 >> CopyTest.csv
	java ArrayCopyTest 100  10000000 >> CopyTest.csv
	java ArrayCopyTest 1000 10000000 >> CopyTest.csv
	java ArrayCopyTest 10000 1000000 >> CopyTest.csv
	java ArrayCopyTest 100000 100000 >> CopyTest.csv


java-file-tests: checks niotest
	for f in $(JAVA_FILE_TEST_SRCS) ; do TN=`basename $$f .java` ; javac $$f ; echo running $$TN ; \
	 for size in 10 100 1000 4096 ; do \
		time ( java $$TN $$size 10 > $${TN}_$${size}.log ) 2>$${TN}_$${size}.time ; grep ","  $${TN}_$${size}.log >> java-file-tests.csv \
	 ; done \
	; done 
	mkdir -p $(LOGDIR)/java
	mv *.log *.time $(LOGDIR)/java

c-file-tests: checks ramdisk-check bench_write bench_read
	for TN in bench_write bench_read ; do \
	 cp $$TN /tmp/ramdisk ; \
	 for size in 10 100 1000 4096 ; do \
		time ( ./$$TN $$size 10 > $${TN}_$${size}.log ) 2>$${TN}_$${size}.time ; grep ","  $${TN}_$${size}.log >> c-file-tests.csv ; \
		pushd /tmp/ramdisk >/dev/null && time ( ./$$TN $$size 100 > ramdisk_$${TN}_$${size}.log ) 2>ramdisk_$${TN}_$${size}.time ; popd >/dev/null ; \
		grep ","  /tmp/ramdisk/ramdisk_$${TN}_$${size}.log | sed -e 's/^/ramdisk-/' >> c-file-tests.csv \
	 ; done \
	; done
	mkdir -p $(LOGDIR)/c
	mv /tmp/ramdisk/*.log /tmp/ramdisk/*.time *.log *.time $(LOGDIR)/c

ramdisk-check:
	if [ ! -d /tmp/ramdisk ] ; then sudo mkdir /tmp/ramdisk ; sudo mount -t tmpfs -o size=220m tmpfs /tmp/ramdisk ; sudo chmod a+w /tmp/ramdisk ; fi
	if [ ! -f /tmp/ramdisk/test.txt ] ; then base64 /dev/urandom | head -c 100000000 > /tmp/ramdisk/test.txt ; fi

niotest: checks ramdisk-check
	for f in ChannelsTest FileInputStreamTest FileOutputStreamTest Report ; do \
		javac $$f.java && cp $$f.class /tmp/ramdisk ; \
	done
	for TN in FileOutputStreamTest ChannelsTest FileInputStreamTest ; do \
		echo running $$TN ; \
		for size in 10 100 1000 4096 ; do \
		 pushd /tmp/ramdisk >/dev/null && time ( java $$TN $$size 100 > ramdisk_$${TN}_$${size}.log ) 2> ramdisk_$${TN}_$${size}.time && popd >/dev/null ; \
		 grep ","  /tmp/ramdisk/ramdisk_$${TN}_$${size}.log | sed -e 's/^/ramdisk-/' >> java-file-tests.csv \
		; done \
	; done
	mv /tmp/ramdisk/*.log /tmp/ramdisk/*.time $(LOGDIR)

zip:
	tar cvzf test.tgz $(SRCS) Makefile

build: $(BINS)

clean: 
	rm -f $(BINS) *.class test.txt testout.txt c-file-tests.csv java-file-tests.csv java-tests.csv CopyTest.csv
	if [ -d /tmp/ramdisk ] ; then sudo umount /tmp/ramdisk ; sudo rm -rf /tmp/ramdisk ; fi


