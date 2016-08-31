
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <resolv.h>
#include <sys/socket.h>
#include <sys/uio.h>
#include <netinet/in.h>
#include "stats.h"

int MSGS=1;
int MLEN=10;
int client_mode = 0;

#define panic(_m) panic_msg(_m,__FILE__,__LINE__)

void panic_msg(char *m, char *f, int n) 
{ 
	printf("[%s] %s:%d %s\n",
		client_mode ? "client":"server",
		f,n,m); 
	exit(1); 
}



//socket init for datagram
void quick_socket_init(int *sd, struct sockaddr_in *addr, int port, int type) {
	*sd = socket(PF_INET, type, 0);
	bzero(addr, sizeof(struct sockaddr_in));
	addr->sin_family = AF_INET;
	addr->sin_port = htons(port);
}

void quick_msg_init(struct msghdr *msg, struct sockaddr_in *addr , struct iovec *io) {
	bzero(msg, sizeof(struct msghdr));
	msg->msg_name = addr;
	msg->msg_namelen = sizeof(struct sockaddr_in);
	msg->msg_iov = io;
	msg->msg_iovlen = MSGS;
}

void prep_recv(int sd, struct sockaddr_in *addr, int type) {
	if (type == SOCK_STREAM) {
		int on=1;
		int rc = setsockopt(sd,
		   SOL_SOCKET,  SO_REUSEADDR,
		   (char *)&on, sizeof(on));
		if (rc < 0) {
			panic("setsockopt failed.");
		}
	}
	addr->sin_addr.s_addr = INADDR_ANY;
	if ( bind(sd, (struct sockaddr *)addr, sizeof(struct sockaddr_in)) != 0 ) {
		printf("Tried bind addr %x. ",addr->sin_addr.s_addr);
		panic("Can't bind address!");
	}
	if (type == SOCK_STREAM) {
		int rc = listen(sd, 5);
		if (rc < 0) {
			panic("listen failed.");
		}		
	}
}

