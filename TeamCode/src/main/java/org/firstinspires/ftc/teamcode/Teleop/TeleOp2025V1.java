package org.firstinspires.ftc.teamcode.Teleop;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.core.units.Angle;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.FieldCentric;
import dev.nextftc.hardware.driving.HolonomicDrivePowers;
import dev.nextftc.hardware.driving.HolonomicMode;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.Direction;
import dev.nextftc.hardware.impl.IMUEx;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp(name = "Teleop")
public class TeleOp2025V1 extends NextFTCOpMode {
    public TeleOp2025V1() {
        addComponents(
                new SubsystemComponent(Intake.INSTANCE),
                new SubsystemComponent(Shuffler.INSTANCE),
                new SubsystemComponent(Arm.INSTANCE),
                new SubsystemComponent(Shooter.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    // change the names and directions to suit your robot
    private final MotorEx frontLeftMotor = new MotorEx("leftFront");
    private final MotorEx frontRightMotor = new MotorEx("rightFront").reversed();
    private final MotorEx backLeftMotor = new MotorEx("leftBack");
    private final MotorEx backRightMotor = new MotorEx("rightBack").reversed();


    GoBildaPinpointDriver odo;

    double LeftPower;
    double RightPower;
    double LeftStrafePower;
    double RightStrafePower;

    DcMotor FrontLeft;
    DcMotor FrontRight;
    DcMotor BackLeft;
    DcMotor BackRight;

    @Override
    public void onStartButtonPressed() {
        Command driverControlled = new MecanumDriverControlled(
                frontLeftMotor,
                frontRightMotor,
                backLeftMotor,
                backRightMotor,
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX(),
                new FieldCentric(
                        // THIS IS THE FIX: Pass a supplier function that returns the current angle
                        () -> Angle.fromRad(odo.getPosition().getHeading(AngleUnit.RADIANS))
                )
        );
        driverControlled.schedule();

        odo = hardwareMap.get(GoBildaPinpointDriver.class,"GoBuildaOdom");

        odo.resetPosAndIMU();

        /*
        FrontLeft = hardwareMap.get(DcMotor.class, "leftFront");
        FrontRight = hardwareMap.get(DcMotor.class, "rightFront");
        BackRight = hardwareMap.get(DcMotor.class, "rightBack");
        BackLeft = hardwareMap.get(DcMotor.class, "leftBack");

        FrontLeft.setDirection(DcMotor.Direction.FORWARD);
        FrontRight.setDirection(DcMotor.Direction.REVERSE);
        BackLeft.setDirection(DcMotor.Direction.FORWARD);
        BackRight.setDirection(DcMotor.Direction.REVERSE );

        telemetry.addData("angle", odo.getPosition().getHeading(AngleUnit.RADIANS));
        telemetry.update();
         */

        Gamepads.gamepad1().leftTrigger()
                .atLeast(.7).toggleOnBecomesTrue()
                .whenBecomesTrue(Intake.INSTANCE.on)
                .whenBecomesFalse(Intake.INSTANCE.off);

        Gamepads.gamepad1().a().whenBecomesTrue(new SequentialGroup(Arm.INSTANCE.down, Shuffler.INSTANCE.IntakePos1));
        Gamepads.gamepad1().x().whenBecomesTrue(new SequentialGroup(Arm.INSTANCE.down, Shuffler.INSTANCE.IntakePos2));
        Gamepads.gamepad1().b().whenBecomesTrue(new SequentialGroup(Arm.INSTANCE.down, Shuffler.INSTANCE.IntakePos3));

        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(Shuffler.INSTANCE.ShootPos1);
        Gamepads.gamepad1().dpadDown().whenBecomesTrue(Shuffler.INSTANCE.ShootPos2);
        Gamepads.gamepad1().dpadRight().whenBecomesTrue(Shuffler.INSTANCE.ShootPos3);

        Gamepads.gamepad1().y()
                .toggleOnBecomesTrue()
                .whenBecomesTrue(Arm.INSTANCE.up)
                .whenBecomesFalse(Arm.INSTANCE.down);

        Gamepads.gamepad1().rightTrigger()
                .atLeast(.7).toggleOnBecomesTrue()
                .whenBecomesTrue(Shooter.INSTANCE.on)
                .whenBecomesFalse(Shooter.INSTANCE.off);





    }

    @Override
    public void onUpdate() {

        odo.update();


        /*
        double drive = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;
        double strafe = gamepad1.left_stick_x;

        double botHeading = odo.getPosition().getHeading(AngleUnit.RADIANS);



        double rotX = strafe * Math.cos(-botHeading) - drive * Math.sin(-botHeading);
        double rotY = strafe * Math.sin(-botHeading) + drive * Math.cos(-botHeading);

        rotX = rotX * 1.1;

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(strafe), 1);
        double frontLeftPower = (rotY + rotX + turn) / denominator;
        double backLeftPower = (rotY - rotX + turn) / denominator;
        double frontRightPower = (rotY - rotX - turn) / denominator;
        double backRightPower = (rotY + rotX - turn) / denominator;

        FrontLeft.setPower(frontLeftPower);
        BackLeft.setPower(backLeftPower);
        BackRight.setPower(backRightPower);
        FrontRight.setPower(frontRightPower);
         */
    }
}