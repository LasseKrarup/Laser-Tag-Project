#ifndef I2C_DRIVER_HPP
#define I2C_DRIVER_HPP

#include <MsgQueue.hpp>
#include <pthread.h>
#include <queue>

class i2cDriver {
public:
  i2cDriver();
  void send(char &buf);
  char receive();
  unsigned char getDataReadyFlag();

private:
  std::queue<char> dataQueue;
  void i2cReaderEventHandler();
  void i2cSenderEventHandler();
  pthread_t i2cReaderThread;
  pthread_t i2cSenderThread;
  MsgQueue i2cReaderMsgQ;
  MsgQueue i2cSenderMsgQ;
  void i2cSendByte(char byte);
  void i2cReceiveByte();
  unsigned char slaveRequest = 0;
  unsigned char dataReadyFlag = 0;
};

struct i2cMessage : public Message {
  char data;
};

enum {
  I2C_SLAVE_REQ, // A slave requests the master to read from it
  I2C_SEND
}; // Msg ID's

#endif /*I2C_DRIVER_HPP*/
