package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;


public class Shooter implements Subsystem {
    public static final Shooter INSTANCE = new Shooter();
    private Shooter() { }

    private MotorEx motor = new MotorEx("Shooter");

    public Command on(double power) {
        return new SetPower(motor, power).requires(this);
    }
    public Command off = new SetPower(motor, 0).requires(this);

    @Override
    public void periodic() {

    }
}

