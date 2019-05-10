#include "tcpserver.hpp"
#include <fstream>
#define DEVICE

int main(){
    pthread_cond_t cond;
    pthread_mutex_t mutex;
    pthread_cond_init(&cond, NULL);
    pthread_mutex_init(&mutex, NULL);
    std::queue<std::string> messages;
    tcpserver server(&mutex, &messages, &cond);
    pthread_t tcpthread;
    pthread_create(&tcpthread, NULL, server.start, NULL);
    while(true){
        unsigned int data;
        std::string input;
        std::cin >> input ;
        pthread_mutex_lock(&mutex);
        messages.push(input);
        pthread_mutex_unlock(&mutex);
        pthread_cond_signal(&cond);
    }
    return 0;
}