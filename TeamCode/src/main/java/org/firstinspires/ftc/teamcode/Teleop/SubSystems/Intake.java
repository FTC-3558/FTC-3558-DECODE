package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;

public class Intake implements Subsystem {
    public static final Intake INSTANCE = new Intake();

    private Intake() {
    }

    private MotorEx motor = new MotorEx("Intake");


    public Command on = new SetPower(motor, 1).requires(this);

    public Command Reverse = new SetPower(motor, -1).requires(this);
    public Command off = new SetPower(motor, 0).requires(this);

    @Override
    public void periodic() {
    }
}
