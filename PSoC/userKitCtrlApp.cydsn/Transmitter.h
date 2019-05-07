/**
 * Transmitter.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#ifndef TRANSMITTER_H
#define TRANSMITTER_H

#include "project.h"
    
void startTransmitting(void)
{
    transmit_clock_Start();         // Start transmitting
    Timer_triggerBlocking_Start();  // Start triggerBlocking timer
}

void stopTransmitting(void)
{
    Timer_triggerBlocking_Stop();   // Stop triggerBlocking timer
    transmit_clock_Stop();          // Stop transmitting
}

#endif /* TRANSMITTER_H */ 
/* [] END OF FILE */
