/**
 * UserKitComUnitIF.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#ifndef USER_KIT_COM_UNIT_IF_H
#define USER_KIT_COM_UNIT_IF_H

#include "project.h"
    
void receiverHit(int currentLaserID)
{
    switch(currentLaserID)
    {
        case 0:
            Pin_0_Write(~Pin_0_Read());
            break;
        case 1:
            Pin_1_Write(~Pin_1_Read());
            break;
        case 2:
            Pin_2_Write(~Pin_2_Read());
            break;
        case 3:
            Pin_3_Write(~Pin_3_Read());
            break;
        case 4:
            Pin_4_Write(~Pin_4_Read());
            break;
        default:
            break;
    }
}

#endif /* USER_KIT_COM_UNIT_IF_H */ 
/* [] END OF FILE */
