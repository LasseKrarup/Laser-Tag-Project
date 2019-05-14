#include <i2c_driver.hpp>

int main(void) {
  i2cDriver i2cdriver(0x48);

  printf("Starting test program\n");

  /* Test send */
  char ch = '1';
  i2cdriver.send(ch);

  /* Test receive */
  while (!i2cdriver.getDataReadyFlag())
    ;
  printf("Data received from slave: 0x%02x\n", i2cdriver.receive());

  printf("End of program\n");

  return 0;
}