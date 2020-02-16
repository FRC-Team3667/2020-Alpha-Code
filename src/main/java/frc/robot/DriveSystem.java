/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Timer;
import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class DriveSystem {
    private static class Auton {
        private static class pseuJoy extends Joystick {
            public static final int MOVE_FWD_FULL_SPD = -1;
            public static final int STRAFE_FULL_SPD = 1;
            public static final double MOVE_SLOW_COEF = .2;
            public static final double TURN_FULL_SPD = .8;
            public static final double TURN_SLOW_SPD = .2;

            private double ax0;
            private double ax1;
            private double ax4;

            public pseuJoy()
            {
                super(50);
                ax0 = 0;
                ax1 = 0;
                ax4 = 0;
            }

            public void addToAxis(int i, double val)
            {
                setAxis(i, getRawAxis(i) + val);
            }

            public void setAxis(int i, double val)
            {
                switch(i)
                {
                    case 0:
                        ax0 = val;
                        break;

                    case 1:
                        ax1 = val;
                        break;

                    case 4:
                        ax4 = val;
                        break;
                }
            }

            @Override
            public double getRawAxis(int i)
            {
                switch(i)
                {
                    case 0:
                        return ax0;

                    case 1:
                        return ax1;

                    case 4:
                        return ax4;

                    default:
                        return 0.0;
                }
            }
        }

        // private static final int L_DEPTH = 0;
        // private static final int M_DEPTH = 1;
        // private static final int H_DEPTH = 2;

        /*Constants for array of booleans describing
        whether various autonomous driving functions
        are active */
        private static final int ACTVE_LENGTH = 3;
        private static final int MOVING_FORWARD = 0;
        private static final int TURNING = 1;
        private static final int STRAFING = 2;

        //Miscellaneous constants
        private static final int TIME_SAFE = 500;
        private static final int ANGLE_SAFE = 10;

        private DriveSystem d;
        private pseuJoy j;
        private Timer t;
        private boolean[] active;
        private double curAngle;
        private double targetAngle;
        private int stage;

        public Auton(DriveSystem d)
        {
            this.d = d;
            j = new pseuJoy();
            t = new Timer();
            active = new boolean[ACTVE_LENGTH];
            curAngle = 0;
            targetAngle = 0;
            stage = 0;
        }

        //Time-based auton

        // public void drive(int t)
        // {
        //     if(t < 5)
        //     {
        //         d.drive(new pseuJoy()
        //     }
        // }

        //Stage-based auton
        //depth and angle-based drive
        // public void drive(int d, int a)
        // {
            
        // }

        //experimental drive
        public void drive()
        {
            switch(stage)
            {
                case 0:
                    turn(45);
                    break;

                case 1:
                    move(pseuJoy.MOVE_FWD_FULL_SPD, pseuJoy.MOVE_SLOW_COEF * pseuJoy.MOVE_FWD_FULL_SPD, 5);
                    break;

                case 2:
                    strafe(pseuJoy.STRAFE_FULL_SPD, pseuJoy.MOVE_SLOW_COEF * pseuJoy.STRAFE_FULL_SPD, 5);
                    break;
            }
            d.drive(j);
            SmartDashboard.putNumber("Axis 0:", j.getRawAxis(0));
            SmartDashboard.putNumber("Axis 1:", j.getRawAxis(1));
            SmartDashboard.putNumber("Axis 4:", j.getRawAxis(4));
        }

        private void move(double speed, double slowS, double sec)
        {
            moveAdv(MOVING_FORWARD, 1, speed, slowS, sec);
        }

        private void strafe(double speed, double slowS, double sec)
        {
            if(d.isMecanum())
            {
                moveAdv(STRAFING, 0, speed, slowS, sec);
            }
        }

        private void moveAdv(int boolInd, int axis, double speed, double slowS, double sec)
        {
            if(!active[boolInd])
            {
                //*initialization operations*
                j.setAxis(axis, speed);
                active[boolInd] = true;
                t.schedule(new TimerTask(){
                  @Override
                  public void run()
                  {
                    j.setAxis(axis, slowS);
                  }
                }, (long) (sec * 1000) - TIME_SAFE);
                t.schedule(new TimerTask(){
                    @Override
                    public void run()
                    {
                      j.setAxis(axis, 0);
                      active[boolInd] = false;
                      stage++;
                    }
                  }, (long) (sec * 1000));
            }
        }

        private void turn(double angle)
        {
            if(!active[TURNING])
            {
                //initialization operations
                curAngle = d.nav.getAngle();
                targetAngle = curAngle + angle;
                active[TURNING] = true;
                if(curAngle - ANGLE_SAFE > targetAngle)
                {
                    j.setAxis(4, -pseuJoy.TURN_FULL_SPD);
                }
                else if(curAngle + ANGLE_SAFE < targetAngle)
                {
                    j.setAxis(4, pseuJoy.TURN_FULL_SPD);
                }
                else if(curAngle > targetAngle)
                {
                    j.setAxis(4, -pseuJoy.TURN_SLOW_SPD);
                }
                else if(curAngle < targetAngle)
                {
                    j.setAxis(4, pseuJoy.TURN_SLOW_SPD);
                }
                else
                {
                    active[TURNING] = false;
                }
            }
            else
            {
                curAngle = d.nav.getAngle();
                if(j.getRawAxis(4) > 0) 
                {
                    if(curAngle >= targetAngle)
                    {
                        j.setAxis(4, 0);
                        active[TURNING] = false;
                        //advance the stage:
                        stage++;
                    }
                    if(j.getRawAxis(4) > pseuJoy.TURN_SLOW_SPD && curAngle + ANGLE_SAFE >= targetAngle)
                    {
                        j.setAxis(4, pseuJoy.TURN_SLOW_SPD);
                    }
                }
                else if(j.getRawAxis(4) != 0)
                {
                    if(curAngle <= targetAngle)
                    {
                        j.setAxis(4, 0);
                        active[TURNING] = false;
                        //advance the stage:
                        stage++;
                    }
                    if(j.getRawAxis(4) < -pseuJoy.TURN_SLOW_SPD && curAngle - ANGLE_SAFE <= targetAngle)
                    {
                        j.setAxis(4, pseuJoy.TURN_SLOW_SPD);
                    }
                }
            }
        }
    }

    // private class Auton extends DriveSystem{
    //     public Auton(DriveSystem d)
    //     {
    //         this = d;
    //     }

    //     public void drive(int t)
    //     {
    //         drive(new Joystick(0));
    //     }
    // }

    private boolean mecanum;
    private WPI_VictorSPX frontLeftMotor;
    private WPI_VictorSPX frontRightMotor;
    private WPI_VictorSPX rearRightMotor;
    private WPI_VictorSPX  rearLeftMotor;
    private MecanumDrive _mDrive;
    private DifferentialDrive _aDrive;
    private NavX nav;
    private Auton auto;

    public DriveSystem(boolean mecanum)
    {
        this.mecanum = mecanum;
        if(mecanum)
        {
            frontLeftMotor = new WPI_VictorSPX(13);
            frontRightMotor = new WPI_VictorSPX(12);
            rearRightMotor = new WPI_VictorSPX(11);
            rearLeftMotor = new WPI_VictorSPX(10);
            _mDrive = new MecanumDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        }
        else
        {
            _aDrive = new DifferentialDrive(new SpeedControllerGroup(new WPI_VictorSPX(13), new WPI_VictorSPX(10)),
            new SpeedControllerGroup(new WPI_VictorSPX(12), new WPI_VictorSPX(11)));
        }
        nav = new NavX();
        auto = new Auton(this);
    }

    //Time-based auton

    // public void autoDrive(int t)
    // {
    //     auto.drive(t);
    // }


    //autoDrive based on depth and angle
    // public void autoDrive(int depth, int angle)
    // {
    //     auto.drive(depth, angle);
    // }

    public void autoDrive()
    {
        auto.drive();
    }

    public void drive(Joystick j)
    {
        if(mecanum)
        {
            _mDrive.driveCartesian(j.getRawAxis(0), -j.getRawAxis(1), j.getRawAxis(4), 0);
        }
        else
        {
            _aDrive.arcadeDrive(-j.getRawAxis(1), j.getRawAxis(4));
        }
    }

    public boolean isMecanum() {
        return mecanum;
    }

    public void setMecanum(boolean mecanum) {
        this.mecanum = mecanum;
    }

}
