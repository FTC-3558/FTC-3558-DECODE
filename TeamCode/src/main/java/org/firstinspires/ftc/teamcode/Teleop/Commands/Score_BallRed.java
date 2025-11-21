package org.firstinspires.ftc.teamcode.Teleop.Commands;

import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;

// NOTE: The delay value in the ParallelGroup is 3 seconds, not 1.5 seconds as commented.
// I will keep it as 3 to match your code, but you may need to adjust it to 1.5.

public class Score_BallRed extends Command {

    private final Shooter shooter = Shooter.INSTANCE;
    private final Shuffler shuffler = Shuffler.INSTANCE;
    private final Arm arm = Arm.INSTANCE;
    private final SequentialGroup sequence;

    public Score_BallRed(int slotIndex) {
        // Initialize the internal sequence
        this.sequence = new SequentialGroup(
                // 1. Rotate the shuffler (using your non-command based method)
                new InstantCommand(()-> Shuffler.INSTANCE.rotateSlotToShoot(slotIndex)),

                // 2. Start the flywheel and wait for spin-up
                new ParallelGroup(
                        shooter.on(Vision.INSTANCE.getShooterPoweRed()), // Start Flywheel
                        new Delay(3) // Wait 3 seconds for flywheel to stabilize
                ).requires(shooter),

                // 3. Push the ball, wait, and reset the arm
                new SequentialGroup(
                        arm.up, // Push the ball with the lever arm
                        new Delay(1), // Wait 1 second for the ball to shoot (Original was 0.5s)
                        arm.down // Reset the lever arm
                ).requires(arm),

                // 4. Stop the flywheel and mark the slot as empty
                shooter.off,
                new InstantCommand(() -> shuffler.markSlotAsEmpty(slotIndex)),
                new Delay(.5)
        );

        // Add all required subsystems from the sequence's components to this parent command.
        // This is necessary for the scheduler to know Score_Ball needs control of Shuffler, Shooter, and Arm.
        this.addRequirements(shooter, shuffler, arm);
    }

    // Command delegation methods (delegate all actions to the internal sequence)
    @Override
    public boolean isDone() {
        return sequence.isDone(); // Whether or not the command is done
    }

    @Override
    public void start() {
        // CRITICAL FIX 1: Use sequence.start() instead of sequence.schedule()
        // This tells the internal sequence to begin immediately under the control of the parent.
        sequence.start();
    }

    @Override
    public void update() {
        // CRITICAL FIX 2: You MUST call update on the internal sequence on every loop
        // This drives the sequence, allowing it to move from one command (Delay) to the next.
        sequence.update();
    }

    @Override
    public void stop(boolean interrupted) {
        // CRITICAL FIX 3: Stop the internal sequence when the parent stops.
        sequence.stop(interrupted);
    }
}