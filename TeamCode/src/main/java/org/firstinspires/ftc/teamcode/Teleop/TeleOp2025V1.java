package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.Util.Motors;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.hardware.driving.MecanumDriverControlled;

@TeleOp(name = "TeleOp2025V1")
public class TeleOp2025V1 extends NextFTCOpMode {
    public TeleOp2025V1() {
        addComponents(
                new SubsystemComponent(Shooter.INSTANCE, Shuffler.INSTANCE)
        );
    }

    @Override
    public void onStartButtonPressed() {

        Motors motors = new Motors(hardwareMap);

        //Drive Control
        Command driverControlled = new MecanumDriverControlled(
                motors.FrontLeft,
                motors.FrontRight,
                motors.BackLeft,
                motors.BackRight,
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX()
        );
        driverControlled.schedule();
        //

        //Shooter Toggle on/off
        Gamepads.gamepad1().leftTrigger()
                .atLeast(.7).toggleOnBecomesTrue()
                .whenBecomesTrue(Shooter.INSTANCE.on)
                .whenBecomesFalse(Shooter.INSTANCE.off);


    }

}