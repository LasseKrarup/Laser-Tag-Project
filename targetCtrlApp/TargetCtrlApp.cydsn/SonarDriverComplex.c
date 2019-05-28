//SonarDriver


#include <stdio.h>
#include "project.h"

/*
uint32_t timerVal;
int sonarDistance;

CY_ISR(Sonar_Timer_isr_handler)
{
    timerVal = Timer_Sonar_ReadCounter();
    double hlTime = ((double)16777215 - (double)timerVal)/(double)24; //
    sonarDistance = hlTime / 58; //Omregning til cm
}

CY_ISR(sonar_echo_isr_handler)
{
    Timer_Sonar_
}

CY_ISR(sonar_echo_isr_not_handler)
{
    Timer_Sonar_SoftwareCapture();
    Timer_Sonar_Stop();
}
    
*/

void sonarStart()
{
    Pin_Sonar_Trig_Write(0);
    Timer_Sonar_Start();
    //isr_sonar_timer_StartEx(Sonar_Timer_isr_handler);
    //isr_sonar_echo_StartEx(sonar_echo_isr_handler);
    //isr_sonar_echo_StartEx(sonar_echo_isr_not_handler);
}

int sonarGetPosition()
{
    UART_Sonar_PutString("sonarGetPosition start\r\n");
    //Trig burst
    Pin_Sonar_Trig_Write(1);
    
    CyDelayUs(10); //10 us delay - måske som timer?
    Pin_Sonar_Trig_Write(0);
    //UART_Sonar_PutString("Before first while\r\n");
    //while(Pin_Sonar_Echo_Read() != 1){} // Vent - lav som interrupt?
    //UART_Sonar_PutString("Past first while\r\n");
    //Timer_Sonar_WriteCounter(16777215); //Max værdi - 24 bit

    //while(Pin_Sonar_Echo_Read() == 1){} // Vent - lav som interrupt?
    //UART_Sonar_PutString("Past second while\r\n");
    //uint32_t timerVal = Timer_Sonar_ReadCounter();

    //High level time: 16777215 / 699 us = 24, for omregning til us
    //double hlTime = ((double)16777215 - (double)timerVal)/(double)24; //
    //int sonarDistance = hlTime / 58; //Omregning til cm

    CyDelayUs(60000);
    //Datasheet anbefaler delay på 60 ms
    
    if(sonarDistance < 100 || sonarDistance > 0)
        return sonarDistance;
    else
        return 0;
}