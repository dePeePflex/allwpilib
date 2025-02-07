// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// THIS FILE WAS AUTO-GENERATED BY ./wpilibj/generate_hids.py. DO NOT MODIFY

package edu.wpi.first.wpilibj;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.event.BooleanEvent;
import edu.wpi.first.wpilibj.event.EventLoop;

/**
 * Handle input from Xbox controllers connected to the Driver Station.
 *
 * <p>This class handles Xbox input that comes from the Driver Station. Each time a value is
 * requested the most recent value is returned. There is a single class instance for each controller
 * and the mapping of ports to hardware buttons depends on the code in the Driver Station.
 *
 * <p>Only first party controllers from Microsoft are guaranteed to have the correct mapping, and
 * only through the official NI DS. Sim is not guaranteed to have the same mapping, as well as any
 * 3rd party controllers.
 */
public class XboxController extends GenericHID implements Sendable {
  /** Represents a digital button on a XboxController. */
  public enum Button {
    /** A button. */
    kA(1),
    /** B button. */
    kB(2),
    /** X button. */
    kX(3),
    /** Y button. */
    kY(4),
    /** Left bumper button. */
    kLeftBumper(5),
    /** Right bumper button. */
    kRightBumper(6),
    /** Back button. */
    kBack(7),
    /** Start button. */
    kStart(8),
    /** Left stick button. */
    kLeftStick(9),
    /** Right stick button. */
    kRightStick(10);

    /** Button value. */
    public final int value;

    Button(int value) {
      this.value = value;
    }

    /**
     * Get the human-friendly name of the button, matching the relevant methods. This is done by
     * stripping the leading `k`, and appending `Button`.
     *
     * <p>Primarily used for automated unit tests.
     *
     * @return the human-friendly name of the button.
     */
    @Override
    public String toString() {
      // Remove leading `k`
      return this.name().substring(1) + "Button";
    }
  }

  /** Represents an axis on an XboxController. */
  public enum Axis {
    /** Left X axis. */
    kLeftX(0),
    /** Right X axis. */
    kRightX(4),
    /** Left Y axis. */
    kLeftY(1),
    /** Right Y axis. */
    kRightY(5),
    /** Left trigger. */
    kLeftTrigger(2),
    /** Right trigger. */
    kRightTrigger(3);

    /** Axis value. */
    public final int value;

    Axis(int value) {
      this.value = value;
    }

    /**
     * Get the human-friendly name of the axis, matching the relevant methods. This is done by
     * stripping the leading `k`, and appending `Axis` if the name ends with `Trigger`.
     *
     * <p>Primarily used for automated unit tests.
     *
     * @return the human-friendly name of the axis.
     */
    @Override
    public String toString() {
      var name = this.name().substring(1); // Remove leading `k`
      if (name.endsWith("Trigger")) {
        return name + "Axis";
      }
      return name;
    }
  }

  /**
   * Construct an instance of a controller.
   *
   * @param port The port index on the Driver Station that the controller is plugged into (0-5).
   */
  public XboxController(final int port) {
    super(port);
    HAL.reportUsage("HID", port, "XboxController");
  }

  /**
   * Get the X axis value of left side of the controller.
   *
   * @return The axis value.
   */
  public double getLeftX() {
    return getRawAxis(Axis.kLeftX.value);
  }

  /**
   * Get the X axis value of right side of the controller.
   *
   * @return The axis value.
   */
  public double getRightX() {
    return getRawAxis(Axis.kRightX.value);
  }

  /**
   * Get the Y axis value of left side of the controller.
   *
   * @return The axis value.
   */
  public double getLeftY() {
    return getRawAxis(Axis.kLeftY.value);
  }

  /**
   * Get the Y axis value of right side of the controller.
   *
   * @return The axis value.
   */
  public double getRightY() {
    return getRawAxis(Axis.kRightY.value);
  }

