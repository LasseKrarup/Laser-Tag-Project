/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#include "project.h"

CY_ISR(isr_mixerFreq_handler)
{   
    Test_1_Write(!Test_1_Read());
}

int main(void)
{    
    Timer_mixerFreq_Start();
    
    isr_mixerFreq_StartEx(isr_mixerFreq_handler);   // Start mixerFreq isr
    
    CyGlobalIntEnable;  // Enable global interrupts
    
    for(;;)
    {
        // Empty loop
    }
}

/* [] END OF FILE */
