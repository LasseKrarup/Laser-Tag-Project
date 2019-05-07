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
    
void initComUnitIF(uint8 userKitID)
{
    I2C_req_Write(1);   // Request I2C from ComUint (the I2C Master)
    
    userKitID = userKitID + 1;  // Add offset so userKitID is from 1-10
    userKitID = userKitID + 48; // Add ASCII offset to get the chars '1'-'10'
    
    i2cbuf[0] = userKitID;  // Write userKitID to i2c buffer
    
    I2C_req_Write(0);
}
    
void sendHitInd(uint8 currentLaserID)
{
    I2C_req_Write(1);   // Request I2C from ComUint (the I2C Master)
    
    currentLaserID = currentLaserID + 1;  // Add offset so currentLaserID is from 1-10
    currentLaserID = currentLaserID + 48; // Add ASCII offset to get the chars '1'-'10'
    
    i2cbuf[0] = currentLaserID;  // Write currentLaserID to i2c buffer
    
    I2C_req_Write(0);
}

#endif /* COM_UNIT_IF_H */ 
/* [] END OF FILE */
