/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 1.0
 */

#include "project.h"
#include "PracticeKitCtrl.h"
#include "ComUnitIF.h"
#include "Receiver.h"

static const float minLevelDetection = 1.0; // Minimum level for detection in Volt
float filterOutputVolt = 0;                 // Holds filter output in Volt
uint8 currentLaserID = 0;                  // Holds current laser id 0-9

CY_ISR_PROTO(isr_filter_handler);       // Interrupt handling filter output
CY_ISR_PROTO(isr_mixerFreq_handler);    // Interrupt handling change of mixer frequency

int main(void)
{
    CyGlobalIntEnable;  // Enable global interrupts
    
    initPracticeKitCtrl(); // Initialize PracticeKitCtrl
    
    isr_filter_StartEx(isr_filter_handler);         // Start filter isr
    isr_mixerFreq_StartEx(isr_mixerFreq_handler);   // Start mixerFreq isr
    
    for(;;)
    {
        // Empty loop
    }
}

CY_ISR(isr_filter_handler)
{
    filterOutputVolt = ADC_DelSig_CountsTo_Volts(filterOutput); // Convert filter output to volts
    
    if (filterOutputVolt > minLevelDetection || filterOutputVolt < -minLevelDetection)
    {
        sendHitInd(currentLaserID);    // Reciever is hit
    }
}

CY_ISR(isr_mixerFreq_handler)
{
    currentLaserID = changeMixerFrequency(currentLaserID);  // Change mixer frequency
}

/* [] END OF FILE */
