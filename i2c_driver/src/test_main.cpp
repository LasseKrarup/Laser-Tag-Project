#include <i2c_driver.hpp>

int main(void) {
  i2cDriver i2cdriver(0x48);

  /* Test send */
  char ch = '1';
  i2cdriver.send(ch);

  /* Test i2cReceiveByte */
  printf("i2cReceiveByte: %c\n", i2cdriver.i2cReceiveByte());

  /* Test receive */
  while (!i2cdriver.getDataReadyFlag())
    ;
  printf("Data received from slave: %c\n", i2cdriver.receive());

  printf("End of program\n");

  return 0;
}