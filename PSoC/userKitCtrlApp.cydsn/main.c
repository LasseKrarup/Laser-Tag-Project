/**
 * main.c
 * 
 * @author  Frederik Sidenius Dam
 * @version 0.1
 */

#include "project.h"
#include "UserKitCtrl.h"

static const int unitId = 1;    // Hardcoded UserKitId
int currentLaserId = 0; // Holds current laser id

int main(void)
{
    init(unitId);   // Initialize to UserKit
    
    CyGlobalIntEnable; // Enable global interrupts

    for(;;)
    {
        /* Place your application code here. */
    }
}

/* [] END OF FILE */
