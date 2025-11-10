package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import java.util.List;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.subsystems.SubsystemGroup;

public class Sorting_System extends SubsystemGroup {
    public static final Sorting_System INSTANCE = new Sorting_System();
    public static List<Boolean> Occupied = List.of(false, false, false);

    public static List<String> Color = List.of(null, null, null);



    private Sorting_System() {
        super(
                Intake.INSTANCE,
                Shuffler.INSTANCE
        );
    }


    public class Intakestart extends Command {
        boolean stop = false;
        public Intakestart() {
            requires(Intake.INSTANCE, Shuffler.INSTANCE);
            setInterruptible(true);
        }
        @Override
        public boolean isDone() {
            return stop;
        }
        @Override
        public void start() {
            for (int i = 0; i < 3; i++) {
                if (Occupied.get(i) == false) {
                    switch (i) {
                        case 0: Shuffler.INSTANCE.IntakePos1.schedule();
                        case 1: Shuffler.INSTANCE.IntakePos2.schedule();
                        case 2: Shuffler.INSTANCE.IntakePos3.schedule();
                    }
                    break;
                }
            }

        }
        @Override
        public void update(){

        }
        @Override
        public void stop(boolean interrupted) {

        }

    }


    @Override
    public void initialize() {
        // initialization logic (runs on init)
    }

    @Override
    public void periodic() {
        // periodic logic (runs every loop)
    }

}