/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#include "project.h"
#include "PracticeKitCtrl.h"
#include "PracticeKitComUnitIF.h"
#include "Receiver.h"

static const int16 minLevelDetection = 2; // Minimum level for detection
uint16 currentLaserID = 0;                // Holds current laser id

CY_ISR_PROTO(isr_filter_handler);
CY_ISR_PROTO(isr_mixerFreq_handler);

int main(void)
{
    init();   // Initialize PracticeKit
    
    isr_filter_StartEx(isr_filter_handler);         // Start filter isr
    isr_mixerFreq_StartEx(isr_mixerFreq_handler);   // Start mixerFreq isr
    CyGlobalIntEnable;                              // Enable global interrupts

    for(;;)
    {
        // Empty loop
    }
}

CY_ISR(isr_filter_handler)
{
    if (filterOutput > minLevelDetection)
    {
        // Disable interrupt
        // receiverHit(currentLaserID)
        // Evt. sleep???
        // Evt. clear pending interrupts
        // Enable interrupts
    }  
}

/* [] END OF FILE */
