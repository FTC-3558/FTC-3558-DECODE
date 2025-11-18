package org.firstinspires.ftc.teamcode.Teleop.Commands;

import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Shuffler.BallColor;
import org.firstinspires.ftc.teamcode.Teleop.SubSystems.Vision;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Auto_Score extends Command {

    private final Shuffler shuffler = Shuffler.INSTANCE;
    private final Vision vision = Vision.INSTANCE; //Fix
    private final SequentialGroup sequence;

    // This map defines the order of colors to score based on the Vision Motif.
    private static final List<BallColor> GPP_ORDER = Arrays.asList(BallColor.GREEN, BallColor.PURPLE, BallColor.PURPLE);
    private static final List<BallColor> PGP_ORDER = Arrays.asList(BallColor.PURPLE, BallColor.GREEN, BallColor.PURPLE);
    private static final List<BallColor> PPG_ORDER = Arrays.asList(BallColor.PURPLE, BallColor.PURPLE, BallColor.GREEN);
 //

    /*
     * Creates a command that checks for three balls, identifies the motif, and scores all three
     * in the sequence defined by the motif. This is ideal for TeleOp automation.
     * Requirements: Shuffler, Shooter, Arm, and Vision.
     */
    public Auto_Score() {
        // Step 1: Initialize a list to hold the dynamic sequence of commands
        List<Command> dynamicCommands = new ArrayList<>();

        // Step 2: Check if all slots are full and determine the score order.
        if (shuffler.getBallColors().contains(BallColor.VOID)) {
            // If not all slots are full, Proceed to shoot all available balls.
            dynamicCommands.add(new InstantCommand(new Delay(1)));
        } else {
            // Step 3: Determine the required scoring sequence based on Limelight data
            String motif = vision.getMotif();
            List<BallColor> scoreOrder;

            switch (motif) {
                case "GPP":
                    scoreOrder = GPP_ORDER;
                    break;
                case "PGP":
                    scoreOrder = PGP_ORDER;
                    break;
                case "PPG":
                    scoreOrder = PPG_ORDER;
                    break;
                default:
                    System.out.println("WARNING: Vision failed or unknown motif. Defaulting to PGP sequence.");
                    scoreOrder = PGP_ORDER;
                    break;
            }

            // Step 4: Build the scoring sequence dynamically
            // We use a mutable copy of the ball colors list to track which slots have been used.
            List<BallColor> currentBallState = new ArrayList<>(shuffler.getBallColors());

            for (BallColor targetColor : scoreOrder) {
                // Find the index of the first slot containing the target color in the *current* state.
                int slotIndex = currentBallState.indexOf(targetColor);

                if (slotIndex != -1) {
                    // Add the command to score the ball from the found slot.
                    dynamicCommands.add(new Score_Ball(slotIndex));

                    // IMPORTANT: Mark the ball in the *local state* as VOID so we don't try to shoot it again.
                    currentBallState.set(slotIndex, BallColor.VOID);
                }
            }

            // Step 5: After scoring all three balls, rotate the shuffler to an empty slot for the next intake.
            dynamicCommands.add(shuffler.rotateToEmptySlotForIntake());
        }

        // Final Step: Initialize the final SequentialGroup field by passing the dynamically built list.
        // We use toArray() to convert the List<Command> into a Command... array (varargs).
        this.sequence = new SequentialGroup(dynamicCommands.toArray(new Command[0]));
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