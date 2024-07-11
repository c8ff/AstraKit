package dev.seeight.astrakit.box;

import dev.seeight.astrakit.box.res.BoxResources;
import dev.seeight.renderer.renderer.Renderer;

public interface UIBoxContext {
	int getWindowWidth();

	int getWindowHeight();

	Renderer getRenderer();

	default boolean boxBoundView() {
		return false;
	}

	Mouser getMouser();

	Keyboarder getKeyboarder();

	double getDeltaTime();

	void catchError(String message, Throwable throwable);

	BoxResources getBoxResources();
}
