package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;


public class GyroMath {
    //define class members
    private ElapsedTime runtime = new ElapsedTime();
    private HardwareTestbot myRobot;
    //declaration of objects
    private Orientation angle;
    public Acceleration gravity;
    //angle variables
    private double globalAngle;
    double target_Angle = 0;
    private double prev_angle_error;
    double angle_error;
    //distance variables - LATER
    double distance = 0;
    double prev_dist_error, dist_error;
    double target_Distance = 1;
    //timing
    private double elaspsedTime, time, timePrev;
    private double period;
    //variables for the PID systems
    double kP = 0.005;
    double kI = 0.2;
    double kD = 1;
    //actual PID outputs
    double PID_p, PID_i = 0, PID_d = 0, PID_total;

    public GyroMath() { }

    public void initDrive(HardwareTestbot robot) { myRobot = robot;
    time = runtime.seconds();
    }
    //PID Math given target
    public double calcAngle(double target){
        target_Angle = target;
        if(runtime.seconds() > time + period){
            time = runtime.seconds();
            //CONVENTIONS USED COUNTERCLOCKWISE IS NEGATIVE TURN ----- CLOCKWISE IS POSITIVE TURN
            angle_error = (getAngle() - convertGlobalAngle(target_Angle));
            PID_p = kP * angle_error;
            double angle_Derv = angle_error - prev_angle_error;
            PID_d = kD*(angle_Derv/period);

            if(-50 > angle_error && angle_error < 50){
                PID_i = PID_i + (kI * angle_error);
            }else{
                PID_i = 0;
            }
            if(angle_error > 5){
                PID_total = PID_p; //+ PID_i + PID_d;
            }else{
                PID_total = 0;
            }
            prev_angle_error = angle_error;
            //send back value
        }
        return PID_total;
    }
    //make current heading the zero
    void resetAngle() { angle = myRobot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }
    //reading angle objects z axis
    double getAngle() {
        Orientation angle = myRobot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return angle.firstAngle;
    }
    //converting heading to global angle
    double getGlobalAngle() {
        globalAngle = (angle.firstAngle+360)%360;
        return globalAngle;
    }
    //convert target to hemisphere angle
    double convertGlobalAngle(double target){
        double hemiTarget = target;
        if(target > 179){
            hemiTarget = ((target % 180)-180);
        }
        return hemiTarget;
    }
}
