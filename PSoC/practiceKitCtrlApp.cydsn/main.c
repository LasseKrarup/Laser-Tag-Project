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

#define FILTER_TAPS 2

static const float minLevelDetection = 1.0; // Minimum level for detection in volts
float filterOutputVolt = 0;                 // Holds filter output in voltage
uint16 currentLaserID = 0;                  // Holds current laser id

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
    
    if ((filterOutputVolt > minLevelDetection) | (filterOutputVolt < -minLevelDetection))
    {
        receiverHit(currentLaserID);
        CyDelay(5000);  // Blocking sleep in 5s
    }
}

CY_ISR(isr_mixerFreq_handler)
{
    currentLaserID = changeMixerFrequency(currentLaserID);
    
    ADC_DelSig_StopConvert();   // Stop converting
    for (size_t i = 0; i < FILTER_TAPS; i++)
        Filter_Write24(Filter_CHANNEL_A, 0);    // Reset filter with zeros
    ADC_DelSig_StartConvert();  // Start converting
}

/* [] END OF FILE */
