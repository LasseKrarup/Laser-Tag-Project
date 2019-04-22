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

static const float minLevelDetection = 1.0;   // Minimum level for detection
float filterOutputVolt = 0;                 // Holds filter output in voltage
uint16 currentLaserID = 0;                  // Holds current laser id

int test = 0;
int test2 = 0;
int test3 = 0;

CY_ISR_PROTO(isr_filter_handler);
CY_ISR_PROTO(isr_mixerFreq_handler);

int main(void)
{
    CyGlobalIntEnable;  // Enable global interrupts
    
    init(); // Initialize PracticeKit
    
    isr_filter_StartEx(isr_filter_handler);         // Start filter isr
    isr_mixerFreq_StartEx(isr_mixerFreq_handler);   // Start mixerFreq isr
    
    for(;;)
    {
        // Empty loop
    }
}

CY_ISR(isr_filter_handler)
{
    filterOutputVolt = ADC_DelSig_CountsTo_Volts(filterOutput);
    test++;
    
    if ((filterOutputVolt > minLevelDetection) | (filterOutputVolt < -minLevelDetection))
    {
        test2++;
        // Disable interrupt
        // receiverHit(currentLaserID)
        // Evt. sleep???
        // Evt. clear pending interrupts
        // Enable interrupts         
    }
}

CY_ISR(isr_mixerFreq_handler)
{ 
    test3++;
    currentLaserID = changeMixerFrequency(currentLaserID);
    // Reset ADC og filter?
}

/* [] END OF FILE */
