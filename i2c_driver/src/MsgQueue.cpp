#include <MsgQueue.hpp>
#include <pthread.h>

MsgQueue::MsgQueue(unsigned long maxSize) : maxSize(maxSize) {
  // printf("Constructor of MsgQueue\n");

  // printf("Init cond's\n");
  pthread_cond_init(&msgQueueNotFull, NULL);
  pthread_cond_init(&msgQueueNotEmpty, NULL);

  // printf("Init mut\n");
  pthread_mutex_init(&mutex, NULL);
};

MsgQueue::~MsgQueue() {
  pthread_cond_destroy(&msgQueueNotFull);
  pthread_cond_destroy(&msgQueueNotEmpty);
  pthread_mutex_destroy(&mutex);
}

void MsgQueue::send(unsigned long id, Message *msg) {
  pthread_mutex_lock(&mutex);
  while (msgContainer.size() >= maxSize) { // If container full
    printf("Queue is full, please wait\n");
    pthread_cond_wait(&msgQueueNotFull, &mutex);
  }

  MsgStruct item = {id = id, msg = msg};

  msgContainer.push(item);

  pthread_cond_signal(&msgQueueNotEmpty);
  pthread_mutex_unlock(&mutex);
}

Message *MsgQueue::receive(unsigned long &id) {
  pthread_mutex_lock(&mutex);
  while (msgContainer.size() == 0) { // If container empty
    // printf("Queue is empty, please wait\n");
    pthread_cond_wait(&msgQueueNotEmpty, &mutex); // some mutex
  }

  Message *tempMsg = msgContainer.front().msg; // access message
  id = msgContainer.front().id; // pass message id to the id reference

  msgContainer.pop(); //...and remove the message from the queue

  pthread_cond_signal(&msgQueueNotFull); // signal not full anymore

  pthread_mutex_unlock(&mutex);

  return tempMsg;
}