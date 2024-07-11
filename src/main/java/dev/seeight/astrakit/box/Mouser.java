package dev.seeight.astrakit.box;

import org.lwjgl.glfw.GLFW;

public interface Mouser {
	double getX();

	double getY();

	default boolean isLeft(int code) {
		return code == GLFW.GLFW_MOUSE_BUTTON_1;
	}

	default boolean isRight(int code) {
		return code == GLFW.GLFW_MOUSE_BUTTON_2;
	}

	default boolean isMiddle(int code) {
		return code == GLFW.GLFW_MOUSE_BUTTON_3;
	}

}
