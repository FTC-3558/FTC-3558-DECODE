package org.firstinspires.ftc.teamcode.Auto.Leave;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class LeaveGoal extends LinearOpMode {

    DcMotor FrontLeft;
    DcMotor FrontRight;
    DcMotor BackLeft;
    DcMotor BackRight;

    RuntimeException time;

    @Override
    public void runOpMode() {

        time = new RuntimeException();

        FrontLeft = hardwareMap.get(DcMotor.class, "leftFront");
        FrontRight = hardwareMap.get(DcMotor.class, "rightFront");
        BackRight = hardwareMap.get(DcMotor.class, "rightBack");
        BackLeft = hardwareMap.get(DcMotor.class, "leftBack");

        FrontLeft.setDirection(DcMotor.Direction.FORWARD);
        FrontRight.setDirection(DcMotor.Direction.REVERSE);
        BackLeft.setDirection(DcMotor.Direction.FORWARD);
        BackRight.setDirection(DcMotor.Direction.REVERSE );

        waitForStart();

        if (opModeIsActive()) {

            FrontLeft.setPower(-.4);
            BackLeft.setPower(.4);
            FrontRight.setPower(.4);
            BackRight.setPower(-.4);
            sleep(1500);
            FrontLeft.setPower(0);
            BackLeft.setPower(0);
            FrontRight.setPower(0);
            BackRight.setPower(0);
        }
    }
}
