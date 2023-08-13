package dev.seeight.astrakit.components.impl;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.IComponentRenderer;
import dev.seeight.common.lwjgl.font.FontRenderer;

public class TitleComponent extends Component {
	public String text;

	public TitleComponent(String text, FontRenderer font) {
		this.text = text;
		this.setSize(font.getWidthFloat(text), font.FONT_HEIGHT_FLOAT);
	}

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderTitle(this, alpha);
	}
}
