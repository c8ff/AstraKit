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
