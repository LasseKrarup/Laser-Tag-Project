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

#include "MotorControl.h"
#include <stdio.h>
#include <stdlib.h>
#include <float.h>


    
    //char uartBuffer[256];
    
    uint16_t setPoint = 0;          //Positionen motoren skal hen til
    float dt = 0;                   //Tidsopløsningen til integral
    uint16_t lastError = 0;         //Til evt. differential del
    uint16_t startCondition = 0;
    
    float integral;
    //Begrænsning af integral, for at modvirke oscillering
    int16_t integralMax = 6500;
    int16_t integralMin = -6500;
    //UART
    char uartBufferMotor[256];    
    //PID faktorer
    float Kp = 6;//4.7;//3.2;//2.4;
    float Ki = 0;//1.22;//3.8;
    //float Kd = 2;
    
    bool killMotorSignal = false;        //Sættes til 1 for at slukke motoren
    
    int motorDirection = 1;
    
    //int hysteresisThreshold = 200;       //Minimum control signal for at skifte retning
    
    
void motorControlInit(uint16_t sampleTimeMilliseconds)
{
    PWM_Motor_Control_Start();
    Pin_Motor_Backwards_Write(0);
    Pin_Motor_Forward_Write(0);
    dt = (float)sampleTimeMilliseconds / 1000;
    integral = 0;
}

void motorChangeSetPoint(uint16_t newSetPoint)
{
    setPoint = newSetPoint;
    snprintf(uartBufferMotor, sizeof(uartBufferMotor), "Setpoint changed to: %d \r\n", newSetPoint);
    UART_Sonar_PutString(uartBufferMotor);
    
    startCondition = 0;//3;
    integral = 0;
}

void writeKillMotor(bool killValue)
{
    killMotorSignal = killValue;
    if(killValue){
        motorSetDirection(0);
        //Pin_Motor_Backwards_Write(0);
        //Pin_Motor_Forward_Write(0);
        //motorDirection = 0;
    }
}

void motorSetDirection(int8_t motorDirectionNew){
    switch(motorDirectionNew) {
        case 0 :    //Kill
            Pin_Motor_Backwards_Write(0);
            Pin_Motor_Forward_Write(0);
            motorDirection = 0;
            break;
        case 1 :    //Backwards
            Pin_Motor_Backwards_Write(1);
            Pin_Motor_Forward_Write(0);
            motorDirection = 1;
            break;
        case 2 :    //Forwards
            Pin_Motor_Backwards_Write(0);
            Pin_Motor_Forward_Write(1);
            motorDirection = 2;
            break;
        default:
            snprintf(uartBufferMotor, sizeof(uartBufferMotor), "Invalid direction\r\n");
            UART_Sonar_PutString(uartBufferMotor);
    }
}

void getPositionControl(uint16_t distance)
{
    int error = distance - setPoint;
    if(error >= 0)
    {
        motorSetDirection(2);
    }
    else if(error < 0)
    {
        motorSetDirection(1); 
        error = -error;
    }
    
    //PID beregning
    double proportional = error;
    //if(error < 4000 && error > -4000)
    integral = integral + (error * dt);
    
    //int derivative = ((error - lastError) / dt) * Kd;
    
    
    //Medregn faktorer
    int proportionalControl = proportional * Kp;
    int integralControl = integral * Ki;
    
    //Begræns integral
    if(integralControl > integralMax){
        integralControl = integralMax;
    }
    else if(integralControl < integralMin){
        integralControl = integralMin;
    }
    
    int controlSignal = proportionalControl + integralControl;// + derivativeControl; //Adder alle control parts
    
    
    // Mekaniske begrænsninger
    if(controlSignal < 11000 && controlSignal > 4000){
        controlSignal = 11000;
        
    }
    else if(controlSignal <= 4000){ //Sikrer at motoren ikke får for lav PWM
        //writeKillMotor(true);
        controlSignal = 0;
    }
    else if(controlSignal > 28000){ // Eller for høj
        controlSignal = 28000;
    }
    
    if(startCondition > 0){ // Motoren skal bruge et skub for at starte
        if(controlSignal < 20000){ //Sættes kun op
        controlSignal = 20000;
    }
        startCondition--;
        integral = 0;       
    }
    
    //Check hvilken vej motoren skal køre, skift med H-bro
    if(killMotorSignal)
    {
        controlSignal = 0;
    }//Hysterese - start hvis stoppet, ellers skift retning hvis over threshold
    
    // Send PWM
    PWM_Motor_Control_WriteCompare(controlSignal);
    
    //Output information
    if(motorDirection == 1){ //Backwards
        snprintf(uartBufferMotor, sizeof(uartBufferMotor), "setPoint: %d, Error: -%d, proportionalC: %d, integralC: %d Control signal: %d \r\n", setPoint, error, proportionalControl, integralControl, controlSignal);
    }
    else if(motorDirection == 2){ //Forwards
        snprintf(uartBufferMotor, sizeof(uartBufferMotor), "setPoint: %d, Error: %d, proportionalC: %d, integralC: %d Control signal: %d \r\n", setPoint, error, proportionalControl, integralControl, controlSignal);
    }
    else if(motorDirection == 0){ //Stopped
        snprintf(uartBufferMotor, sizeof(uartBufferMotor), "MOTOR OFF, Control signal: %d\r\n", controlSignal);
    }

    UART_Sonar_PutString(uartBufferMotor);
    
    lastError = error;
}



/* [] END OF FILE */
