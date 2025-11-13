package org.firstinspires.ftc.teamcode.Teleop;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Auto_Score;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Intake_Store;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision; // NEW: Import Vision subsystem

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
        // --- HARDWARE INJECTION FIX ---
        // The OpMode constructor has access to hardwareMap. We retrieve the sensor here.
        NormalizedColorSensor colorSensor = hardwareMap.get(NormalizedColorSensor.class, "ballColorSensor");
        Limelight3A Limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // We inject the sensor into the subsystem using the setter.
        Shuffler.INSTANCE.setColorSensor(colorSensor);
        Vision.INSTANCE.setLimelight(Limelight);

        addComponents(
                new SubsystemComponent(Intake.INSTANCE),
                new SubsystemComponent(Shuffler.INSTANCE),
                new SubsystemComponent(Arm.INSTANCE),
                new SubsystemComponent(Shooter.INSTANCE),
                new SubsystemComponent(Vision.INSTANCE),
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


        // --- NEW AUTOMATED BINDINGS ---

        // 1. LEFT BUMPER: Automated Intake and Store (Rotation, Intake, Detection, Storage)
        // Replaces the manual left trigger, A, X, and B buttons
        Gamepads.gamepad1().leftBumper()
                .whenBecomesTrue(new Intake_Store());

        // 2. RIGHT BUMPER: Automated 3-Ball Score (Checks balls, reads vision, scores, resets)
        // Replaces the manual right trigger, D-Pad, and Y buttons for shooting
        Gamepads.gamepad1().rightBumper()
                .whenBecomesTrue(new Auto_Score());

        // 3. LEFT TRIGGER: Manual Intake Reverse (for clearing jams)
        Gamepads.gamepad1().leftTrigger()
                .atLeast(.7).toggleOnBecomesTrue()
                .whenBecomesTrue(Intake.INSTANCE.Reverse) // Bind reverse function
                .whenBecomesFalse(Intake.INSTANCE.off);

        // --- MANUAL OVERRIDES (Used for debugging or individual control) ---

        // X Button: Manual Shoot (Hold Flywheel and push ball)
        Gamepads.gamepad1().x()
                .toggleOnBecomesTrue()
                .whenBecomesTrue(Shooter.INSTANCE.on) // Flywheel ON
                .whenBecomesFalse(Shooter.INSTANCE.off); // Flywheel OFF

        // Y Button: Manual Arm Up/Down
        Gamepads.gamepad1().y()
                .toggleOnBecomesTrue()
                .whenBecomesTrue(Arm.INSTANCE.up)
                .whenBecomesFalse(Arm.INSTANCE.down);


        // DPad Controls for Manual Shuffler Indexing (useful for testing)
        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(Shuffler.INSTANCE.rotateToEmptySlotForIntake());
        Gamepads.gamepad1().dpadRight().whenBecomesTrue(Shuffler.INSTANCE.rotateSlotToShoot(0));

        // --- REMOVED BINDINGS ---
        // Removed: A, X, B bindings (manual shuffler rotation - now handled by IntakeAndStoreCommand)
        // Removed: D-Pad bindings (manual shuffler rotation - now handled by AutoScoreAllCommand)
        // Removed: Right Trigger binding (manual shooter on/off - now handled by AutoScoreAllCommand)
    }

    @Override
    public void onUpdate() {
        odo.update();
    }
}