package dev.seeight.astrakit.components.impl;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.IComponentRenderer;
import dev.seeight.common.lwjgl.font.FontRenderer;

public class LabelComponent extends Component {
	private String string;
	private FontRenderer font;

	public LabelComponent(String string) {
		this.string = string;
	}

	public LabelComponent(String string, FontRenderer fontRenderer) {
		this(string);
		this.font = fontRenderer;
		this.calcSize(fontRenderer);
	}

	public void calcSize(FontRenderer font) {
		this.setFont(font);
		this.width = font.getWidthFloat(this.getString());
		this.height = font.FONT_HEIGHT_FLOAT;
	}

	public void setFont(FontRenderer font) {
		this.font = font;
	}

	public void setString(String string) {
		this.string = string;
		this.calcSize(this.font);
	}

	public String getString() {
		return string;
	}

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderLabel(this, alpha);
	}
}
