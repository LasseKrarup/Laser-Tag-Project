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
#ifndef SONARDRIVER_H
    #define SONARDRIVER_H
    
//Denne driver skal have
    //En timer "Timer_Sonar"
    //En digital pin "Pin_Sonar_Echo"
    //En digital pin "Pin_Sonar_Trig"
    
#include <stdio.h>
#include "project.h"

    void sonarInit();
    uint16_t sonarGetPosition(void);
    
    
    
#endif
/* [] END OF FILE */
