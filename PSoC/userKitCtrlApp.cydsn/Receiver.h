/**
 * Receiver.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 1.0
 */

#ifndef RECEIVER_H
#define RECEIVER_H
    
#include "project.h"
    
#define FILTER_TAPS 2   // Number of filter taps

uint16 clockDividerLO[] = {1188, 1081, 992, 916, 851, 795, 745, 702, 633, 628};   // Clock devider for frequencies 20.2 kHz to 38.2 kHz with 2 kHz steps

void receiverHit(void)
{
    transmit_clock_Stop();          // Stop transmitting
        
    PWM_hitIndicator_Start();   // Start hit indication
    CyDelay(5000);              // Blocking sleep
    PWM_hitIndicator_Stop();    // Stop hit indication
}

int changeMixerFrequency(int currentLaserID)
{
    if (currentLaserID == 9)
        currentLaserID = 0; // Reset value
    else
        currentLaserID++;
    
    Clock_LO_SetDividerValue(clockDividerLO[currentLaserID]);
    
    ADC_DelSig_StopConvert();   // Stop converting
    for (size_t i = 0; i < FILTER_TAPS; i++)
        Filter_Write24(Filter_CHANNEL_A, 0);    // Reset filter with zeros
    ADC_DelSig_StartConvert();                  // Start converting
    
    return currentLaserID;
}

#endif /* RECEIVER_H */ 
/* [] END OF FILE */
