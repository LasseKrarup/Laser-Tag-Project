/**
 * Receiver.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 1.0
 */

#ifndef RECEIVER_H
#define RECEIVER_H
    
#include "project.h"

uint16 clockDividerLO[] = {1188, 1081, 992, 916, 851, 795, 745, 702, 633, 628};   // Clock devider for frequencies 20.2 kHz to 38.2 kHz with 2 kHz steps
    
int changeMixerFrequency(int currentLaserID)
{
    switch(currentLaserID)
    {
        case 0:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 1:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 2:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 3:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 4:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 5:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 6:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 7:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 8:
            currentLaserID++;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        case 9:
            currentLaserID = 0;
            Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
            break;
        default:
            break;
    }
        
    return currentLaserID;
}

#endif /* RECEIVER_H */ 
/* [] END OF FILE */
