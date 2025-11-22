package org.firstinspires.ftc.teamcode.Auto.PedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Teleop.Commands.Auto_ScoreBlue;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Auto_ScoreRed;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallBlue;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallRed;
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

@Autonomous(name = "GoalSide_Score")
public class GoalSide_ScoreRed extends NextFTCOpMode {

    private Limelight3A limelight;
    private Paths paths;

    public GoalSide_ScoreRed() {
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
        // Define the exact starting pose from the field setup
        public static final Pose START_POSE = new Pose(110, 135.257, Math.toRadians(0));

        public final PathChain Start_BackUp_Turn;
        public final PathChain Leave_ToShoot;

        public Paths(Follower follower) {
            Start_BackUp_Turn = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(START_POSE, new Pose(96.193, 95.903))
                    )
                    .setLinearHeadingInterpolation(START_POSE.getHeading(), Math.toRadians(110))
                    .build();

            Leave_ToShoot = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(96.193, 95.903), new Pose(77.650, 77.940))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(110), Math.toRadians(45))
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
                    new FollowPath(paths.Start_BackUp_Turn),

                    // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                    new Delay(1),

                    // 2. Drive to the shooting area.
                    new FollowPath(paths.Leave_ToShoot),

                    new Score_BallRed(0),
                    new Score_BallRed(2),
                    new Score_BallRed(1)

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()
            );
        }
        else if (Vision.INSTANCE.getMotif() == "PGP") {
            return new SequentialGroup(
                    // 1. Drive the robot to the scoring position where the camera can see the tags.
                    new FollowPath(paths.Start_BackUp_Turn),

                    // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                    new Delay(1),

                    // 2. Drive to the shooting area.
                    new FollowPath(paths.Leave_ToShoot),

                    new Score_BallRed(0),
                    new Score_BallRed(1),
                    new Score_BallRed(2)

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()
            );
        }
        else {
            return new SequentialGroup(
                    // 1. Drive the robot to the scoring position where the camera can see the tags.
                    new FollowPath(paths.Start_BackUp_Turn),

                    // Optional: Wait briefly for the vision reading to stabilize after stopping the drive train.
                    new Delay(1),

                    // 2. Drive to the shooting area.
                    new FollowPath(paths.Leave_ToShoot),

                    new Score_BallRed(1),
                    new Score_BallRed(0),
                    new Score_BallRed(2)

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