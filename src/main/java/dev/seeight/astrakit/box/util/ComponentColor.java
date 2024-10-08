package dev.seeight.astrakit.box.util;

import java.awt.*;

public class ComponentColor {
	public float r = 1;
	public float g = 1;
	public float b = 1;
	public float a = 1;

	public ComponentColor setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	public ComponentColor setColor(Color color) {
		r = color.getRed() / 255F;
		g = color.getGreen() / 255F;
		b = color.getBlue() / 255F;
		a = color.getAlpha() / 255F;
		return this;
	}

	public ComponentColor setColor(ComponentColor color) {
		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
		return this;
	}
}
