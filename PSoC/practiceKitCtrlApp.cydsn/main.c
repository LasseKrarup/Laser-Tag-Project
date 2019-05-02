/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 1.0
 */

#include "project.h"
#include "PracticeKitCtrl.h"
#include "PracticeKitComUnitIF.h"
#include "Receiver.h"

#define FILTER_TAPS 2   // Number of filter taps

static const float minLevelDetection = 1.0; // Minimum level for detection in Volt
float filterOutputVolt = 0;                 // Holds filter output in Volt
uint16 currentLaserID = 0;                  // Holds current laser id 0-9

CY_ISR_PROTO(isr_filter_handler);       // Interrupt handling filter output
CY_ISR_PROTO(isr_mixerFreq_handler);    // Interrupt handling change of mixer frequency

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
    filterOutputVolt = ADC_DelSig_CountsTo_Volts(filterOutput); // Convert filter output to volts
    
    if (filterOutputVolt > minLevelDetection || filterOutputVolt < -minLevelDetection)
    {
        receiverHit(currentLaserID);    // Reciever is hit
        //CyDelay(5000);                  // Blocking sleep
    }
}

CY_ISR(isr_mixerFreq_handler)
{
    currentLaserID = changeMixerFrequency(currentLaserID);  // Change mixer frequency
    
    ADC_DelSig_StopConvert();   // Stop converting
    for (size_t i = 0; i < FILTER_TAPS; i++)
        Filter_Write24(Filter_CHANNEL_A, 0);    // Reset filter with zeros
    ADC_DelSig_StartConvert();  // Start converting
}

/* [] END OF FILE */