  /**
   * Get the left trigger axis value of the controller. Note that this axis is bound to the
   * range of [0, 1] as opposed to the usual [-1, 1].
   *
   * @return The axis value.
   */
  public double getLeftTriggerAxis() {
    return getRawAxis(Axis.kLeftTrigger.value);
  }

  /**
   * Constructs an event instance around the axis value of the left trigger. The returned trigger
   * will be true when the axis value is greater than {@code threshold}.
   *
   * @param threshold the minimum axis value for the returned {@link BooleanEvent} to be true. This
   *     value should be in the range [0, 1] where 0 is the unpressed state of the axis.
   * @param loop the event loop instance to attach the event to.
   * @return an event instance that is true when the left trigger's axis exceeds the provided
   *     threshold, attached to the given event loop
   */
  public BooleanEvent leftTrigger(double threshold, EventLoop loop) {
    return axisGreaterThan(Axis.kLeftTrigger.value, threshold, loop);
  }

  /**
   * Constructs an event instance around the axis value of the left trigger. The returned trigger
   * will be true when the axis value is greater than 0.5.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance that is true when the left trigger's axis exceeds the provided
   *     threshold, attached to the given event loop
   */
  public BooleanEvent leftTrigger(EventLoop loop) {
    return leftTrigger(0.5, loop);
  }

  /**
   * Get the right trigger axis value of the controller. Note that this axis is bound to the
   * range of [0, 1] as opposed to the usual [-1, 1].
   *
   * @return The axis value.
   */
  public double getRightTriggerAxis() {
    return getRawAxis(Axis.kRightTrigger.value);
  }

  /**
   * Constructs an event instance around the axis value of the right trigger. The returned trigger
   * will be true when the axis value is greater than {@code threshold}.
   *
   * @param threshold the minimum axis value for the returned {@link BooleanEvent} to be true. This
   *     value should be in the range [0, 1] where 0 is the unpressed state of the axis.
   * @param loop the event loop instance to attach the event to.
   * @return an event instance that is true when the right trigger's axis exceeds the provided
   *     threshold, attached to the given event loop
   */
  public BooleanEvent rightTrigger(double threshold, EventLoop loop) {
    return axisGreaterThan(Axis.kRightTrigger.value, threshold, loop);
  }

  /**
   * Constructs an event instance around the axis value of the right trigger. The returned trigger
   * will be true when the axis value is greater than 0.5.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance that is true when the right trigger's axis exceeds the provided
   *     threshold, attached to the given event loop
   */
  public BooleanEvent rightTrigger(EventLoop loop) {
    return rightTrigger(0.5, loop);
  }

