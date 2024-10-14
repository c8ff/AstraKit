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

package dev.seeight.astrakit.box.layout;


import dev.seeight.astrakit.box.container.ParentBox;

// TODO. Bruh
public class SpreadLayout implements Layout {
	protected float padding = 0;
	protected float margin = 0;

	protected Alignment alignment = Alignment.LEFT_OR_UP;

	public SpreadLayout setAlignment(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public SpreadLayout setPadding(float padding) {
		this.padding = padding;
		return this;
	}

	public SpreadLayout setMargin(float margin) {
		this.margin = margin;
		return this;
	}

	@Override
	public void arrange(ParentBox box) {
		// TODO: ????
		box.setHeight(box.getMinHeight() + margin * 2);

		float width;

		if (box.getWidth() == 0) {
			float w = 0;

			for (var child : box.children()) {
				w += child.getWidth();
			}

			width = w;

			box.setWidth(width);
		} else {
			width = box.getWidth();
		}

		float space = (width - this.padding * (box.children().size() - 1) - this.margin * 2) / (box.children().size());

		float x = this.margin;

		for (var child : box.children()) {
			if (child.getSizing(Axis.HORIZONTAL) == Sizing.FILL) {
				child.setWidth(space);
			}

			child.setX(x + (space - child.getWidth()) / 2f);
			x += space + this.padding;
		}

		Axis axis = Axis.HORIZONTAL;
		Axis axis1 = axis.opposite();

		switch (alignment) {
			case LEFT_OR_UP -> {
				for (var child : box.children()) {
					child.setPosition(axis1, margin);
				}
			}
			case CENTER -> {
				float s = box.getSize(axis1);

				for (var child : box.children()) {
					child.setPosition(axis1, (s - child.getSize(axis1)) / 2f);
				}
			}
			case RIGHT_OR_DOWN -> {
				float s = box.getSize(axis1);

				for (var child : box.children()) {
					child.setPosition(axis1, s - child.getSize(axis1) - margin);
				}
			}
		}
	}

	@Override
	public void onResize(ParentBox box, float width, float height) {

	}

	@Override
	public float getMinWidth(ParentBox parentBox) {
		// TEMPORARY
		final Axis axis = Axis.HORIZONTAL;
		final Axis axis1 = axis.opposite();

		float w = 0;
		boolean _f = true;
		if (axis1 == Axis.HORIZONTAL) {
			for (var child : parentBox.children()) {
				float s = child.getMinSize(axis1);
				if (w < s) {
					w = s;
				}
			}
		} else {
			for (var child : parentBox.children()) {
				float s = child.getMinSize(axis1);

				if (!_f) w += padding; else _f = false;
				w += s;
			}
		}

		return w + margin * 2;
	}

	@Override
	public float getMinHeight(ParentBox parentBox) {
		// TEMPORARY
		final Axis axis = Axis.HORIZONTAL;
		final Axis axis1 = axis.opposite();
		final float margin = 0;

		float h = margin;

		boolean _f = true;
		if (axis1 == Axis.VERTICAL) {
			for (var child : parentBox.children()) {
				float s = child.getSize(axis1);
				if (h < s) {
					h = s;
				}
			}
		} else {
			for (var child : parentBox.children()) {
				float s = child.getSize(axis1);

				if (!_f) h += padding; else _f = false;
				h += s;
			}
		}

		return h + margin;
	}

	@Override
	public Axis getMainAxis() {
		return Axis.HORIZONTAL;
	}
}
