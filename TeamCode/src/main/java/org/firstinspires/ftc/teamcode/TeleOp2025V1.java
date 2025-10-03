package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.TeleOp.CustomLib.JoyStick;
import org.firstinspires.ftc.teamcode.TeleOp.SubSystems.Drive;

import org.firstinspires.ftc.teamcode.TeleOp.Motors;

@TeleOp(name = "TeleOp2025V1")
public class TeleOp2025V1 extends LinearOpMode {
    @Override
    public void runOpMode() {

        Motors motors = new Motors(hardwareMap);
        Drive drive = new Drive(gamepad1, motors.DriveMotors, motors.imu);

        waitForStart();

        while (opModeIsActive()) {

            //Drive Mode
            JoyStick.Command(gamepad1.right_bumper, true, drive::RobotCentric);
            JoyStick.Command(gamepad1.right_bumper, false, drive::FieldCentric);
            JoyStick.Command(gamepad1.right_trigger, true, drive::Reset_Gyro);

        }
    }
}