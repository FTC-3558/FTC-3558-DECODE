package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

public class Arm implements Subsystem {
    public static final Arm INSTANCE = new Arm();
    private Arm() { }

    private ServoEx servo = new ServoEx("Arm");

    public Command up = new SetPosition(servo, .95).requires(this);
    public Command down = new SetPosition(servo, .72 ).requires(this);

}
