package org.firstinspires.ftc.teamcode.Teleop.Commands;

import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler.BallColor;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.delays.WaitUntil;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import java.util.Set;

/*
 * Commands the robot to rotate the shuffler to an empty slot, run the intake until a ball is detected,
 * then stops the intake and stores the detected color in that slot.
 * Requires: Intake and Shuffler
 */
public class Intake_Store extends Command {

    private final Intake intake = Intake.INSTANCE;
    private final Shuffler shuffler = Shuffler.INSTANCE;
    private SequentialGroup sequence;

    public Intake_Store() {
        // Initialize the internal sequence
        sequence = new SequentialGroup(
                // Step 1: Find the first empty slot and rotate the shuffler to align that slot for intake.
                shuffler.rotateToEmptySlotForIntake(),

                // Step 2: Start the intake roller.
                intake.on,

                //Delay Cause the Shufflers Slow
                new Delay(1000),

                // Step 3: CRITICAL: Wait until the sensor detects a ball.
                new WaitUntil(shuffler::isBallPresent).requires(shuffler),

                // Step 4: Stop the intake immediately upon detection.
                intake.off

                // Step 5: Replaced anonymous Command with RunOnceCommand to read sensor and store color
                //new InstantCommand(this::StoreSensorData).requires(shuffler)
        );
    }

    public void StoreSensorData () {
        BallColor color = shuffler.readAndDetermineColor();

        // Find the slot that the rotation command *just* aligned (the first VOID slot)
        int slotIndexToStore = shuffler.findSlotByColor(BallColor.VOID);

        if (slotIndexToStore != -1) {
            shuffler.storeColorInSlot(color, slotIndexToStore);
        }
    }

    // Command delegation methods (delegate all actions to the internal sequence)
    @Override
    public boolean isDone() {
        return sequence.isDone(); // whether or not the command is done
    }

    @Override
    public void start() {
        // executed when the command begins
        sequence.schedule();
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