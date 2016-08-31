#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <inttypes.h>
#include <math.h>
#include <time.h>

#include "stats.h"

static uint64_t gbins[BINS];
static double gsum=0,gsum_sq=0;
static int details[]={5,10,50,67,75,80,85,90,95,99,999,9999};
static int ndetails=sizeof(details)/sizeof(int);

uint64_t total(uint64_t *bins) {
	uint64_t ret=0;
	int i;
	if (bins == NULL)
		bins=gbins;
	for (i=0; i<BINS ; i++) {
		ret += bins[i];
	}
	return ret;
}

void init_bins(uint64_t *bins) {
	int i;
    if (bins == NULL)
        bins=gbins;
	for (i=0; i<BINS ; i++) {
		bins[i]=0;
	}
}

double tv_to_double(struct timeval *tv) {
	return ( (double)tv->tv_sec * 1e6 + (double)tv->tv_usec );
}
double ts_to_double(struct timespec *ts) {
	return ( (double)ts->tv_sec * 1e9 + (double)ts->tv_nsec );
}

double double_time() {
	struct timespec timer_cur;
	clock_gettime(CLOCK_REALTIME, &timer_cur);
	return ts_to_double(&timer_cur);
}

double average(double sum, uint64_t *bins) {
	return sum / total(bins);
}
double stddev(double sum, double sum_sq, uint64_t *bins) {
	return sqrt(sum_sq / total(bins) - pow(sum / total(bins), 2.0));
}

void sample_private(double s, double *sum, double *sum_sq, uint64_t *bins) {
	int bin = log(s)/log(_POW);

	if ( bin < 0) {
	  bin = 0;
	} 
	if (bin >= BINS) {
	  bin = BINS - 1;
	}

	*sum += s;
	*sum_sq += s*s;

	bins[bin]++;
}

void sample(double s) {
	int bin = log(s)/log(_POW);

	if ( bin < 0) {
	  bin = 0;
	} 
	if (bin >= BINS) {
	  bin = BINS - 1;
	}

	gsum += s;
	gsum_sq += s*s;

	gbins[bin]++;
}

void sample_aggregate(double sum, double sum_sq, uint64_t *bins) {
	int i;

	gsum += sum;
	gsum_sq += sum_sq;
		
	for (i=0; i<BINS; i++) 
		gbins[i] += bins[i];

}

void print_header(int newline) {
	int i;
	printf("%-7s %7s %7s %7s",
		   "#type", "avg", "std", "min");
	for (i=0; i<ndetails; i++) {
		char buf[8];
		sprintf(buf,"p%d",details[i]); 
		printf(" %7s",buf);
	}
	if (newline) 
		printf("\n");
}

double get_nth(uint64_t *bins, double nth) {
	uint64_t count = total(bins);
	uint64_t n = 0;
	int i;
	double target;
    if (bins == NULL)
        bins=gbins;
	if (nth<1) { // <1 assume %
		target = count * nth;
	} else { // >1 assume 1-99 is pNth e.g. 99 is p99 (99%), 999 is 99.9% etc.
		target = count * nth/100;
		if (nth>100.0) {
			target = count * nth/1000;
		}
		if (nth>1000.0) {
			target = count * nth/10000;
		}
	}
	for (i = 0; i < BINS; i++) {
	  n += bins[i];

	  if (n > target) { // The nth is inside bins[i].
		double left = target - (n - bins[i]);
		return pow(_POW, (double) i) +
		  left / bins[i] * (pow(_POW, (double) (i+1)) - pow(_POW, (double) i));
	  }
	}

	return pow(_POW, BINS);
} 

void print_stats(const char *tag, 
			   int newline) {
	print_stats_private(tag,newline,gsum,gsum_sq,gbins);
}
void print_stats_private(const char *tag, 
			   int newline, double sum, double sum_sq, uint64_t *bins) 
{
	int i;
    if (bins == NULL) {
    	bins=gbins;
	sum=gsum;
	sum_sq=gsum_sq;
	}
	if (total(bins) == 0) {
	  printf("%-7s",tag);
		for (i=0; i<ndetails+3; i++) {
			printf(" %7.1f",0.0);
		}
			 
	  if (newline) printf("\n");
	  return;
	}

	printf("%-7s %7.1f %7.1f %7.1f",
		   tag, average(sum,bins), stddev(sum,sum_sq,bins),
		   get_nth(bins,0));
		for (i=0; i<ndetails; i++) {
			printf(" %7.1f",get_nth(bins,details[i]));
		}

	if (newline) printf("\n");
}