int main(int argc, char *argv[])
{	int i,j, sd[2], bytes[2] = {0,0};
	char buffer[2][MSGS][MLEN];
	struct iovec io[2][MSGS];
	struct msghdr msg[2];
	struct sockaddr_in addr[2];
	char msgtail[MLEN-4];
	int num_tests = 10;
	int sock_type = SOCK_DGRAM;
	int accept_sd=0;
	
	setvbuf (stdout, NULL, _IONBF, BUFSIZ);
	
	for (i=0 ; i<argc ; i++) {
		if (*argv[i] == 'c') 
			client_mode = 1;
		if (*argv[i] == 't') 
			sock_type = SOCK_STREAM;
		if (*argv[i] == 'u') 
			sock_type = SOCK_DGRAM;
		if (*argv[i] == 's') 
			client_mode=0;
		if (*argv[i] == 'n') {
			i++;
			num_tests=atoi(argv[i]);
		}
		if (*argv[i] == 'm') {
			int msgs;
			i++;
			msgs=atoi(argv[i]);
			if (msgs < MSGS)
				MSGS=msgs;
			else
				printf("To use msgs %d, must build with MSGS at least %d.\n",msgs,msgs);
		}
		if (*argv[i] == 'M') {
			int mlen;
			i++;
			mlen=atoi(argv[i]);
			if (mlen < mlen)
				MLEN=mlen;
			else
				printf("To use mlen %d, must build with MLEN at least %d.\n",mlen,mlen);
		}
	}
	
	printf("Server starting.\n");
	//init the sockets 
	if (client_mode) {
		quick_socket_init(&(sd[0]),&(addr[0]),8080,sock_type);
		quick_socket_init(&(sd[1]),&(addr[1]),8081,sock_type);
	} else {
		quick_socket_init(&(sd[0]),&(addr[0]),8081,sock_type);
		quick_socket_init(&(sd[1]),&(addr[1]),8080,sock_type);
	}
	inet_aton(&(addr[0].sin_addr), "127.0.0.1");
	prep_recv(sd[1],&addr[1],sock_type);
	printf("Sockets initialized.\n");
	
	//init the msg buffers
	memset(msgtail,'x',MLEN-6);
	msgtail[MLEN-5]=0;
	for ( i = 0; i < MSGS; i++ )
	{
		snprintf(buffer[0][i],MLEN,"%04x%s",i,msgtail);
	}
	for ( i = 0; i < MSGS; i++ )
	{
		io[0][i].iov_base = buffer[0][i];
		io[0][i].iov_len = MLEN;
		io[1][i].iov_base = buffer[1][i];
		io[1][i].iov_len = MLEN;
	}
	
	quick_msg_init(&msg[0],&(addr[0]),io[0]);
	quick_msg_init(&msg[1],&(addr[1]),io[1]);
	
	//run the tests.
	//client will first send initial msg, triggering the server to attempt to connect to client.
	//Using 2 sockets to have separate buffers for send/recv.
	accept_sd = sd[1];
	if (client_mode) { // run for num_tests, send and recv, print stats and quit.
	//If sock stream, server will be in accept mode. Need to connect to it.
		int phase=0;
		if (sock_type == SOCK_STREAM) {
			if (connect(sd[0], (struct sockaddr *)&(addr[0]), sizeof(struct sockaddr_in)) < 0) {
				perror("- Connecting stream socket");
				panic("Cannot connect to server!");
			}
		}
		for ( j=0 ; j<num_tests ; j++) {
			double end_time, start_time = double_time();
			if ( (bytes[0] += sendmsg(sd[0], &msg[0], 0)) < 0 ) {
				perror("sendmsg");
				panic("Error in sendmsg, quit.");
			}
			if (phase == 0) { //First time around, wait for connection to establish
				if (sock_type == SOCK_STREAM) {
					accept_sd = accept(sd[1],NULL,NULL);
					phase=1;
				}
			}
			if ( (bytes[1] += recvmsg(accept_sd, &msg[1], 0)) < 0 ) {
				perror("recvmsg");
				break;
			}
			end_time = double_time();
			//ignore first RTT in SOCK_STREAM, since that include latency for establishing the connection.
			if (phase == 1) {
				phase=2;
			} else { 
				sample(end_time - start_time);
			}
		}
		sprintf(buffer[0][0],"%s","QUIT");
		msg[0].msg_iovlen=1;
		sendmsg(sd[0], &msg[0], 0);
	} else { //server, get msg, and return msg.
		int connected=0;
		printf("Server listening.\n");
		if (sock_type == SOCK_STREAM) {
			accept_sd = accept(sd[1],NULL,NULL);
		}
		while (1) {
			double end_time, start_time = double_time();
			if ( (bytes[1] += recvmsg(accept_sd, &msg[1], 0)) < 0 )
				perror("recvmsg");
			//If sock stream, server client will be in accept mode after sending first msg. Need to connect to it.
			if ((sock_type == SOCK_STREAM) && (!connected)) {
				if (connect(sd[0], (struct sockaddr *)&(addr[0]), sizeof(struct sockaddr_in)) < 0) {
					perror("- Connecting stream socket");
					panic("Cannot connect to server!");
				}
				connected=1;
			}

			if ( (bytes[0] += sendmsg(sd[0], &msg[0], 0)) < 0 )
				perror("sendmsg");
			
			end_time = double_time();
			sample(end_time - start_time);
			if (buffer[1][0][0] == 'Q') {
				break;
			}
		}
	}
	
	if ( (accept_sd != sd[1]) && (accept_sd != 0))
		close(accept_sd);	 
	close(sd[0]);
	close(sd[1]);

	printf("%d total bytes sent\n", bytes[0]);
	printf("%d total bytes recv\n", bytes[1]);
	print_header(1);
	print_stats("RTT(ns)",1);
	return 0;
}
