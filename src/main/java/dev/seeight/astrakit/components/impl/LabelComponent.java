/*
 * AstraKit
 * Copyright (C) 2024 c8ff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
