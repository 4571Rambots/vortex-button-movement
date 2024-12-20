package frc.robot;

import frc.robot.subsystems.Motor;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class RobotContainer {
    private final Motor motorSubsystem = new Motor(Constants.KRACKEN_MOTOR_CAN_ID);
    private final XboxController xboxController = new XboxController(Constants.XBOX_CONTROLLER_PORT);

    public RobotContainer() {
        configureButtonBindings();

        // Default command for teleop mode
        motorSubsystem.setDefaultCommand(
            new RunCommand(() -> {
                double speed = -xboxController.getLeftY();

                // Apply deadband
                if (Math.abs(speed) < Constants.DEADBAND_THRESHOLD) {
                    speed = 0;
                }

                motorSubsystem.setMotorSpeed(speed * 0.5); // Scale speed
            }, motorSubsystem)
        );
    }

    private void configureButtonBindings() {
        // Press B to move the motor forward
        new JoystickButton(xboxController, XboxController.Button.kB.value)
            .whileTrue(new RunCommand(() -> motorSubsystem.setMotorSpeed(Constants.AUTO_SPEED), motorSubsystem))
            .onFalse(new RunCommand(motorSubsystem::stopMotor, motorSubsystem));

        // Press A to move the motor backward
        new JoystickButton(xboxController, XboxController.Button.kA.value)
            .whileTrue(new RunCommand(() -> motorSubsystem.setMotorSpeed(-Constants.AUTO_SPEED), motorSubsystem))
            .onFalse(new RunCommand(motorSubsystem::stopMotor, motorSubsystem));
    }

    public Command getTeleopCommand() {
        // Ensure that the default behavior of the subsystem works in teleop
        return new RunCommand(() -> {
            double speed = -xboxController.getLeftY();

            // Apply deadband
            if (Math.abs(speed) < Constants.DEADBAND_THRESHOLD) {
                speed = 0;
            }

            motorSubsystem.setMotorSpeed(speed * 0.5); // Scale speed
        }, motorSubsystem);
    }

    public Command getAutonomousCommand() {
        return new RunCommand(() -> motorSubsystem.setMotorSpeed(Constants.AUTO_SPEED), motorSubsystem)
            .andThen(new RunCommand(motorSubsystem::stopMotor, motorSubsystem))
            .withTimeout(Constants.AUTO_DURATION);
    }
}
