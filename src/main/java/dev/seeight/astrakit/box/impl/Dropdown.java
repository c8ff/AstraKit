package dev.seeight.astrakit.box.impl;

public interface Dropdown {
	void renderOver(float x, float y, float alpha);

	void mouseEventOver(int button, int action, double mx, double my);

	void scrollOver(double x, double y);
}
