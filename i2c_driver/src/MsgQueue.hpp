#ifndef MSGQUEUE_HPP_
#define MSGQUEUE_HPP_

#include <Message.hpp>
#include <pthread.h>
#include <queue>
#include <stdio.h>
#include <string>

class MsgQueue {
public:
  MsgQueue(unsigned long maxSize);
  void send(std::string);
  Message *receive(unsigned long &id);
  ~MsgQueue();

private:
  std::queue<char> msgContainer;
  unsigned long maxSize = 0;
  pthread_cond_t msgQueueNotFull;
  pthread_cond_t msgQueueNotEmpty;
  pthread_mutex_t mutex;
};

#endif // !MSGQUEUE_HPP_