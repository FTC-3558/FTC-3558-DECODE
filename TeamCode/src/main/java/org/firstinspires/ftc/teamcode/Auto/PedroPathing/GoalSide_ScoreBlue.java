package org.firstinspires.ftc.teamcode.Auto.PedroPathing;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Teleop.Commands.Auto_ScoreBlue;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallBlue;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallBlueAuto;
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

@Autonomous(name = "GoalSide_ScoreBlue")
public class GoalSide_ScoreBlue extends NextFTCOpMode {

    private Limelight3A limelight;
    private Paths paths;

    public GoalSide_ScoreBlue() {
        addComponents(
                new SubsystemComponent(Intake.INSTANCE, Shooter.INSTANCE, Shuffler.INSTANCE, Arm.INSTANCE, Vision.INSTANCE),
                BulkReadComponent.INSTANCE,
                // The PedroComponent is responsible for creating and holding the Follower instance
                new PedroComponent(Constants::createFollower)
        );

        // Pre-load the Shuffler state
        Shuffler.INSTANCE.storeColorInSlot(BallColor.PURPLE, 0);
        Shuffler.INSTANCE.storeColorInSlot(BallColor.GREEN, 1);
        Shuffler.INSTANCE.storeColorInSlot(BallColor.PURPLE, 2);
    }

    // --- Path Definitions ---
    public static class Paths {

        public static final Pose START_POSE = new Pose(33.48, 135.68, Math.toRadians(180));

        public PathChain LookAtMotif;
        public PathChain GoToShoot;
        public PathChain Leave;

        public Paths(Follower follower) {
            LookAtMotif = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(33.483, 135.684), new Pose(51.429, 96.948))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(63))
                    .build();

            GoToShoot = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(51.429, 96.948), new Pose(63.903, 81.629))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(63), Math.toRadians(133))
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(63.903, 81.629), new Pose(35.672, 72.657))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(133), Math.toRadians(180))
                    .build();
        }
    }

    public Command autonomousRoutine() {
        Shuffler.ballColors = new BallColor[]{BallColor.VOID, BallColor.VOID, BallColor.VOID};
        // 1. Hardware Initialization
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        Vision.INSTANCE.setLimelight(limelight);
        Servo servo = hardwareMap.get(Servo.class, "Shuffler");
        NormalizedColorSensor color1 = hardwareMap.get(NormalizedColorSensor.class, "Color1");
        NormalizedColorSensor color2 = hardwareMap.get(NormalizedColorSensor.class, "Color2");
        Shuffler.INSTANCE.sethardware(color1,color2, servo);

        // 2. Retrieve the initialized Follower from the PedroComponent
        Follower follower = PedroComponent.follower();

        // ** CRITICAL ADDITION: Set the initial pose for the follower **
        // This tells the pathing system where the robot starts relative to the path coordinates.
        follower.setStartingPose(Paths.START_POSE);

        // 3. Initialize Paths now that the Follower is ready
        this.paths = new Paths(follower);

        if (Vision.INSTANCE.getMotif() == "PPG") {
            return new SequentialGroup(
                    // 1. Drive the robot to the scoring position where the camera can see the tags.
                    new FollowPath(paths.LookAtMotif),

                    // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                    new Delay(0.5),

                    // 2. Drive to the shooting area.
                    new FollowPath(paths.GoToShoot),
                    new Score_BallBlueAuto(0, 1.5),
                    new Score_BallBlueAuto(2, 1),
                    new Score_BallBlueAuto(1, 1),

                    new FollowPath(paths.Leave)

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()
            );
        }
        else if (Vision.INSTANCE.getMotif() == "PGP") {
            return new SequentialGroup(
                    // 1. Drive the robot to the scoring position where the camera can see the tags.
                    new FollowPath(paths.LookAtMotif),

                    // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                    new Delay(0.5),

                    // 2. Drive to the shooting area.
                    new FollowPath(paths.GoToShoot),

                    new Score_BallBlueAuto(0, 1.5),
                    new Score_BallBlueAuto(1, 1),
                    new Score_BallBlueAuto(2, 1),

                    new FollowPath(paths.Leave)

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()
            );
        }
        else {
            return new SequentialGroup(
                    // 1. Drive the robot to the scoring position where the camera can see the tags.
                    new FollowPath(paths.LookAtMotif),

                    // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                    new Delay(0.5),

                    // 2. Drive to the shooting area.
                    new FollowPath(paths.GoToShoot),

                    new Score_BallBlueAuto(1, 1.5),
                    new Score_BallBlueAuto(0, 1),
                    new Score_BallBlueAuto(2, 1),

                    new FollowPath(paths.Leave)

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()
            );
        }
    }

    @Override
    public void onUpdate() {
        // Since Pinpoint is assumed to be running and feeding data back to the
        // follower, the pose estimation is automatically updated here.
        Vision.INSTANCE.UpdateMotif();
    }

    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }
}