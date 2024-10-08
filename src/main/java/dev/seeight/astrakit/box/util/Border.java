package dev.seeight.astrakit.box.util;

import java.awt.*;

public class Border extends ComponentColor {
	public float size = 1;

	public Border setSize(float size) {
		this.size = size;
		return this;
	}

	@Override
	public Border setColor(float r, float g, float b, float a) {
		return (Border) super.setColor(r, g, b, a);
	}

	@Override
	public Border setColor(Color color) {
		return (Border) super.setColor(color);
	}
}
