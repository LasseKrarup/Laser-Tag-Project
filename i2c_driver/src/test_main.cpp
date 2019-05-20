#include <i2c_driver.hpp>
#define ADDRESS 0x08

int main(void) {
  i2cDriver i2cdriver(ADDRESS);

  printf("Starting test program\n");
  printf("Slave address: 0x%02x\n", ADDRESS);

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