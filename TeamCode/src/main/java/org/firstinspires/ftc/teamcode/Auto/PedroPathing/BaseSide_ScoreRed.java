package org.firstinspires.ftc.teamcode.Auto.PedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Teleop.Commands.Auto_ScoreRed;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler.BallColor;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

// REMOVED: import static org.firstinspires.ftc.teamcode.Auto.PedroPathing.Tuning.follower;

@Autonomous(name = "BaseSide_ScoreRed")
public class BaseSide_ScoreRed extends NextFTCOpMode {

    private Limelight3A limelight;
    // Removed 'final' and initializer because it will be set in autonomousRoutine()
    private Paths paths;

    public BaseSide_ScoreRed() {

        // --- HARDWARE INJECTION FIXES ---
        // Limelight/Vision and ColorSensor/Shuffler initialization should be done in autonomousRoutine()
        // or a similar setup method, as hardwareMap is not fully ready in the constructor.

        // Add components
        addComponents(
                // Ensure all needed subsystems are added
                new SubsystemComponent(Intake.INSTANCE, Shooter.INSTANCE, Shuffler.INSTANCE, Arm.INSTANCE, Vision.INSTANCE),
                BulkReadComponent.INSTANCE,
                // The PedroComponent is responsible for creating and holding the Follower instance
                new PedroComponent(Constants::createFollower)
        );


        // 4. Pre-load the Shuffler state to simulate manually loaded balls for this autonomous.
        // We assume a fixed starting state of PGP: PURPLE in slot 0, GREEN in slot 1, PURPLE in slot 2.
        Shuffler.INSTANCE.storeColorInSlot(BallColor.PURPLE, 0);
        Shuffler.INSTANCE.storeColorInSlot(BallColor.GREEN, 1);
        Shuffler.INSTANCE.storeColorInSlot(BallColor.PURPLE, 2);
    }

    // --- Path Definitions ---
    public static class Paths {

        public static final Pose START_POSE = new Pose(87.5, 8.5, Math.toRadians(180));
        public final PathChain Rotate_to_Motif;
        public final PathChain Rotate_to_Score;

        public Paths(Follower follower) {
            Rotate_to_Motif = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(START_POSE, new Pose(87.500, 22.200))
                    )
                    .setLinearHeadingInterpolation(START_POSE.getHeading(), Math.toRadians(100))
                    .build();

            Rotate_to_Score = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(87.500, 22.200), new Pose(83.155, 22.020))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(100), Math.toRadians(45))
                    .build();
        }
    }

    public Command autonomousRoutine() {
        Shuffler.ballColors = new BallColor[]{BallColor.VOID, BallColor.VOID, BallColor.VOID};

        // 1. Hardware Initialization (Moved from constructor, as it uses hardwareMap)
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        Servo servo = hardwareMap.get(Servo.class, "Shuffler");
        Vision.INSTANCE.setLimelight(limelight);

        NormalizedColorSensor color2 = hardwareMap.get(NormalizedColorSensor.class, "Color2");
        NormalizedColorSensor color1 = hardwareMap.get(NormalizedColorSensor.class, "Color1");
        Shuffler.INSTANCE.sethardware(color1, color2, servo);

        // 2. FIX: Retrieve the initialized Follower from the PedroComponent
        Follower follower = PedroComponent.follower();
        follower.setStartingPose(Paths.START_POSE);

        // 3. Initialize Paths now that the Follower is ready
        this.paths = new Paths(follower);

        return new SequentialGroup(
                // 1. Drive the robot to the scoring position where the camera can see the tags.
                new FollowPath(paths.Rotate_to_Motif),

                // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                new Delay(1),

                // 2. Drive to the shooting area.
                new FollowPath(paths.Rotate_to_Score),

                // 3. Score ALL three pre-loaded balls based on the detected motif.
               new Auto_ScoreRed()
        );
    }

    @Override
    public void onUpdate() {
        Vision.INSTANCE.UpdateMotif();
    }

    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }
}