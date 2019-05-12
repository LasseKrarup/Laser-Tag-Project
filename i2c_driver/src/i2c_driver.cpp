#include <errno.h>
#include <fcntl.h>
#include <i2c_driver.hpp>
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>
#include <unistd.h>

/* i2cDriver() - Constructor of i2cDriver.
No parameters required.
It should not be necessary to join threads,
as it is unreachable code */
i2cDriver::i2cDriver(int slaveAddress) : i2cSenderMsgQ(MAX_MQ_SIZE) {
  pthread_create(&i2cSenderThread, NULL, i2cSenderEventHandler, (void *)this);
  pthread_create(&i2cReaderThread, NULL, i2cReaderEventHandler,
                 (void *)&i2cSenderMsgQ);

  // Set slave address
  int fd = open("/dev/i2c-1", O_RDONLY);
  if (ioctl(fd, I2C_SLAVE, slaveAddress) < 0) {
    printf("Error opening i2c-1: %d\n", errno);
  }
  close(fd);
}

/* Public methods */

/* Sends data to the i2cdev device driver */
void i2cDriver::send(char &buf) {
  i2cMessage *msg = new i2cMessage;

  msg->data = buf;

  i2cSenderMsgQ.send(I2C_SEND, msg);
}

/* Receive data from the data queue.
Only call this method if the data-ready flag is high.
You can check this flag with getDataReadyFlag() */
char i2cDriver::receive() {
  char data = dataQueue.front();
  dataQueue.pop();
  if (dataQueue.empty())
    dataReadyFlag = 0;
  return data;
}

/* Returns the data-ready flag */
unsigned char i2cDriver::getDataReadyFlag() { return dataReadyFlag; }

/* Event handler to the i2cReaderThread. This is automatically
called by the constructor and should never be called explicitly */
void *i2cDriver::i2cReaderEventHandler(void *arg) {
  MsgQueue *mqPtr = (MsgQueue *)arg;
  int fd = open("/dev/interrupt_module_dev", O_RDONLY);
  if (fd < 0) {
    printf("i2c_driver: Error in i2cReaderEventHandler. Errno: %d\n", errno);
  }

  while (1) {
    read(fd, NULL, 1);
    mqPtr->send(I2C_SLAVE_REQ);
  }

  close(fd);
}

/* Event handler to the i2cSenderThread. This is automatically
called by the constructor and should never be called explicitly */
void *i2cDriver::i2cSenderEventHandler(void *arg) {
  i2cDriver *thisPtr = (i2cDriver *)arg;

  unsigned long id;
  Message *msg;
  char byte;
  i2cMessage *i2cmsg;
  while (1) {
    msg = thisPtr->i2cSenderMsgQ.receive(id);

    switch (id) {
    case I2C_SLAVE_REQ:
      byte = thisPtr->i2cReceiveByte();
      thisPtr->dataQueue.push(byte);
      thisPtr->dataReadyFlag = 1;
      break;
    case I2C_SEND:
      i2cmsg = static_cast<i2cMessage *>(msg);
      thisPtr->i2cSendByte(i2cmsg->data);
      break;
    default:
      break;
    }

    delete msg;
  }
}

/* Lowest abstraction i2c send method. Do not call this method
explicitly, as it is used under the hood by i2cSenderEventHandler.
Use send() instead. */
void i2cDriver::i2cSendByte(char byte) {
  int fd = open("/dev/i2c-1", O_WRONLY);
  if (fd < 0) {
    printf("i2c_driver: Error in i2cSendByte. Errno: %d\n", errno);
  }
  if (write(fd, &byte, 1) < 0) {
    printf("i2c_driver: Error in reading from i2c-dev1. Errno: %d\n", errno);
  };
  close(fd);
}

/* Lowest abstraction i2c receive method. Do not call this method
explicitly, as it is used under the hood by i2cSenderEventHandler.
Use receive() instead. */
char i2cDriver::i2cReceiveByte() {
  char byte;
  int fd = open("/dev/i2c-1", O_RDONLY);
  if (fd < 0) {
    printf("i2c_driver: Error in i2cReceiveByte. Errno: %d\n", errno);
  }
  if (read(fd, &byte, 1) < 0) {
    printf("i2c_driver: Error in reading from i2c-dev1. Errno: %d\n", errno);
  };
  close(fd);

  return byte;
}