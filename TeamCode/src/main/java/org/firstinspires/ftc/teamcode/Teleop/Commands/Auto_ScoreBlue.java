package org.firstinspires.ftc.teamcode.Teleop.Commands;

import android.annotation.SuppressLint;

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

public class Auto_ScoreBlue extends Command {

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
    @SuppressLint("NewApi")
    public Auto_ScoreBlue() {
        // Step 1: Initialize a list to hold the dynamic sequence of commands
        List<Command> dynamicCommands = new ArrayList<>();

        // Step 2: Check if all slots are full and determine the score order.
        if (shuffler.getBallColors().contains(BallColor.VOID)) {
            // If not all slots are full, Proceed to shoot all available balls.
            List<BallColor> ballState = shuffler.getBallColors();
            for (int i = 0; i < ballState.size(); i++) {
                if (ballState.get(i) != BallColor.VOID) {
                    dynamicCommands.add(new Score_BallBlue(i));
                }
            }
        }

// 💥 CRITICAL FIX: Ensure the command list is NOT empty. 💥
        if (dynamicCommands.isEmpty()) {
            // If the list is empty (e.g., Shuffler is completely empty, or vision scoring failed to match),
            // add a command that immediately finishes without doing anything (a NoOp command).
            dynamicCommands.add(new Delay(.1));
        }

// ... (Rest of the array creation and SequenceGroup creation remains the same)
        else {
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
                    dynamicCommands.add(new Score_BallBlue(slotIndex));

                    // IMPORTANT: Mark the ball in the *local state* as VOID so we don't try to shoot it again.
                    currentBallState.set(slotIndex, BallColor.VOID);
                }
            }

            // Step 5: After scoring all three balls, rotate the shuffler to an empty slot for the next intake.
            dynamicCommands.add(new InstantCommand(shuffler::rotateToEmptySlotForIntake));
        }

        int size = dynamicCommands.size();
        Command[] commandsArray = new Command[size];

        for (int i = 0; i < size; i++) {
            commandsArray[i] = dynamicCommands.get(i);
        }

        // Pass the manually built array to the helper method.
        this.sequence = createSequentialGroup(commandsArray);
    }

    // --- New Private Static Helper Method ---
    // This method is added to the Auto_Score class definition, outside of the constructor.
    private static SequentialGroup createSequentialGroup(Command[] commands) {
        // This method forces the varargs signature to be satisfied cleanly.
        return new SequentialGroup(commands);
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