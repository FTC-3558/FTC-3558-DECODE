package org.firstinspires.ftc.teamcode.Auto.PedroPathing;

import static org.firstinspires.ftc.teamcode.Auto.PedroPathing.Tuning.follower;

import com.qualcomm.hardware.limelightvision.Limelight3A; // NEW: Limelight Import
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision; // NEW: Vision Subsystem Import

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup; // NEW: To run actions simultaneously
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import static dev.nextftc.extensions.pedro.PedroComponent.follower; // Static import for the follower instance

@Autonomous(name = "Basic_Score_Vision") // Renamed the OpMode for clarity
public class Basic_Score extends NextFTCOpMode {

    private Limelight3A limelight; // Limelight field
    private Vision vision; // Vision subsystem field
    private final Paths paths;

    public Basic_Score() {
        // 1. Initialize Limelight and Vision
        // NOTE: Ensure "limelight3a" matches your configuration file!
        // We use get(Limelight3A.class, "limelight3a") to pull the component from the hardware map.
        limelight = hardwareMap.get(Limelight3A.class, "limelight3a");
        vision = new Vision(limelight);

        // Add components first
        addComponents(
                // Include the Vision subsystem in the component list
                new SubsystemComponent(Intake.INSTANCE, Shooter.INSTANCE, Shuffler.INSTANCE, vision),
                BulkReadComponent.INSTANCE,
                new PedroComponent(Constants::createFollower)
        );

        // 2. Initialize Paths
        this.paths = new Paths(follower);
    }

    // --- Placeholder Commands for Motif Actions ---
    // In a real program, you would replace these with commands that control your robot's mechanisms.

    // A command for the GPP scoring action (requires the Vision subsystem)
    private Command getGPPCommand() {
        // Requires the Vision subsystem, runs for max 1.5 seconds
        return new InstantCommand();
    }

    // A command for the PGP scoring action
    private Command getPGPCommand() {
        return new InstantCommand();
    }

    // A command for the PPG scoring action
    private Command getPPGCommand() {
        return new InstantCommand();
    }

    // Default command if no tag is found ("Void")
    private Command getDefaultCommand() {
        return new InstantCommand();
    }

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

    private Command selectScoringCommand() {
        String motif = vision.getMotif();
        telemetry.addData("Vision Detected Motif", motif);

        switch (motif) {
            case "GPP":
                return getGPPCommand();
            case "PGP":
                return getPGPCommand();
            case "PPG":
                return getPPGCommand();
            default: // Catches "Void" or any other unexpected result
                return getDefaultCommand();
        }
    }
    private Command autonomousRoutine() {

        Command conditionalScoringCommand = selectScoringCommand();

        return new SequentialGroup(
                // 1. Drive the robot to the scoring position where the camera can see the tags.
                new FollowPath(paths.Start_BackUp_Turn),

                // 2. Execute two commands in parallel:
                //    - The chosen scoring command (GPP/PGP/PPG/Void)
                //    - The second path (driving to the shooting area)
                new ParallelGroup(
                        conditionalScoringCommand,
                        new FollowPath(paths.Leave_ToShoot)
                )
        );
    }
}