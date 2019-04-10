/**
 * UserKitCtrl.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#ifndef USER_KIT_CTRL_H
#define USER_KIT_CTRL_H

#include "project.h"

void DMA_DelSig_Config()
{
    // Configure DMA   
}

void DMA_Filter_Config()
{
    // Configure DMA   
}
    
void init(int unitId)
{
    switch(unitId)
    {
        case 1:
            // Set transmitter frequency
            break;
        default:
            break;
    }
    
    // Start
    TIA_Start();
    Mixer_Start();
    ADC_DelSig_Start();
    ADC_DelSig_SetCoherency(ADC_DelSig_COHER_MID);
    ADC_DelSig_StartConvert();
    DMA_DelSig_Config();
    Filter_Start();
    Filter_SetCoherency(Filter_CHANNEL_A, Filter_KEY_HIGH);
    DMA_Filter_Config();
}

#endif /* USER_KIT_CTRL_H */ 
/* [] END OF FILE */