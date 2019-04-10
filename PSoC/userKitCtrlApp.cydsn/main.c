/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#include "project.h"
#include "UserKitCtrl.h"
#include "UserKitComUnitIF.h"
#include "Transmitter.h"
#include "Receiver.h"

static const int userKitID = 1; // Hardcoded UserKitId

static const float minLevelDetection = 2.5; // Minimum level for detection
int currentLaserID = 0;                     // Holds current laser id
int filterOutput = 0;                       // Holds last output from filter

CY_ISR_PROTO(isr_filter_handler);
CY_ISR_PROTO(isr_trigger_handler);
CY_ISR_PROTO(isr_triggerBlocking_handler);

int main(void)
{
    init(userKitID);   // Initialize to UserKit
    
    isr_filter_StartEx(isr_filter_handler);                     // Start filter isr
    isr_trigger_StartEx(isr_trigger_handler);                   // Start trigger isr
    isr_triggerBlocking_StartEx(isr_triggerBlocking_handler);   // Start triggerBlocking isr
    CyGlobalIntEnable;                                          // Enable global interrupts

    for(;;)
    {
        // Empty loop
    }
}

CY_ISR(isr_filter_handler)
{
       
}

CY_ISR(isr_trigger_handler)
{
    // Disable trigger interrupts
    startTransmitting();
    // Start triggerBlocking timer
}

CY_ISR(isr_triggerBlocking_handler)
{
    // Stop triggerBlocking timer
    stopTransmitting();
    // Enable trigger interrupts
}

/* [] END OF FILE */
