/**
 * UserKitCtrl.h
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#ifndef USER_KIT_CTRL_H
#define USER_KIT_CTRL_H

#include "project.h"

int16 filterOutput = 0;     // Holds last output from filter
    
void DMA_DelSig_Config()
{
    /* Variable declarations for DMA_DelSig */
    /* Move these variable declarations to the top of the function */
    uint8 DMA_DelSig_Chan;
    uint8 DMA_DelSig_TD[1];
    
    /* Defines for DMA_DelSig */
    #define DMA_DelSig_BYTES_PER_BURST 2
    #define DMA_DelSig_REQUEST_PER_BURST 1
    #define DMA_DelSig_SRC_BASE (CYDEV_PERIPH_BASE)
    #define DMA_DelSig_DST_BASE (CYDEV_PERIPH_BASE)

    /* DMA Configuration for DMA_DelSig */
    DMA_DelSig_Chan = DMA_DelSig_DmaInitialize(DMA_DelSig_BYTES_PER_BURST, DMA_DelSig_REQUEST_PER_BURST, 
        HI16(DMA_DelSig_SRC_BASE), HI16(DMA_DelSig_DST_BASE));
    DMA_DelSig_TD[0] = CyDmaTdAllocate();
    CyDmaTdSetConfiguration(DMA_DelSig_TD[0], 2, DMA_INVALID_TD, TD_INC_DST_ADR); // changed from CY_DMA_DISABLE_TD, CY_DMA_TD_INC_DST_ADR
    CyDmaTdSetAddress(DMA_DelSig_TD[0], LO16((uint32)ADC_DelSig_DEC_SAMP_PTR), LO16((uint32)Filter_STAGEAM_PTR));   // changed from STAGEA
    CyDmaChSetInitialTd(DMA_DelSig_Chan, DMA_DelSig_TD[0]);
    CyDmaChEnable(DMA_DelSig_Chan, 1);
}

void DMA_Filter_Config()
{
    /* Variable declarations for DMA_Filter */
    /* Move these variable declarations to the top of the function */
    uint8 DMA_Filter_Chan;
    uint8 DMA_Filter_TD[1];
    
    /* Defines for DMA_Filter */
    #define DMA_Filter_BYTES_PER_BURST 2
    #define DMA_Filter_REQUEST_PER_BURST 1
    #define DMA_Filter_SRC_BASE (CYDEV_PERIPH_BASE)
    #define DMA_Filter_DST_BASE (CYDEV_SRAM_BASE)

    /* DMA Configuration for DMA_Filter */
    DMA_Filter_Chan = DMA_Filter_DmaInitialize(DMA_Filter_BYTES_PER_BURST, DMA_Filter_REQUEST_PER_BURST, 
        HI16(DMA_Filter_SRC_BASE), HI16(DMA_Filter_DST_BASE));
    DMA_Filter_TD[0] = CyDmaTdAllocate();
    CyDmaTdSetConfiguration(DMA_Filter_TD[0], 2, DMA_INVALID_TD, DMA_Filter__TD_TERMOUT_EN | CY_DMA_TD_INC_SRC_ADR); // changed from CY_DMA_DISABLE_TD
    CyDmaTdSetAddress(DMA_Filter_TD[0], LO16((uint32)Filter_HOLDAM_PTR), LO16((uint32)&filterOutput));  // changed from HOLDA
    CyDmaChSetInitialTd(DMA_Filter_Chan, DMA_Filter_TD[0]);
    CyDmaChEnable(DMA_Filter_Chan, 1);
}
    
void init(uint16 unitId)
{
    switch(unitId)
    {
        case 0:
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
    
    Timer_mixerFreq_Start();
}

#endif /* USER_KIT_CTRL_H */ 
/* [] END OF FILE */
