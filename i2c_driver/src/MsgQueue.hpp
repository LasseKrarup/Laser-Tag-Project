#ifndef MSGQUEUE_HPP_
#define MSGQUEUE_HPP_

#include <Message.hpp>
#include <pthread.h>
#include <queue>
#include <stdio.h>

class MsgQueue {
public:
  MsgQueue(unsigned long maxSize);
  void send(unsigned long id, Message *msg = NULL);
  Message *receive(unsigned long &id);
  ~MsgQueue();

private:
  struct MsgStruct {
    unsigned long id;
    Message *msg;
  } msgStruct;
  std::queue<struct MsgStruct> msgContainer;
  unsigned long maxSize = 0;
  pthread_cond_t msgQueueNotFull;
  pthread_cond_t msgQueueNotEmpty;
  pthread_mutex_t mutex;
};

#endif // !MSGQUEUE_HPP_