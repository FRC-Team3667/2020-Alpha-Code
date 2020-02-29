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
    private static final double HOOD_UP = 160;
    private static final double HOOD_DOWN = 0;

    private boolean[] active;
    private boolean shoot;
    private int shootStage;

    //*NOTE*: VictorSPX controllers are smaller than TalonSRX controllers
    //motor variables
    private WPI_TalonSRX intakeDep;
        //positive deploys, negative retracts
        //Hold button (RB, cont 2) down to keep deployed and keep stage 1 of intake active

    private WPI_TalonSRX intake1;
        //positive moves balls toward shooter
        //Press button (B, cont 2) to eject balls out of front

    private WPI_TalonSRX intake2;
        //positive moves balls toward shooter

    private WPI_TalonFX shooter;
        //positive shoots balls up and out
        //Press button (A, cont 2) to shoot all balls in robot

    // laser1
    // laser2
    // laserS

    private WPI_VictorSPX extender;
        //positive extends the scissor climber upwards
        //Hold button (RB, cont 1) to extend scissor climber

    private WPI_VictorSPX winch;
        //Hold button (RT, cont 1) to retract scissor climber, lifting robot (no power to scissor climber motor, power to winch motor)

    private WPI_VictorSPX spin;
        //Press button (X, cont 2) to rotate the color wheel 3 times
        //Press button (Y, cont 2) to rotate the color wheel to a given color

    //servo variables
    private Servo hood;
        //0 degrees is fully closed, 180 degrees is fully open (90 degrees upward)

    private Servo colorEx;
        //

    private Servo winchLock;
        //

    public Operations()
    {
        active = new boolean[ACTIVE_LENGTH];
        shoot = false;
        shootStage = 0;
        //motor variable initialization
        intakeDep = new WPI_TalonSRX(21);
        intakeDep.setNeutralMode(NeutralMode.Brake);
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
        hood.setBounds(2.0, 1.8, 1.5, 1.3, 1.0);
        colorEx = new Servo(1);
        colorEx.setBounds(2.0, 1.8, 1.5, 1.3, 1.0);
        winchLock = new Servo(2);
    }

    public void operate(Joystick j1, Joystick j2)
    {
        // if (j1.getRawButton(4)){
        //     intake1.set(.35);
        //     intake2.set(.35);
        // }

        // if (j1.getRawButton(6)){
        //  //lower hood
        //     hoodAngle = (hoodAngle-5<0)? 0:hoodAngle-5;
        //     hood.setSpeed(-1.0) ;
        //     hood.setAngle(hoodAngle);
        //     SmartDashboard.putNumber("hood Angle", hoodAngle);
    
        // }
       
        // if (j1.getRawButton(5)){
        //     // raise hood
        //     hoodAngle = (hoodAngle+5>180)? 180:hoodAngle+5;
        //     hood.setSpeed(1.0) ;
        //     hood.setAngle(hoodAngle);
        //     SmartDashboard.putNumber("hood Angle", hoodAngle);
        // }
        // if (j1.getRawButton(1)){
        //     // slower shooter speed
        //     shooterSpeed = (shooterSpeed-.1<0)? 0:shooterSpeed-.1;
        //     shooter.set(shooterSpeed);
        //     SmartDashboard.putNumber("spped", shooterSpeed);
        //  }
        // if (j1.getRawButton(2)){
        //     // faster shooter speed
        //     shooterSpeed = (shooterSpeed+.1>1.0)? 1:shooterSpeed+.1;
        //     shooter.set(shooterSpeed);
        //     SmartDashboard.putNumber("spped", shooterSpeed);
        //  }
        //  if (j1.getRawButton(3)){
        //      // off
        //      intake1.set(0);
        //      intake2.set(0);
        //     shooterSpeed = 0;
        //     shooter.set(shooterSpeed);
        //     SmartDashboard.putNumber("spped", shooterSpeed);
        //  }
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
        if(j2.getRawButton(1) || shoot)
        {
            shoot = (!shoot)? true:true;
            switch(shootStage)
            {
                case 0:
                    if(setAngle(hood, HOOD_UP))
                    {
                        shootStage++;
                    }
                    break;
                case 1:
                    if(setAngle(hood, HOOD_DOWN))
                    {
                        shootStage++;
                    }
                    break;
                default:
                    shootStage = 0;
                    shoot = false;
            }
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

    //@return is angle set?
    private boolean setAngle(Servo s, double angle)
    {
        if(s.getAngle() == angle)
        {
            return true;
        }
        if(s.getAngle() > angle)
        {
            s.setSpeed(-1);
            s.setAngle(angle);
        }
        else if(s.getAngle() != angle)
        {
            s.setSpeed(1);
            s.setAngle(angle);
        }
        return false;
    }
}
