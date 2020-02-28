/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Servo;

/**
 * Add your docs here.
 */
public class Operations {
    private static final int ACTIVE_LENGTH = 2;
    private static final int DESCENDING = 0;
    private static final int ASCENDING = 1;
    private static final double CLIMB_THRESH = .05;
    private static final double CLIMB_UP_SPD = .65;
    private static final double CLIMB_DOWN_SPD = .3;

    private boolean[] active;

    //*NOTE*: VictorSPX controllers are smaller than TalonSRX controllers
    //motor variables
    WPI_TalonSRX intakeDep;
        //positive deploys, negative retracts
        //Hold button (RB) down to keep deployed and keep stage 1 of intake active

    WPI_TalonSRX intake1;
        //positive moves balls toward shooter

    WPI_TalonSRX intake2;
        //positive moves balls toward shooter

    WPI_TalonFX shooter;
        //positive shoots balls up and out
        //Press button (A) to shoot all balls in robot

    // laser1
    // laser2
    // laserS

    WPI_VictorSPX extender;
        //positive extends the scissor climber upwards
        //Hold button (X) to extend scissor climber

    WPI_VictorSPX winch;
        //Hold button (Y) to retract scissor climber, lifting robot (no power to scissor climber motor, power to winch motor)

    WPI_VictorSPX spin;
        //

    //servo variables
    Servo hood;
        //0 degrees is fully closed, 180 degrees is fully open (90 degrees upward)

    Servo colorEx;
        //

    Servo winchLock;
        //

    public Operations()
    {
        active = new boolean[ACTIVE_LENGTH];
        //motor variable initialization
        intakeDep = new WPI_TalonSRX(21);
        intake1 = new WPI_TalonSRX(22);
        intake1.setInverted(true);
        intake2 = new WPI_TalonSRX(23);
        shooter = new WPI_TalonFX(24);
        shooter.setInverted(true);
        // laser1
        // laser2
        // laserS
        extender = new WPI_VictorSPX(41);
        extender.setInverted(true);
        extender.setNeutralMode(NeutralMode.Brake);
        winch = new WPI_VictorSPX(42);
        spin = new WPI_VictorSPX(51);
        hood = new Servo(0);
        colorEx = new Servo(1);
        winchLock = new Servo(2);
    }

    public void operate(Joystick j1, Joystick j2)
    {
        if(j1.getRawAxis(3) >= CLIMB_THRESH)
        {
            if(!active[DESCENDING])
            {
                extender.setNeutralMode(NeutralMode.Coast);
                active[DESCENDING] = true;
            }
            extender.set(CLIMB_DOWN_SPD);
        }
        else if(j1.getRawButton(6))
        {
            if(!active[ASCENDING])
            {
                extender.setNeutralMode(NeutralMode.Brake);
                active[ASCENDING] = true;
            }
            extender.set(CLIMB_UP_SPD);
        }
        if(active[DESCENDING] && j1.getRawAxis(3) < CLIMB_THRESH)
        {
            active[DESCENDING] = false;
            extender.setNeutralMode(NeutralMode.Brake);
            extender.set(0);
        }
        if(active[ASCENDING] && !j1.getRawButton(6))
        {
            active[ASCENDING] = false;
            extender.set(0);
        }
    }
}
