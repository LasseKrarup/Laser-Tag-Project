/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 1.0
 */

#include "project.h"
#include "UserKitCtrl.h"
#include "UserKitComUnitIF.h"
#include "Receiver.h"
#include "Transmitter.h"

#define FILTER_TAPS 2   // Number of filter taps

static const int userKitID = 1;             // Hardcoded UserKitId
static const float minLevelDetection = 1.0; // Minimum level for detection in volts
float filterOutputVolt = 0;                 // Holds filter output in voltage
uint16 currentLaserID = 0;                  // Holds current laser id

CY_ISR_PROTO(isr_filter_handler);           // Interrupt handling filter output
CY_ISR_PROTO(isr_mixerFreq_handler);        // Interrupt handling change of mixer frequency
CY_ISR_PROTO(isr_trigger_handler);          // Interrupt handling trigger
CY_ISR_PROTO(isr_triggerBlocking_handler);  // Interrupt handling blocking of trigger

int main(void)
{
    CyGlobalIntEnable;  // Enable global interrupts
    
    init(userKitID);   // Initialize UserKit
    // Send userKitID to Rpi - eg. in init
    
    isr_filter_StartEx(isr_filter_handler);                     // Start filter isr
    isr_mixerFreq_StartEx(isr_mixerFreq_handler);               // Start mixerFreq isr
    isr_trigger_StartEx(isr_trigger_handler);                   // Start trigger isr
    isr_triggerBlocking_StartEx(isr_triggerBlocking_handler);   // Start triggerBlocking isr
    
    for(;;)
    {
        // Empty loop
    }
}

CY_ISR(isr_filter_handler)
{
    filterOutputVolt = ADC_DelSig_CountsTo_Volts(filterOutput); // Convert filter output to volts
    
    if ((filterOutputVolt > minLevelDetection) || (filterOutputVolt < -minLevelDetection))
    {
        receiverHit(currentLaserID);    // Reciever is hit
        
        PWM_hitIndicator_Start();       // Start hit indication
        CyDelay(5000);                  // Blocking sleep
        PWM_hitIndicator_Stop();        // Stop hit indication
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
