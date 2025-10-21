package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;


public class Shooter implements Subsystem {
    public static final Shooter INSTANCE = new Shooter();
    private Shooter() { }

    private MotorEx motor = new MotorEx("Shooter");

    private ControlSystem controlSystem = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF()
            .build();

    public Command on = new RunToPosition(controlSystem, 0).requires(this);
    public Command off = new RunToPosition(controlSystem, 1).requires(this);

    @Override
    public void periodic() {
        motor.setPower(controlSystem.calculate(motor.getState()));
    }
}

