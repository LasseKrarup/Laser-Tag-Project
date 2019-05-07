/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 1.0
 */

#include "project.h"
#include "UserKitCtrl.h"
#include "ComUnitIF.h"
#include "Receiver.h"
#include "Transmitter.h"

#define USER_KIT_ID 1   // UserKitID changed by programmer (1-10)

static const uint8 userKitID = USER_KIT_ID-1;   // Hardcoded UserKitId 0-9
static const float minLevelDetection = 1.0;     // Minimum level for detection in Volt
float filterOutputVolt = 0;                     // Holds filter output in Volt
uint8 currentLaserID = 0;                       // Holds current laser id 0-9

CY_ISR_PROTO(isr_filter_handler);           // Interrupt handling filter output
CY_ISR_PROTO(isr_mixerFreq_handler);        // Interrupt handling change of mixer frequency
CY_ISR_PROTO(isr_trigger_handler);          // Interrupt handling trigger
CY_ISR_PROTO(isr_triggerBlocking_handler);  // Interrupt handling blocking of trigger

int main(void)
{
    CyGlobalIntEnable;  // Enable global interrupts
    
    initUserKitCtrl(userKitID); // Initialize UserKitCtrl
    initComUnitIF(userKitID);   // Initialize ComUnitIF
    
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
    
    if ((filterOutputVolt > minLevelDetection || filterOutputVolt < -minLevelDetection) && currentLaserID != userKitID) // Cannot shoot yourself
    {
        sendHitInd(currentLaserID);    // Send hit indication to ComUnit
        receiverHit();
    }
}

CY_ISR(isr_mixerFreq_handler)
{
    currentLaserID = changeMixerFrequency(currentLaserID);  // Change mixer frequency
}

CY_ISR(isr_trigger_handler)
{
    startTransmitting();
}

CY_ISR(isr_triggerBlocking_handler)
{
    stopTransmitting();
}

/* [] END OF FILE */
