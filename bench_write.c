#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

double ts2d(struct timespec *s) {
	double val=(double)s->tv_sec * 1e9 + (double)s->tv_nsec;
	return val;
}

#define SYNC 0

int main(int argc, char *argv[]) {
	size_t totalsize;
	int MB=10,n=0, bufsize=4096;
	
	size_t i;
	struct timespec ts,te;
	//get params and init buffer
	if (argc>1)
		bufsize=atoi(argv[1]);
	if (argc>2)
		MB=atoi(argv[2]);
	totalsize = MB*1000*1000;
	char *c=(char *)malloc(bufsize);
	memset(c,42,bufsize);
	//write to file
#if !(SYNC)
	FILE *f=fopen("testout.txt","wb");
#else
	int fd=open("testout.txt",O_CREAT | O_RDWR | O_SYNC, S_IRUSR|S_IWUSR);
	if (fd == -1) {
		printf("Could not open file!\n");
		exit(1);
	}
	FILE *f=fdopen(fd,"wb"); 
	if (f == NULL) {
		printf("fdopen failed.\n");
		exit(2);
	} 
#endif
	clock_gettime(CLOCK_MONOTONIC,&ts);
	int written=0;
	for (i=0 ; i<totalsize; i+=bufsize) {
		fwrite(c,bufsize,1,f);
		fflush(f);
		n++;
	}
	fclose(f);
#if (SYNC)
	close(fd);
#endif
	clock_gettime(CLOCK_MONOTONIC,&te);
	//spit out info
	double time_taken=(double)(ts2d(&te) - ts2d(&ts)) / 1e6;
	double mbps=(double)MB*1000/(time_taken);
	printf("fwrite[%d/%dMB],%.2f ms,%.2f MB/s, %d blocks, %d ret\n",bufsize,MB,time_taken,mbps,n,written);
	
	return 0;
}

