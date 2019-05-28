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

#ifndef MOTOR_CONTROL_H
#define MOTOR_CONTROL_H

#include <stdio.h>
#include "project.h"
#include <stdbool.h>

void motorControlInit(uint16_t sampleTimeMilliseconds);

void motorChangeSetPoint(uint16_t newSetPoint);

void writeKillMotor(bool killValue);

void motorSetDirection(int8_t motorDirectionNew);

void getPositionControl(uint16_t distance);

#endif
/* [] END OF FILE */
