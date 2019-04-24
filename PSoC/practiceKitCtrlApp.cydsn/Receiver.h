/**
 * Receiver.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#ifndef RECEIVER_H
#define RECEIVER_H
    
#include "project.h"

uint16 clockDevider[] = {1188, 1081, 992, 916, 851};
    
int changeMixerFrequency(int currentLaserID)
{
    switch(currentLaserID)
    {
        case 0:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDevider[currentLaserID]);
            break;
        case 1:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDevider[currentLaserID]);
            break;
        case 2:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDevider[currentLaserID]);
            break;
        case 3:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDevider[currentLaserID]);
            break;
        case 4:
            currentLaserID = 0;
            Clock_LO_SetDividerValue(clockDevider[currentLaserID]);
            break;
        default:
            break;
    }
        
    return currentLaserID;
}

#endif /* RECEIVER_H */ 
/* [] END OF FILE */
