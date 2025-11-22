package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Auto_ScoreBlue;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Intake_Store;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallBlue;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallRed;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision; // NEW: Import Vision subsystem

import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.core.units.Angle;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.FieldCentric;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp(name = "TeleopBlue")
public class TeleOpBlue extends NextFTCOpMode {

    // Motor Definitions
    private final MotorEx frontLeftMotor = new MotorEx("leftFront");
    private final MotorEx frontRightMotor = new MotorEx("rightFront").reversed();
    private final MotorEx backLeftMotor = new MotorEx("leftBack");
    private final MotorEx backRightMotor = new MotorEx("rightBack").reversed();


    // Rotational Lock
    public static final double ROTATION_P_GAIN = 0.03;
    private static final double MAX_ROTATION_POWER = 0.5;
    private boolean Locked = false;

    // Odometry
    GoBildaPinpointDriver odo;

    public TeleOpBlue() {

        // The OpMode constructor has access to hardwareMap. We retrieve the sensor here.

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

    // --- Main TElEOP CODE ---

    @Override
    public void onStartButtonPressed() {
        NormalizedColorSensor color1 = hardwareMap.get(NormalizedColorSensor.class, "Color1");
        NormalizedColorSensor color2 = hardwareMap.get(NormalizedColorSensor.class, "Color2");
        Limelight3A Limelight = hardwareMap.get(Limelight3A.class, "limelight");
        Servo servo = hardwareMap.get(Servo.class, "Shuffler");
        Limelight.pipelineSwitch(3);

        Limelight.start();

        // We inject the sensor into the subsystem using the setter.
        //Only Needed For Color Sensor & LimeLight because NEXTFTC doesn't have wrapper classes for them
        Shuffler.INSTANCE.sethardware(color1, color2, servo);
        Vision.INSTANCE.setLimelight(Limelight);

        servo.setDirection(Servo.Direction.REVERSE);

        // --- DRIVE COMMAND ---
        MecanumDriverControlled driverControlled = new MecanumDriverControlled(
                frontLeftMotor,
                frontRightMotor,
                backLeftMotor,
                backRightMotor,
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX(),
                //Lambda expression to feed a supplier
                () -> DriveAngle(Locked),
                new FieldCentric(
                        () -> Angle.fromRad(odo.getPosition().getHeading(AngleUnit.RADIANS))
                )
        );
        driverControlled.schedule();

        odo = hardwareMap.get(GoBildaPinpointDriver.class,"Go");
        odo.resetPosAndIMU();


        // --- NEW AUTOMATED BINDINGS ---

        // 1. LEFT BUMPER: Automated Intake and Store (Rotation, Intake, Detection, Storage)
        Gamepads.gamepad1().leftBumper()
                .whenBecomesTrue(new Intake_Store());

        // 2. RIGHT BUMPER: Automated 3-Ball Score (Checks balls, reads vision, scores, resets)
        /* Gamepads.gamepad1().rightBumper()
                 .whenBecomesTrue(new Auto_ScoreBlue());*/

        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(new Score_BallBlue(0));
        Gamepads.gamepad1().dpadDown().whenBecomesTrue(new Score_BallBlue(1));
        Gamepads.gamepad1().dpadRight().whenBecomesTrue(new Score_BallBlue(2));

        // 3. LEFT TRIGGER: Manual Intake Reverse (for clearing jams)
        Gamepads.gamepad1().leftTrigger()
                .atLeast(.7).toggleOnBecomesTrue()
                .whenBecomesTrue(Intake.INSTANCE.Reverse) // Bind reverse function
                .whenBecomesFalse(Intake.INSTANCE.off);

        // --- ROTATION LOCK ---

        Gamepads.gamepad1().a()
                .whenBecomesTrue(new InstantCommand(this::SwitchLock));

        // --- MANUAL OVERRIDES (In case my code goes to Shit) ---

        // X Button: Manual Shoot (Hold Flywheel and push ball)
        Gamepads.gamepad1().x()
                .toggleOnBecomesTrue()
                .whenBecomesTrue(Shooter.INSTANCE.on(Vision.INSTANCE.getShooterPowerBlue())) // Flywheel ON
                .whenBecomesFalse(Shooter.INSTANCE.off); // Flywheel OFF

        // Y Button: Manual Arm Up/Down
        Gamepads.gamepad1().y()
                .toggleOnBecomesTrue()
                .whenBecomesTrue(Arm.INSTANCE.up)
                .whenBecomesFalse(Arm.INSTANCE.down);

        // --- GYRO RESET ---
        Gamepads.gamepad1().start()
                .whenBecomesTrue(
                        new InstantCommand(() -> odo.resetPosAndIMU())
                );

        // DPad Controls for Manual Shuffler Indexing
        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(()->Shuffler.INSTANCE.rotateToEmptySlotForIntake());
        Gamepads.gamepad1().dpadRight().whenBecomesTrue(()->Shuffler.INSTANCE.rotateSlotToShoot(0));
    }

    // --- UPDATES ---
    @Override
    public void onUpdate() {
        odo.update();
        Vision.INSTANCE.UpdateMotif();
        telemetry.addData("Stored Colors 1", Shuffler.ballColors[0]);
        telemetry.addData("Stored Colors 2", Shuffler.ballColors[1]);
        telemetry.addData("Stored Colors 3", Shuffler.ballColors[2]);
        telemetry.update();
    }

    // --- TARGET LOCK FUNCTIONS ---

    private double getTargetRotationPower() {
        double error = Vision.INSTANCE.getRotationTOBlue();

        // Simple P-controller correction
        double rotationPower = error * ROTATION_P_GAIN;

        // Clamp the output power to a safe maximum
        return Math.min(Math.max(rotationPower, -MAX_ROTATION_POWER), MAX_ROTATION_POWER);
    }

    /*
    For Reasons this function is passed continuously into the
    mech driver control so instead of switching the mech drive command we just
    inject this and change the return value
    this is simpler then trying to change the command passed to the command scheduler
    it is also low-key a skitzo way of doing things but that's what I do best
    */

    private double DriveAngle(Boolean locked) {
        if (locked) {
            if (Vision.INSTANCE.HasAprilTagInSightBlue()) {
                return getTargetRotationPower();
            }
            else {
                return gamepad1.right_stick_x;
            }
        }
        else { return gamepad1.right_stick_x;}
    }

    // Needs to be a function so it can be called within JoyStick Commands
    private void SwitchLock() {
        Locked = !Locked;
    }
}
