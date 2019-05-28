/* ========================================
 *
 * Copyright YOUR COMPANY, THE YEAR
 * All Rights Reserved
 * UNPUBLISHED, LICENSED SOFTWARE.
 *
 * CONFIDENTIAL AND PROPRIETARY INFORMATION
 * WHICH IS THE PROPERTY OF your company.
 *
 * ========================================
*/
#include "project.h"
#include "SonarDriver.h"
#include "MotorControl.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h> //Afrunding af starttime
#include <time.h> //RNG
#include <stdbool.h> //Hold styr på stopGame signal

static uint16_t sampleTimeMilliseconds = 30;

char uartBuffer[256];

bool stopGame = false;

uint16_t startPosition = 12000;
uint16_t oldSetPoint = 12000;


CY_ISR_PROTO(ISR_UART_rx_handler);
CY_ISR_PROTO(isr_newPosition_handler);
CY_ISR_PROTO(isr_stopGame_handler);
void handleByteReceived(uint8_t byteReceived);
int generateNewSetpoint(void);
void initMain(void);

void initMain(void)
{
    sonarInit();
    UART_Sonar_Start();
    PWM_TimeOut_Start();
    motorControlInit(sampleTimeMilliseconds);
    ADC_DelSig_Start();
        
    //Interrupts
    isr_uart_rx_StartEx(ISR_UART_rx_handler);
    isr_stopGame_StartEx(isr_stopGame_handler);
    isr_newPosition_StartEx(isr_newPosition_handler);
    
    
    //Get ADC result for RNG
    ADC_DelSig_StartConvert();
    while(!ADC_DelSig_IsEndConversion(1)){}
    uint8_t seed = ADC_DelSig_GetResult8(); //Generate seed
    srand(seed);
    //Output seed
    snprintf(uartBuffer, sizeof(uartBuffer), "\r\nTHE SEED: %d \r\n", seed);
    UART_Sonar_PutString(uartBuffer);
}

int main(void)
{   
    initMain();
    
    uint16_t newSetPoint;
    

    uint16_t startRunTime = 1000; //Tid brugt på at finde startposition
    uint16_t startRunThroughs = round(startRunTime/sampleTimeMilliseconds);
    uint16_t startRestTime = 5000 - startRunTime; // Venter i alt fem sekunder før spil startes
    
    for(;;){
        stopGame = false;
        
        snprintf(uartBuffer, sizeof(uartBuffer), "\r\n PRESS SWITCH TO START GAME\r\n");
        UART_Sonar_PutString(uartBuffer);
        
        while(!Pin_StartGame_Read()){} //Vent på switch
        
        CyGlobalIntEnable; /* Enable global interrupts. */
        
        oldSetPoint = newSetPoint = startPosition;
        motorChangeSetPoint(startPosition);
        snprintf(uartBuffer, sizeof(uartBuffer), "\r\nReturning to default setpoint: %d \r\n", newSetPoint);
        UART_Sonar_PutString(uartBuffer);
        writeKillMotor(false);
        for(int i = 0; i < startRunThroughs; i++)
        {
            //uint16_t distance = sonarGetPosition();
            int distance = 15000; //Test uden sonar
            getPositionControl(distance);
            CyDelay(sampleTimeMilliseconds);
        }
        snprintf(uartBuffer, sizeof(uartBuffer), "\r\nWAITING %d SECONDS \r\n", startRestTime);
        UART_Sonar_PutString(uartBuffer);
        writeKillMotor(true);
        CyDelay(startRestTime);
        
        
        Timer_GameCounter_Start();
        motorChangeSetPoint(generateNewSetpoint());
        
        PWM_TimeOut_Start();
        
        writeKillMotor(false);
        while(!stopGame){
            uint16_t distance = sonarGetPosition();
            getPositionControl(distance);
            
            //uint16_t cmDist = 16.5-((distance/24.0)/58.0);
            //snprintf(uartBuffer, sizeof(uartBuffer), "\r\n cm: %d \r\n", cmDist);
            //UART_Sonar_PutString(uartBuffer);
            CyDelay(sampleTimeMilliseconds);
        }
        
        writeKillMotor(true);
        
        PWM_TimeOut_Stop();
        CyGlobalIntDisable; /* Disable global interrupts. */
        //Returner til midterposition og slut spil
        Timer_GameCounter_Stop();
        
        snprintf(uartBuffer, sizeof(uartBuffer), "\r\nGAME OVER: \r\n");
        UART_Sonar_PutString(uartBuffer);
    }
}

int generateNewSetpoint(void)           //Finder en ny position, som ikke er i nærheden af den nuværende
{
    uint16_t minDistance = 2000;
    uint16_t newSetPoint;
    snprintf(uartBuffer, sizeof(uartBuffer), "OLD SETPOINT: %d \r\n", oldSetPoint);
    UART_Sonar_PutString(uartBuffer);
    do {
        newSetPoint = rand() % 14000 + 3600;
        snprintf(uartBuffer, sizeof(uartBuffer), "Setpoint SUGGESTION: %d \r\n", newSetPoint);
        UART_Sonar_PutString(uartBuffer);
    } while(newSetPoint > (oldSetPoint - minDistance) && newSetPoint < (oldSetPoint + minDistance));
    oldSetPoint = newSetPoint;

    return newSetPoint;
}

CY_ISR(isr_newPosition_handler)
{
    motorChangeSetPoint(generateNewSetpoint());
}

CY_ISR(ISR_UART_rx_handler)
{
    uint8_t bytesToRead = UART_Sonar_GetRxBufferSize();
    while (bytesToRead > 0)
    {
        uint8_t byteReceived = UART_Sonar_ReadRxData();
        UART_Sonar_WriteTxData(byteReceived); // echo back
        
        handleByteReceived(byteReceived);
        
        bytesToRead--;
    }
}

CY_ISR(isr_stopGame_handler)
{
    stopGame = true;
    
}

void handleByteReceived(uint8_t byteReceived)
{
    switch(byteReceived)
    {
        case 't' :
        {
            snprintf(uartBuffer, sizeof(uartBuffer), "Setpoint changed to: 17600 \r\n");
            UART_Sonar_PutString(uartBuffer);
            motorChangeSetPoint(17000);
            isr_newPosition_Stop();
        }
        break;
        case 'r' :
        {
            snprintf(uartBuffer, sizeof(uartBuffer), "Setpoint changed to: 12000 \r\n");
            UART_Sonar_PutString(uartBuffer);
            motorChangeSetPoint(8000);
            isr_newPosition_Stop();
        }
        break;
        case 'y' :
        {
            snprintf(uartBuffer, sizeof(uartBuffer), "Setpoint: 5000 \r\n");
            UART_Sonar_PutString(uartBuffer);
            motorChangeSetPoint(5000);
            isr_newPosition_Stop();
        }
        break;
        case 'w' :
        {
            snprintf(uartBuffer, sizeof(uartBuffer), "Normal operation resumed \r\n");
            UART_Sonar_PutString(uartBuffer);
            isr_newPosition_StartEx(isr_newPosition_handler);
        }
        break;
        case '1' :
        {
            snprintf(uartBuffer, sizeof(uartBuffer), "Freeze all motor functions \r\n");
            UART_Sonar_PutString(uartBuffer);
            writeKillMotor(true);
        }
        break;
        case '2' :
        {
            snprintf(uartBuffer, sizeof(uartBuffer), "Revive motor \r\n");
            UART_Sonar_PutString(uartBuffer);
            writeKillMotor(false);
        }
        break;
        default :
        {
            // nothing
        }
        break;
    }
}

/* [] END OF FILE */
