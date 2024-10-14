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

public interface Layout {
	/**
	 * Applies the layout into the specified box by rearranging the child boxes.
	 *
	 * @param box The box to apply the layout to.
	 */
	void arrange(ParentBox box);

	/**
	 * Called when the box is resized. This method must be called AFTER the size of the box has been set.
	 *
	 * @param box The box that was resized.
	 * @param width A positive float number, if changed. Otherwise, 0 will be used.
	 * @param height A positive float number, if changed. Otherwise, 0 will be used.
	 */
	void onResize(ParentBox box, float width, float height);

	float getMinWidth(ParentBox parentBox);

	float getMinHeight(ParentBox parentBox);

	Axis getMainAxis();
}
