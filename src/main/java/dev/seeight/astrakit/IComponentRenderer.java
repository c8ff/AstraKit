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
