/* ========================================
 *
 * Copyright YOUR COMPANY, THE YEAR
 * All Rights Reserved
 * UNPUBLISHED, LICENSED SOFTWARE.
 *
 * CONFIDENTIAL AND PROPRIETARY INFORMATION
 * WHICH IS THE PROPERTY OF your company.
 *
 * ========================================
*/


#include <stdio.h>
#include "project.h"
#include "SonarDriver.h"

void sonarInit()
{
    Pin_Sonar_Trig_Write(0);
    Timer_Sonar_Start();
}

uint16_t sonarGetPosition(void)
{
    //Trig burst
    Pin_Sonar_Trig_Write(1);
    
    CyDelayUs(10); //10 us delay
    Pin_Sonar_Trig_Write(0);
    while(Pin_Sonar_Echo_Read() != 1)
    {
        
    }

    Timer_Sonar_WriteCounter(16777216); //Max værdi - 24 bit
    
    while(Pin_Sonar_Echo_Read() == 1){}

    uint32_t timerVal = Timer_Sonar_ReadCounter();

    return timerVal - 16756000;
}


/* [] END OF FILE */