  /**
   * Read the value of the A button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getAButton() {
    return getRawButton(Button.kA.value);
  }

  /**
   * Whether the A button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getAButtonPressed() {
    return getRawButtonPressed(Button.kA.value);
  }

  /**
   * Whether the A button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getAButtonReleased() {
    return getRawButtonReleased(Button.kA.value);
  }

  /**
   * Constructs an event instance around the A button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the A button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent a(EventLoop loop) {
    return button(Button.kA.value, loop);
  }

  /**
   * Read the value of the B button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getBButton() {
    return getRawButton(Button.kB.value);
  }

  /**
   * Whether the B button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getBButtonPressed() {
    return getRawButtonPressed(Button.kB.value);
  }

  /**
   * Whether the B button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getBButtonReleased() {
    return getRawButtonReleased(Button.kB.value);
  }

  /**
   * Constructs an event instance around the B button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the B button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent b(EventLoop loop) {
    return button(Button.kB.value, loop);
  }

  /**
   * Read the value of the X button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getXButton() {
    return getRawButton(Button.kX.value);
  }

  /**
   * Whether the X button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getXButtonPressed() {
    return getRawButtonPressed(Button.kX.value);
  }

  /**
   * Whether the X button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getXButtonReleased() {
    return getRawButtonReleased(Button.kX.value);
  }

  /**
   * Constructs an event instance around the X button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the X button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent x(EventLoop loop) {
    return button(Button.kX.value, loop);
  }

  /**
   * Read the value of the Y button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getYButton() {
    return getRawButton(Button.kY.value);
  }

  /**
   * Whether the Y button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getYButtonPressed() {
    return getRawButtonPressed(Button.kY.value);
  }

  /**
   * Whether the Y button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getYButtonReleased() {
    return getRawButtonReleased(Button.kY.value);
  }

  /**
   * Constructs an event instance around the Y button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the Y button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent y(EventLoop loop) {
    return button(Button.kY.value, loop);
  }

  /**
   * Read the value of the left bumper button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getLeftBumperButton() {
    return getRawButton(Button.kLeftBumper.value);
  }

  /**
   * Whether the left bumper button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getLeftBumperButtonPressed() {
    return getRawButtonPressed(Button.kLeftBumper.value);
  }

  /**
   * Whether the left bumper button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getLeftBumperButtonReleased() {
    return getRawButtonReleased(Button.kLeftBumper.value);
  }

  /**
   * Constructs an event instance around the left bumper button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the left bumper button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent leftBumper(EventLoop loop) {
    return button(Button.kLeftBumper.value, loop);
  }

  /**
   * Read the value of the right bumper button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getRightBumperButton() {
    return getRawButton(Button.kRightBumper.value);
  }

  /**
   * Whether the right bumper button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getRightBumperButtonPressed() {
    return getRawButtonPressed(Button.kRightBumper.value);
  }

  /**
   * Whether the right bumper button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getRightBumperButtonReleased() {
    return getRawButtonReleased(Button.kRightBumper.value);
  }

  /**
   * Constructs an event instance around the right bumper button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the right bumper button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent rightBumper(EventLoop loop) {
    return button(Button.kRightBumper.value, loop);
  }

  /**
   * Read the value of the back button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getBackButton() {
    return getRawButton(Button.kBack.value);
  }

  /**
   * Whether the back button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getBackButtonPressed() {
    return getRawButtonPressed(Button.kBack.value);
  }

  /**
   * Whether the back button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getBackButtonReleased() {
    return getRawButtonReleased(Button.kBack.value);
  }

  /**
   * Constructs an event instance around the back button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the back button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent back(EventLoop loop) {
    return button(Button.kBack.value, loop);
  }

  /**
   * Read the value of the start button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getStartButton() {
    return getRawButton(Button.kStart.value);
  }

  /**
   * Whether the start button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getStartButtonPressed() {
    return getRawButtonPressed(Button.kStart.value);
  }

  /**
   * Whether the start button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getStartButtonReleased() {
    return getRawButtonReleased(Button.kStart.value);
  }

  /**
   * Constructs an event instance around the start button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the start button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent start(EventLoop loop) {
    return button(Button.kStart.value, loop);
  }

  /**
   * Read the value of the left stick button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getLeftStickButton() {
    return getRawButton(Button.kLeftStick.value);
  }

  /**
   * Whether the left stick button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getLeftStickButtonPressed() {
    return getRawButtonPressed(Button.kLeftStick.value);
  }

  /**
   * Whether the left stick button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getLeftStickButtonReleased() {
    return getRawButtonReleased(Button.kLeftStick.value);
  }

  /**
   * Constructs an event instance around the left stick button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the left stick button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent leftStick(EventLoop loop) {
    return button(Button.kLeftStick.value, loop);
  }

  /**
   * Read the value of the right stick button on the controller.
   *
   * @return The state of the button.
   */
  public boolean getRightStickButton() {
    return getRawButton(Button.kRightStick.value);
  }

  /**
   * Whether the right stick button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  public boolean getRightStickButtonPressed() {
    return getRawButtonPressed(Button.kRightStick.value);
  }

  /**
   * Whether the right stick button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  public boolean getRightStickButtonReleased() {
    return getRawButtonReleased(Button.kRightStick.value);
  }

  /**
   * Constructs an event instance around the right stick button's digital signal.
   *
   * @param loop the event loop instance to attach the event to.
   * @return an event instance representing the right stick button's digital signal
   *     attached to the given loop.
   */
  public BooleanEvent rightStick(EventLoop loop) {
    return button(Button.kRightStick.value, loop);
  }

