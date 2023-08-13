package dev.seeight.astrakit.components.impl;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.IComponentRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class CheckBoxComponent extends Component {
	private boolean value;
	@NotNull
	private final String valueName;
	@Nullable
	private final ConsumerBoolean consumer;

	public CheckBoxComponent(boolean defaultValue, @NotNull String valueName, @Nullable ConsumerBoolean consumer) {
		this.value = defaultValue;
		this.valueName = valueName;
		this.consumer = consumer;
	}

	@Override
	public void mouseButtonEvent(int x, int y, int button, int action) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
			this.value = !this.value;
			if (this.consumer != null) {
				this.consumer.call(this.value);
			}
		}
	}

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderCheckBox(this, alpha);
	}

	public CheckBoxComponent setValue(boolean value) {
		this.value = value;
		return this;
	}

	public boolean getValue() {
		return this.value;
	}

	public @NotNull String getValueName() {
		return valueName;
	}

	public interface ConsumerBoolean {
		void call(boolean value);
	}
}
