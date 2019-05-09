#ifndef I2C_DRIVER_HPP
#define I2C_DRIVER_HPP

#include <MsgQueue.hpp>
#include <pthread.h>

class i2cDriver {
public:
  i2cDriver();
  void send(char &buf);
  char receive();
  unsigned char slaveRequest = 0;
  unsigned char dataReadyFlag = 0;

private:
  void i2cReaderEventHandler();
  void i2cSenderEventHandler();
  pthread_t i2cReaderThread;
  pthread_t i2cSenderThread;
  MsgQueue i2cReaderMsgQ;
  MsgQueue i2cSenderMsgQ;
};

#endif /*I2C_DRIVER_HPP*/
