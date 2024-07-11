package dev.seeight.astrakit.box;

import java.awt.*;

public class Border {
	public float r = 1, g = 1, b = 1, a = 1;
	public float size = 1;

	public Border setSize(float size) {
		this.size = size;
		return this;
	}

	public Border setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	public Border setColor(Color color) {
		r = color.getRed() / 255F;
		g = color.getGreen() / 255F;
		b = color.getBlue() / 255F;
		a = color.getAlpha() / 255F;
		return this;
	}
}
