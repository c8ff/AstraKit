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

package dev.seeight.astrakit.box;

import org.jetbrains.annotations.Contract;

public interface IFloatRect {
	void setX(float x);
	float getX();

	void setY(float y);
	float getY();

	void setWidth(float width);
	float getWidth();

	void setHeight(float height);
	float getHeight();

	@Contract("_, _ -> this")
	@SuppressWarnings("UnusedReturnValue")
	default IFloatRect setPosition(float x, float y) {
		this.setX(x);
		this.setY(y);
		return this;
	}

	@Contract("_, _ -> this")
	@SuppressWarnings("UnusedReturnValue")
	default IFloatRect setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
		return this;
	}

	@Contract(value = "_, _, _, _ -> this")
	default IFloatRect setDimensions(float x, float y, float width, float height) {
		this.setPosition(x, y);
		this.setSize(width, height);
		return this;
	}

	default boolean isInside(double pointX, double pointY) {
		return getX() < pointX && getY() < pointY && getX() + getWidth() > pointX && getY() + getHeight() > pointY;
	}
}
