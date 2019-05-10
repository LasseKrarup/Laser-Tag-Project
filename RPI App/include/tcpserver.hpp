#ifndef TCPSERVER_HPP
#define TCPSERVER_HPP

#include <pthread.h>
#include <sys/socket.h>
#include <iostream>
#include <netinet/in.h>
#include <netinet/tcp.h>  
#include <queue>
#include <string>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

class tcpserver
{
private:
    // data
    static pthread_mutex_t *queue_lock;
    static pthread_cond_t *queue_sig;
    static std::queue<std::string> *_queue;
    static int _socket;
    static bool active;

    //functions
    void connect();
    static void *recievethread(void *);
    static void *sendthread(void *);

public:
    tcpserver(pthread_mutex_t *, std::queue<std::string> *, pthread_cond_t *);
    static void *start(void *);
    ~tcpserver();
};

#endif