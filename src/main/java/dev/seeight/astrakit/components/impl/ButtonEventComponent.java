package dev.seeight.astrakit.components.impl;

import org.jetbrains.annotations.NotNull;

public class ButtonEventComponent extends ButtonComponent {
	private final ClickEvent event;

	public ButtonEventComponent(String string, @NotNull ClickEvent event) {
		super(string);
		this.event = event;
	}

	@Override
	public void click() {
		this.event.click(this);
	}

	public interface ClickEvent {
		void click(ButtonEventComponent button);
	}
}
