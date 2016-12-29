#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <signal.h>
#define BACKLOG 20

struct sigaction sigchld_action = {
	.sa_handler = SIG_DFL,
	.sa_flags = SA_NOCLDWAIT
};


int main(int argc, char *argv[])
{
	sigaction(SIGCHLD, &sigchld_action, NULL);
	char client_ip[20];
	uint32_t client_port;

	int sockfd, cli_sockfd;
	socklen_t clilen;
	char buffer[100];
	struct sockaddr_in cli_addr;
	struct addrinfo hints, *serv;
	int pid, pid2;
	int n;

	memset(&hints, 0, sizeof(struct addrinfo));
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE;

	if((n = getaddrinfo(NULL, argv[1], &hints, &serv)) != 0){
		fprintf(stderr, "getaddrinfo error: %s\n", gai_strerror(n));
		exit(1);
	}
		
	if((sockfd = socket(serv->ai_family, serv->ai_socktype, serv->ai_protocol)) < 0){
		fprintf(stderr, "socket error\n");
		exit(1);
	}

	if(bind(sockfd, serv->ai_addr, serv->ai_addrlen) < 0){
		fprintf(stderr, "bind error\n");
		exit(1);
	}

	listen(sockfd, BACKLOG);
	clilen = sizeof(cli_addr);

	while(1){
		if((cli_sockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen)) < 0){
			fprintf(stderr, "accept error\n");
			exit(1);
		}
		
		if((pid = fork()) < 0){
			close(cli_sockfd);
			continue;
		} else if(pid > 0){
			close(cli_sockfd);
			continue;
		} else{
				inet_ntop(AF_INET, &(cli_addr.sin_addr), client_ip, INET_ADDRSTRLEN);
				client_port = ntohs(cli_addr.sin_port);
				
				while(1){

					memset(buffer, 0, sizeof(buffer));

					if((n = read(cli_sockfd, buffer, 100)) < 0){
						fprintf(stderr, "read error\n");
						exit(1);
					}

					printf("recv from %s:%ld, msg = %s\n", client_ip, client_port, buffer);
				}
				close(cli_sockfd);
				exit(0);
		}
	}
	close(sockfd);
	return 0; 
}
