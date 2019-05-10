#include <errno.h>
#include <fcntl.h>
#include <i2c_driver.hpp>
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>

/* i2cDriver() - Constructor of i2cDriver.
No parameters required.
It should not be necessary to join threads,
as it is unreachable code */
i2cDriver::i2cDriver(int slaveAddress) : i2cSenderMsgQ(MAX_MQ_SIZE) {
  pthread_create(&i2cSenderThread, NULL, i2cSenderEventHandler, NULL);
  pthread_create(&i2cReaderThread, NULL, i2cReaderEventHandler, NULL);

  // Set slave address
  int fd = open("/dev/i2c-1", O_RDONLY);
  if (ioctl(fd, I2C_SLAVE, slaveAddress) < 0) {
    printf("Error: %d", errno);
  }
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
  return data;
}

/* Returns the data-ready flag */
unsigned char i2cDriver::getDataReadyFlag() { return dataReadyFlag; }

/* Event handler to the i2cReaderThread. This is automatically
called by the constructor and should never be called explicitly */
void *i2cDriver::i2cReaderEventHandler(void *) {
  // while (true)
  //// Open /dev/interrupt_module_dev

  //// Read from interrupt module (blocking)

  //// Close /dev/interrupt_module_dev

  //// send I2C_SLAVE_REQ to SenderMsgQueue
}

/* Event handler to the i2cSenderThread. This is automatically
called by the constructor and should never be called explicitly */
void *i2cDriver::i2cSenderEventHandler(void *) {
  // While true
  //// Read from SenderMsgQueue

  //// switch(ID)
  ////// case I2C_SLAVE_REQ
  //////// i2cReceiveByte()
  //////// Place data in dataqueue
  //////// Set data ready flag high
  ////// case I2C_SEND
  //////// i2cSendByte()

  //// Delete msg
}

/* Lowest abstraction i2c send method. Do not call this method
explicitly, as it is used under the hood by i2cSenderEventHandler.
Use send() instead. */
void i2cDriver::i2cSendByte(char byte) {
  //////// open i2cdev
  //////// Write to i2cdev
  //////// close i2cdev
}

/* Lowest abstraction i2c receive method. Do not call this method
explicitly, as it is used under the hood by i2cSenderEventHandler.
Use receive() instead. */
void i2cDriver::i2cReceiveByte() {
  //////// Open i2cdev
  //////// Read from i2cdev
  //////// close i2cdev
}