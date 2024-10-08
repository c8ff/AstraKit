package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.Component;
import dev.seeight.astrakit.box.layout.Sizing;

public class FillerBox extends Component {
	public final float width;
	public final float height;

	public FillerBox() {
		this(10, 10);
	}

	public FillerBox(float width, float height) {
		this.width = width;
		this.height = height;
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setSizingX(Sizing.FILL);
		this.setSizingY(Sizing.FILL);
	}

	@Override
	public boolean isOutsideView(float offsetX, float offsetY) {
		return true;
	}
}
