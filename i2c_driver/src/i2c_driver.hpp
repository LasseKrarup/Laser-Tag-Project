#ifndef I2C_DRIVER_HPP
#define I2C_DRIVER_HPP

#include <MsgQueue.hpp>
#include <errno.h>
#include <fcntl.h>
#include <linux/i2c-dev.h>
#include <pthread.h>
#include <queue>
#include <string.h>
#include <sys/ioctl.h>
#include <unistd.h>

#define MAX_MQ_SIZE 10

class i2cDriver {
public:
  i2cDriver(int slaveAddress);
  ~i2cDriver();
  void send(char &buf);
  char receive();
  unsigned char getDataReadyFlag();
  char i2cReceiveByte();

private:
  std::queue<char> dataQueue;
  static void *i2cReaderEventHandler(void *);
  static void *i2cSenderEventHandler(void *);
  pthread_t i2cReaderThread;
  pthread_t i2cSenderThread;
  MsgQueue i2cSenderMsgQ;
  void i2cSendByte(char byte);
  unsigned char dataReadyFlag = 0;
  int slaveAddress = 0;
};

struct i2cMessage : public Message {
  char data;
};

enum {
  I2C_SLAVE_REQ, // A slave requests the master to read from it
  I2C_SEND
}; // Msg ID's

#endif /*I2C_DRIVER_HPP*/
