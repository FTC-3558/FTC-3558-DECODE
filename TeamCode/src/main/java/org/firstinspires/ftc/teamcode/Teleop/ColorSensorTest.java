package org.firstinspires.ftc.teamcode.Teleop;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import dev.nextftc.core.commands.delays.WaitUntil;

@TeleOp(name = "colorSensortest")
public class ColorSensorTest extends LinearOpMode {
    Servo servo;
    double pos = 0;
    @Override

    public void runOpMode() {
        NormalizedColorSensor color = hardwareMap.get(NormalizedColorSensor.class, "Color2");

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("alpha", color.getNormalizedColors().alpha);
            telemetry.addData("blue", color.getNormalizedColors().blue);
            telemetry.addData("green", color.getNormalizedColors().green);
            telemetry.addData("red", color.getNormalizedColors().red);
            telemetry.update();
        }
    }
}
