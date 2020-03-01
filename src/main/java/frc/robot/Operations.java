/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import java.util.TimerTask;
import java.util.Timer;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Operations {
    private static final int ACTIVE_LENGTH = 2;
    private static final int DESCENDING = 0;
    private static final int ASCENDING = 1;
    private static final double CLIMB_UP_SPD = .65;
    private static final double CLIMB_DOWN_SPD = .3;
    private static final double WINCH_DOWN_SPD = -.35;
    private static final double INTAKE_DEPLOY_POS = 65;
    private static final double INTAKE_RETRACT_POS = 10;
    private static final double INTAKE_CLIMB_POS = 25;
    private static final double INTAKE_POS_TOLERANCE = 1.5;
    private static final double INTAKE_IN_SPD = 0.4;
    private static final double INTAKE_OUT_SPD = -0.5;
    private static final double HOOD_UP = 160;
    private static final double HOOD_DOWN = 0;
    private static final int HOOD_UP_T = 3000;
    private static final int HOOD_DOWN_T = 2100;
    private static final double SHOOTER_SPD = 1;

    // constant for talonFX
    private static final int UNITS_PER_REV = 2048;

    private boolean[] active;
    private boolean shoot;
    private int shootStage;
    private Timer t;
    private int winchCycles;

    //Limit switches
    DigitalInput extendLimitSwitch;

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
        t = new Timer();
        winchCycles = 0;
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
        // hood.setSpeed(0);
        colorEx = new Servo(1);
        colorEx.setBounds(2.0, 1.8, 1.5, 1.3, 1.0);
        winchLock = new Servo(2);
        extendLimitSwitch = new DigitalInput(1);
        intakeDep.setSelectedSensorPosition(0);
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
        if(j2.getRawButton(1) || shoot)
        {
            fireCells();
        }
        if(j2.getRawButton(LogitechJoy.BTN_RB))
        {
            setCellIntakePos(INTAKE_DEPLOY_POS);
            intake1.set(INTAKE_IN_SPD);
        }
        else
        {
            setCellIntakePos(INTAKE_RETRACT_POS);
        }
        if(j2.getRawButton(LogitechJoy.BTN_B))
        {
            intake1.set(INTAKE_OUT_SPD);
            intake2.set(INTAKE_OUT_SPD);
        }
        else
        {
            intake2.set(0);
        }
        if(!j2.getRawButton(LogitechJoy.BTN_RB) && !j2.getRawButton(LogitechJoy.BTN_B))
        {
            intake1.set(0);
        }
        climb(j1);
        healthCheck();
    }

    private void healthCheck() {}

    private void fireCells() {
        shoot = true;
        switch(shootStage)
        {
            case 0:
                hood.setAngle(HOOD_UP);
                shooter.set(SHOOTER_SPD);
                t.schedule(new TimerTask(){
                    @Override
                    public void run()
                    {
                        shootStage++;
                    }
                    }, (long) HOOD_UP_T);
                break;
            case 1:
                intake2.set(INTAKE_IN_SPD);
                t.schedule(new TimerTask(){
                    @Override
                    public void run()
                    {
                        intake2.set(0);
                        shootStage++;
                    }
                    }, (long) 7);
                break;
            default:
                shootStage = 0;
                shoot = false;
        }
    }

    private void climb(Joystick j) {
        if (j.getRawAxis(3) >= LogitechJoy.TRIGGER_THRESH) {
            if (!active[DESCENDING]) {
                extender.setNeutralMode(NeutralMode.Coast);
                active[DESCENDING] = true;
            }
            extender.set(CLIMB_DOWN_SPD);
        } 
        else if(j.getRawButton(6) && !extendLimitSwitch.get()) 
        {
            if (!active[ASCENDING]) {
                extender.setNeutralMode(NeutralMode.Brake);
                active[ASCENDING] = true;
            }
            extender.set(CLIMB_UP_SPD);
        }
        if(active[DESCENDING] && j.getRawAxis(3) < LogitechJoy.TRIGGER_THRESH) {
            active[DESCENDING] = false;
            extender.setNeutralMode(NeutralMode.Brake);
            extender.set(0);
        }
        if((active[ASCENDING] && !j.getRawButton(6)) || extendLimitSwitch.get()) {
            active[ASCENDING] = false;
            extender.set(0);
        }
    }

    private void setCellIntakePos(double targetPos) {
        double intakeDepCurrentPos = ((double) intakeDep.getSelectedSensorPosition(0) / UNITS_PER_REV);
        if(intakeDepCurrentPos > targetPos + INTAKE_POS_TOLERANCE)
        {
            /* Check if over tolerance */
            intakeDep.set(-0.3);
        } 
        else if(intakeDepCurrentPos < targetPos - INTAKE_POS_TOLERANCE)
        {
            /* Check if under tolerance */
            intakeDep.set(0.2);
        }
        else
        {
            /* Position is within tolerance */
            intakeDep.set(0);
        }
    }
}
