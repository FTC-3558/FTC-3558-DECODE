package org.firstinspires.ftc.teamcode.TeleOp.SubSystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Drive {

    double drive;
    double turn;
    double strafe;
    DcMotor[] motors;
    IMU gyro;

    public Drive(Gamepad js, DcMotor[] motors, IMU imu) {
        this.drive = js.left_stick_y;
        this.turn = js.right_stick_x;
        this.strafe = js.left_stick_x;
        this.motors = motors;
        this.gyro = imu;

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
        gyro.initialize(parameters);
    }

    public void Reset_Gyro() {
        gyro.resetYaw();
    }

    public void RobotCentric() {
        double LeftPower = Range.clip(drive + turn, -1.0, 1.0) * .75;
        double RightPower = Range.clip(drive - turn, -1.0, 1.0) * .75;
        double LeftStrafePower = strafe * .75;
        double RightStrafePower = strafe * .75;


        // Send calculated power to wheels
        motors[0].setPower(LeftPower);
        motors[1].setPower(RightPower);
        motors[2].setPower(LeftPower);
        motors[3].setPower(RightPower);

        if (Math.abs(strafe) > 0.1) {
            motors[0].setPower(LeftStrafePower);
            motors[1].setPower(RightStrafePower);
            motors[2].setPower(-LeftStrafePower);
            motors[3].setPower(-RightStrafePower);
        }
    }

    public void FieldCentric() {
        double botHeading = gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double rotX = strafe * Math.cos(-botHeading) - drive * Math.sin(-botHeading);
        double rotY = strafe * Math.sin(-botHeading) + drive * Math.cos(-botHeading);

        rotX = rotX * 1.1;

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(strafe), 1);
        double frontLeftPower = (rotY + rotX + turn) / denominator;
        double backLeftPower = (rotY - rotX + turn) / denominator;
        double frontRightPower = (rotY - rotX - turn) / denominator;
        double backRightPower = (rotY + rotX - turn) / denominator;

        motors[0].setPower(frontLeftPower);
        motors[1].setPower(backLeftPower);
        motors[2].setPower(backRightPower);
        motors[3].setPower(frontRightPower);
    }
}