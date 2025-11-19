package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

@TeleOp(name = "colorSensortest")
public class ColorSensorTest extends OpMode {
    NormalizedColorSensor cs;
    @Override
    public void init() {
        cs = hardwareMap.get(NormalizedColorSensor.class, "Color");
    }

    @Override
    public void loop() {
        telemetry.addData("red", cs.getNormalizedColors().red);
        telemetry.addData("Blue", cs.getNormalizedColors().blue);
        telemetry.addData("Green", cs.getNormalizedColors().green);
        telemetry.addData("Alpha", cs.getNormalizedColors().alpha);
        telemetry.update();
    }
}
