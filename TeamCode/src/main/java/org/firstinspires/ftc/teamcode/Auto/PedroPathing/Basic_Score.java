package org.firstinspires.ftc.teamcode.Auto.PedroPathing;

import static org.firstinspires.ftc.teamcode.Auto.PedroPathing.Tuning.follower;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.teamcode.Teleop.Commands.Auto_Score;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler.BallColor;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@Autonomous(name = "Basic_Score_Vision")
public class Basic_Score extends NextFTCOpMode {

    private Limelight3A limelight;
    private final Vision vision = Vision.INSTANCE;
    private final Paths paths;

    public Basic_Score() {

        // --- HARDWARE INJECTION FIXES ---

        // 1. Vision: Initialize Limelight and pass it to the Vision singleton
        limelight = hardwareMap.get(Limelight3A.class, "limelight3a");
        vision.setLimelight(limelight);

        // 2. Shuffler: Initialize Color Sensor and pass it to the Shuffler singleton
        NormalizedColorSensor colorSensor = hardwareMap.get(NormalizedColorSensor.class, "ballColorSensor");
        Shuffler.INSTANCE.setColorSensor(colorSensor);

        // Add components
        addComponents(
                // Ensure all needed subsystems are added
                new SubsystemComponent(Intake.INSTANCE, Shooter.INSTANCE, Shuffler.INSTANCE, Arm.INSTANCE, vision),
                BulkReadComponent.INSTANCE,
                new PedroComponent(Constants::createFollower)
        );

        // 3. Initialize Paths
        this.paths = new Paths(follower);

        // 4. Pre-load the Shuffler state to simulate manually loaded balls for this autonomous.
        // We assume a fixed starting state of PGP: PURPLE in slot 0, GREEN in slot 1, PURPLE in slot 2.
        Shuffler.INSTANCE.storeColorInSlot(BallColor.PURPLE, 0);
        Shuffler.INSTANCE.storeColorInSlot(BallColor.GREEN, 1);
        Shuffler.INSTANCE.storeColorInSlot(BallColor.PURPLE, 2);
    }

    // The conditional scoring methods are now removed, as AutoScoreAllCommand handles the motif logic.


    // --- Path Definitions (Unchanged) ---
    public static class Paths {
        public final PathChain Start_BackUp_Turn;
        public final PathChain Leave_ToShoot;

        public Paths(Follower follower) {
            this.Start_BackUp_Turn = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(108.855, 134.981), new Pose(83.974, 120.052))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(360), Math.toRadians(90))
                    .build();
            this.Leave_ToShoot = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(95.480, 119.643), new Pose(95.675, 96.260))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(45))
                    .build();
        }
    }

    public Command autonomousRoutine() {

        return new SequentialGroup(
                // 1. Drive the robot to the scoring position where the camera can see the tags.
                new FollowPath(paths.Start_BackUp_Turn),

                // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                new Delay(250),

                // 2. Drive to the shooting area.
                new FollowPath(paths.Leave_ToShoot),

                // 3. Score ALL three pre-loaded balls based on the detected motif.
                new Auto_Score()
        );
    }

    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }
}