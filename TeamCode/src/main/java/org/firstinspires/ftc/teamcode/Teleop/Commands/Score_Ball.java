package org.firstinspires.ftc.teamcode.Teleop.Commands;

import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Arm;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;

import dev.nextftc.core.commands.utility.InstantCommand;
import java.util.Set;

/**
 * Composite command to perform a single shot sequence.
 * This command assumes the target ball is in a known physical slot.
 * * Requirements: Shuffler, Shooter, Arm
 */
public class Score_Ball extends Command {

    private final Shooter shooter = Shooter.INSTANCE;
    private final Shuffler shuffler = Shuffler.INSTANCE;
    private final Arm arm = Arm.INSTANCE;
    private final SequentialGroup sequence;

    /**
     * Creates a sequential command to score the ball currently in the given slot index.
     * @param slotIndex The physical index (0, 1, or 2) of the ball to be scored.
     */
    public Score_Ball(int slotIndex) {
        // Initialize the internal sequence
        this.sequence = new SequentialGroup(
                // 1. Rotate the shuffler to align the target slot with the shooting mechanism
                shuffler.rotateSlotToShoot(slotIndex),

                // 2. Start the flywheel and wait for it to spin up (Parallel)
                new ParallelGroup(
                        shooter.on, // Start Flywheel
                        new Delay(1500) // Wait 1.5 seconds for flywheel to stabilize
                ).requires(shooter),

                // 3. Push the ball, wait, and reset the arm (Sequential)
                new SequentialGroup(
                        arm.up, // Push the ball with the lever arm
                        new Delay(500), // Wait 0.5s for the ball to shoot
                        arm.down // Reset the lever arm
                ).requires(arm),

                // 4. Stop the flywheel and mark the slot as empty
                shooter.off,
                new InstantCommand(() -> shuffler.markSlotAsEmpty(slotIndex))
        );
    }

    // Command delegation methods (delegate all actions to the internal sequence)
    @Override
    public boolean isDone() {
        return sequence.isDone(); // whether or not the command is done
    }

    @Override
    public void start() {
        // executed when the command begins
    }

    @Override
    public void update() {
        // executed on every update of the command
    }

    @Override
    public void stop(boolean interrupted) {
        // executed when the command ends
    }
}