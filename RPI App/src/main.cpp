#include "tcpserver.hpp"
#include "i2c_driver.hpp"
#include <fstream>

int main(){
    i2cDriver i2c(1);

    pthread_cond_t cond;
    pthread_mutex_t mutex;
    pthread_cond_init(&cond, NULL);
    pthread_mutex_init(&mutex, NULL);
    std::queue<char> messages;
    tcpserver server(&mutex, &messages, &cond, &i2c);
    pthread_t tcpthread;
    pthread_create(&tcpthread, NULL, server.start, NULL);

    
    while(true){
        if(i2c.getDataReadyFlag() ==1){
            pthread_mutex_lock(&mutex);
            messages.push(i2c.receive());
            pthread_cond_signal(&cond);
            pthread_mutex_unlock(&mutex);
        }
    }
    return 0;
}