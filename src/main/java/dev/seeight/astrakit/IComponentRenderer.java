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

package dev.seeight.astrakit;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.components.impl.*;

public interface IComponentRenderer {
	void renderSlider(SliderComponent slider, float a);

	void renderTextField(TextFieldComponent field, float a);

	void renderLabel(LabelComponent label, float alpha);

	void renderButton(ButtonComponent button, float alpha);

	void renderCheckBox(CheckBoxComponent component, float alpha);

	void renderDropdown(DropdownComponent component, float alpha);

	void renderTitle(TitleComponent component, float alpha);

	void renderDefault(Component component, float alpha);
}
