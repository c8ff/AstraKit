package dev.seeight.astrakit.box.layout;

public enum Axis {
	HORIZONTAL,
	VERTICAL;

	public Axis opposite() {
		if (this == VERTICAL) {
			return HORIZONTAL;
		}

		return VERTICAL;
	}
}
