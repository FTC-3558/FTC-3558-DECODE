package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

public class Shuffler implements Subsystem {
    public static final Shuffler INSTANCE = new Shuffler();
    private Shuffler() { }

    private ServoEx servo = new ServoEx("Shuffler");

    public Command pos1 = new SetPosition(servo, 0.0).requires(this);
    public Command pos2 = new SetPosition(servo, 0.5).requires(this);
    public Command pos3 = new SetPosition(servo, 1.0).requires(this);

}
