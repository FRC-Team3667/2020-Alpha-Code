/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

/**
 * Add your docs here.
 */
public class DriveSystem {
    private static class Auton {
        private static class pseuJoy extends Joystick {
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

            public void setAxis(int i, int val)
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

        private DriveSystem d;
        private pseuJoy j;
        private boolean moving;
        private boolean turning;
        private double angle1;
        private double angle2;
        // private int stage;

        public Auton(DriveSystem d)
        {
            this.d = d;
            j = new pseuJoy();
            moving = false;
            turning = false;
            // stage = 0;
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

        }

        private void move(int dist)
        {
            if(!moving)
            {
                //initialization operations

            }
        }

        private void turn(int angle)
        {
            if(!turning)
            {
                //initialization operations
                angle1 = d.nav.getAngle();
                angle2 = angle1 + angle;
                turning = true;
            }
            if(angle1 > angle2)
            {
                
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