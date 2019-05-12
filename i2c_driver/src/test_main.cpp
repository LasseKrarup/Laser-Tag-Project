#include <i2c_driver.hpp>

int main(void) {
  i2cDriver i2cdriver(0x8);

  char ch = '1';
  i2cdriver.send(ch);

  return 0;
}