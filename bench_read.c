#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>

double ts2d(struct timespec *s) {
	double val=(double)s->tv_sec * 1e9 + (double)s->tv_nsec;
	return val;
}

int main(int argc, char *argv[]) {
	size_t ret,bytes=0;
	int bufsize=4096;
	
	size_t i;
	struct timespec ts,te;
	//get params and init buffer
	if (argc>1)
		bufsize=atoi(argv[1]);
	char *c=(char *)malloc(bufsize);
	memset(c,42,bufsize);
	//write to file
	FILE *f=fopen("test.txt","rb");
	i=setvbuf(f, NULL, _IONBF, bufsize);
	if (i != 0) {
		printf("Failed setting vbuf\n");
		exit(1);
	}
	clock_gettime(CLOCK_MONOTONIC,&ts);
	while (!feof(f)) {
		ret=fread(c,bufsize,1,f);
		if (ret>0) { 
			bytes+=bufsize;
		} else {
			break;
		}
	}
	fclose(f);
	clock_gettime(CLOCK_MONOTONIC,&te);
	//spit out info
	double MB=(double)bytes / 1e6;
	double time_taken=(double)(ts2d(&te) - ts2d(&ts)) / 1e6;
	double mbps=MB*1000/(time_taken);
	printf("fread[%d/%dMB],%.2f ms,%.2f MB/s\n",bufsize,(int)MB,time_taken,mbps);
	
	return ret;
}

