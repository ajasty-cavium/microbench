#ifndef _STATS_H_
#define _STATS_H_

#include <sys/time.h>

#define _POW 1.08
#define BINS 200

void init_bins(uint64_t *bins);
double tv_to_double(struct timeval *tv);
double double_time();
void sample(double s);
void sample_private(double s, double *sum, double *sum_sq, uint64_t *bins);
void sample_aggregate(double sum, double sum_sq, uint64_t *bins);
void print_header(int newline);
void print_stats(const char *tag, int newline);
void print_stats_private(const char *tag, int newline, 
	double sum, double sum_sq, uint64_t *bins);

#endif