  /**
   * Read the value of the left bumper (LB) button on the controller.
   *
   * @return The state of the button.
   * @deprecated Use {@link getLeftBumperButton} instead. This function is deprecated for removal
   *     to make function names consistent to allow the HID classes to be automatically generated.
   */
  @Deprecated(since = "2025", forRemoval = true)
  public boolean getLeftBumper() {
    return getRawButton(Button.kLeftBumper.value);
  }

  /**
   * Read the value of the right bumper (RB) button on the controller.
   *
   * @return The state of the button.
   * @deprecated Use {@link getRightBumperButton} instead. This function is deprecated for removal
   *     to make function names consistent to allow the HID classes to be automatically generated.
   */
  @Deprecated(since = "2025", forRemoval = true)
  public boolean getRightBumper() {
    return getRawButton(Button.kRightBumper.value);
  }

  /**
   * Whether the left bumper (LB) was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   * @deprecated Use {@link getLeftBumperButtonPressed} instead. This function is deprecated for
   *     removal to make function names consistent to allow the HID classes to be automatically
   *     generated.
   */
  @Deprecated(since = "2025", forRemoval = true)
  public boolean getLeftBumperPressed() {
    return getRawButtonPressed(Button.kLeftBumper.value);
  }

  /**
   * Whether the right bumper (RB) was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   * @deprecated Use {@link getRightBumperButtonPressed} instead. This function is deprecated for
   *     removal to make function names consistent to allow the HID classes to be automatically
   *     generated.
   */
  @Deprecated(since = "2025", forRemoval = true)
  public boolean getRightBumperPressed() {
    return getRawButtonPressed(Button.kRightBumper.value);
  }

  /**
   * Whether the left bumper (LB) was released since the last check.
   *
   * @return Whether the button was released since the last check.
   * @deprecated Use {@link getLeftBumperButtonReleased} instead. This function is deprecated for
   *     removal to make function names consistent to allow the HID classes to be automatically
   *     generated.
   */
  @Deprecated(since = "2025", forRemoval = true)
  public boolean getLeftBumperReleased() {
    return getRawButtonReleased(Button.kLeftBumper.value);
  }

  /**
   * Whether the right bumper (RB) was released since the last check.
   *
   * @return Whether the button was released since the last check.
   * @deprecated Use {@link getRightBumperButtonReleased} instead. This function is deprecated for
   *     removal to make function names consistent to allow the HID classes to be automatically
   *     generated.
   */
  @Deprecated(since = "2025", forRemoval = true)
  public boolean getRightBumperReleased() {
    return getRawButtonReleased(Button.kRightBumper.value);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("HID");
    builder.publishConstString("ControllerType", "Xbox");
    builder.addDoubleProperty("LeftTrigger", this::getLeftTriggerAxis, null);
    builder.addDoubleProperty("RightTrigger", this::getRightTriggerAxis, null);
    builder.addDoubleProperty("LeftX", this::getLeftX, null);
    builder.addDoubleProperty("RightX", this::getRightX, null);
    builder.addDoubleProperty("LeftY", this::getLeftY, null);
    builder.addDoubleProperty("RightY", this::getRightY, null);
    builder.addBooleanProperty("A", this::getAButton, null);
    builder.addBooleanProperty("B", this::getBButton, null);
    builder.addBooleanProperty("X", this::getXButton, null);
    builder.addBooleanProperty("Y", this::getYButton, null);
    builder.addBooleanProperty("LeftBumper", this::getLeftBumperButton, null);
    builder.addBooleanProperty("RightBumper", this::getRightBumperButton, null);
    builder.addBooleanProperty("Back", this::getBackButton, null);
    builder.addBooleanProperty("Start", this::getStartButton, null);
    builder.addBooleanProperty("LeftStick", this::getLeftStickButton, null);
    builder.addBooleanProperty("RightStick", this::getRightStickButton, null);
  }
}
