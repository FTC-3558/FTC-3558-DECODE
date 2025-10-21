package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;

public class Intake implements Subsystem {
    public static final Intake INSTANCE = new Intake();

    private Intake() {
    }

    private MotorEx motor = new MotorEx("Intake");

    private ControlSystem controlSystem = ControlSystem.builder()
            .velPid(0.005, 0, 0)
            .basicFF()
            .build();

    public Command on = new RunToPosition(controlSystem, 0).requires(this);
    public Command off = new RunToPosition(controlSystem, .5).requires(this);

    @Override
    public void periodic() {
        motor.setPower(controlSystem.calculate(motor.getState()));
    }
}
