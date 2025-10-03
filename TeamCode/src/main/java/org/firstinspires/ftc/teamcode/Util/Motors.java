package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

public class Motors {
    HardwareMap HardwareMap;
    public Limelight3A Limelight;
    public IMU imu;
    public DcMotor FrontLeft;
    public DcMotor FrontRight;
    public DcMotor BackLeft;
    public DcMotor BackRight;

    public DcMotor[] DriveMotors;
    public Motors(HardwareMap hwmp) {
        this.HardwareMap = hwmp;
        this.Limelight = HardwareMap.get(Limelight3A.class, "limelight");
        this.imu = HardwareMap.get(IMU.class, "imu");
        this.FrontLeft = HardwareMap.get(DcMotor .class, "leftFront");
        this.FrontRight = HardwareMap.get(DcMotor.class, "rightFront");
        this.BackRight = HardwareMap.get(DcMotor.class, "rightBack");
        this.BackLeft = HardwareMap.get(DcMotor.class, "leftBack");
        this.DriveMotors = new DcMotor[]{
                FrontLeft,
                FrontRight,
                BackRight,
                BackLeft
        };
    }
}