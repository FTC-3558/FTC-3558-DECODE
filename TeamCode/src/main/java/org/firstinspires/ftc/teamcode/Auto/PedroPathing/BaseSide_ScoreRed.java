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
import org.firstinspires.ftc.teamcode.Teleop.Commands.Intake_Store;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallBlue;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallRed;
import org.firstinspires.ftc.teamcode.Teleop.Commands.Score_BallRedAuto;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler.BallColor;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
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
    private final Intake intake = Intake.INSTANCE;

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

        public static final Pose START_POSE = new Pose(91.039, 8.534, Math.toRadians(90));

        public PathChain GotoShoot;
        public PathChain Goto1stLine;
        public PathChain PickUp1;
        public PathChain PickUp2;
        public PathChain PickUp3;
        public PathChain GotoShoot2;
        public PathChain Leave;

        public Paths(Follower follower) {
            GotoShoot = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(91.040, 8.535), new Pose(80.097, 14.881))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(60))
                    .build();

            Goto1stLine = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(80.097, 14.881), new Pose(104.170, 35.234))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(60), Math.toRadians(0))
                    .build();

            PickUp1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(104.170, 35.234), new Pose(115.112, 35.015))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            PickUp2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(115.112, 35.015), new Pose(122.991, 35.015))
                    )
                    .setTangentHeadingInterpolation()
                    .build();

            PickUp3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(122.991, 35.015), new Pose(131.526, 35.015))
                    )
                    .setTangentHeadingInterpolation()
                    .build();

            GotoShoot2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(131.526, 35.015), new Pose(80.316, 15.100))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(60))
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(80.316, 15.100), new Pose(104.170, 59.526))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(60), Math.toRadians(0))
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

        if (Vision.INSTANCE.getMotif() == "PPG") {
            return new SequentialGroup(
                    // 2. Drive to the shooting area.
                    new FollowPath(paths.GotoShoot),

                    new Score_BallRedAuto(0, 2),
                    new Score_BallRedAuto(2, 1.2),
                    new Score_BallRedAuto(1, 1.2),

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()
                    new FollowPath(paths.Goto1stLine),

                    new FollowPath(paths.PickUp1),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.PickUp2),

                    new ParallelGroup(
                            new Intake_Store()

                    ),

                    new FollowPath(paths.PickUp3),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.GotoShoot2),

                    new Score_BallRedAuto(0, 2),
                    new Score_BallRedAuto(2, 1),
                    new Score_BallRedAuto(1, 1),

                    new FollowPath(paths.Leave)
            );
        }
        else if (Vision.INSTANCE.getMotif() == "PGP") {
            return new SequentialGroup(
                    // 2. Drive to the shooting area.
                    new FollowPath(paths.GotoShoot),

                    new Score_BallRedAuto(0, 2),
                    new Score_BallRedAuto(1, 1),
                    new Score_BallRedAuto(2, 1),

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()

                    new FollowPath(paths.Goto1stLine),

                    new FollowPath(paths.Goto1stLine),

                    new FollowPath(paths.PickUp1),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.PickUp2),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.PickUp3),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.GotoShoot2),

                    new Score_BallRedAuto(0, 2),
                    new Score_BallRedAuto(1, 1),
                    new Score_BallRedAuto(2, 1),

                    new FollowPath(paths.Leave)
            );
        }
        else {
            return new SequentialGroup(
                    // 2. Drive to the shooting area.
                    new FollowPath(paths.GotoShoot),

                    new Score_BallRedAuto(1, 2),
                    new Score_BallRedAuto(0, 1),
                    new Score_BallRedAuto(2, 1),

                    // 3. Score ALL three pre-loaded balls based on the detected motif.
                    // new Auto_Score()

                    new FollowPath(paths.Goto1stLine),

                    new FollowPath(paths.Goto1stLine),

                    new FollowPath(paths.PickUp1),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.PickUp2),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.PickUp3),

                    new ParallelGroup(
                            new Intake_Store()
                    ),

                    new FollowPath(paths.GotoShoot2),

                    new Score_BallRedAuto(1, 2),
                    new Score_BallRedAuto(0, 1),
                    new Score_BallRedAuto(2, 1),

                    new FollowPath(paths.Leave)
            );
        }
    }

    @Override
    public void onUpdate() {

        Vision.INSTANCE.UpdateMotif();
        telemetry.addData("Motif: ", Vision.INSTANCE.Motif);
    }

    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }
}