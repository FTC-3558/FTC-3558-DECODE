package org.firstinspires.ftc.teamcode.Teleop.Util;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import dev.nextftc.hardware.impl.MotorEx;

public class Motors {
    HardwareMap HardwareMap;
    public Limelight3A Limelight;
    public IMU imu;
    public MotorEx FrontLeft;
    public MotorEx FrontRight;
    public MotorEx BackLeft;
    public MotorEx BackRight;

    public DcMotor[] DriveMotors;
    public Motors(HardwareMap hwmp) {
        this.HardwareMap = hwmp;
        this.Limelight = HardwareMap.get(Limelight3A.class, "Limelight");
        this.imu = HardwareMap.get(IMU.class, "imu");
        this.FrontLeft = new MotorEx("leftFront");
        this.FrontRight = new MotorEx("rightFront");
        this.BackRight = new MotorEx("rightBack");
        this.BackLeft = new MotorEx("leftBack");
    }
}