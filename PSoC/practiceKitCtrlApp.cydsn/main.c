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

static const float minLevelDetection = 2.5; // Minimum level for detection
int currentLaserID = 0;                     // Holds current laser id
int filterOutput = 0;                       // Holds last output from filter

CY_ISR_PROTO(isr_filter_handler);

int main(void)
{
    init();   // Initialize to PracticeKit
    
    isr_filter_StartEx(isr_filter_handler); // Start filter isr
    CyGlobalIntEnable;                      // Enable global interrupts

    for(;;)
    {
        // Empty loop
    }
}

CY_ISR(isr_filter_handler)
{
       
}

/* [] END OF FILE */
