package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

public class Shuffler implements Subsystem {
    public static final Shuffler INSTANCE = new Shuffler();
    private Shuffler() { }

    private ServoEx servo = new ServoEx("Shuffler");

    public Command IntakePos1 = new SetPosition(servo, 0.1).requires(this);
    public Command IntakePos3 = new SetPosition(servo, 0.5).requires(this);
    public Command IntakePos2 = new SetPosition(servo, .87).requires(this);

    public Command ShootPos1 = new SetPosition(servo, 0.30).requires(this);
    public Command ShootPos2 = new SetPosition(servo, 0.69).requires(this);
    public Command ShootPos3 = new SetPosition(servo, -.25).requires(this);
}
