/**
 * ComUnitIF.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 1.0
 */

#ifndef COM_UNIT_IF_H
#define COM_UNIT_IF_H

#include "project.h"

uint8 i2cbuf[1];
    
void initComUnitIF()
{
    I2C_SlaveInitReadBuf(i2cbuf, 1);    // Sets the read buffer for i2c
}

void sendHitInd(uint8 currentLaserID)
{
    I2C_SlaveClearReadBuf();    // Clears i2c read buffer
    
    currentLaserID = currentLaserID + 48; // Add ASCII offset to get the chars '0'-'9'
    i2cbuf[0] = currentLaserID; // Write currentLaserID to i2c buffer
    
    I2C_req_Write(0);   // Request I2C from ComUint (the I2C Master)
}

#endif /* COM_UNIT_IF_H */ 
/* [] END OF FILE */
