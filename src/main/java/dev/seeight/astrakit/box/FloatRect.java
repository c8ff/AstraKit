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

public class FloatRect implements IFloatRect {
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public float getY() {
		return this.y;
	}

	@Override
	public void setWidth(float width) {
		this.width = width;
	}

	@Override
	public float getWidth() {
		return this.width;
	}

	@Override
	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public float getHeight() {
		return this.height;
	}
}
