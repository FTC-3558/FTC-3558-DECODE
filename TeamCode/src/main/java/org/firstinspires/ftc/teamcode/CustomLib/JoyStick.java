package org.firstinspires.ftc.teamcode.TeleOp.CustomLib;


public class JoyStick {
    public static void Command(boolean button, boolean goal, Runnable action) {
        if (button == goal) {
            action.run();
        }
    }
    public static void Command(double button, boolean goal, Runnable action) {
        if (button > .5 == goal) {
            action.run();
        }
    }
    public static void Command(double button, boolean button2, boolean goal, Runnable action) {
        if (button > .5 == goal && button2 == goal) {
            action.run();
        }
    }
    public static void Command(boolean button, boolean button2, boolean goal, Runnable action) {
        if (button == goal && button2 == goal) {
            action.run();
        }
    }
    public static void Command(double button, double button2, boolean goal, Runnable action) {
        if (button > .5 == goal && button2 > .5 == goal) {
            action.run();
        }
    }
}