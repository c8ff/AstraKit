package dev.seeight.astrakit.box.crop;

import dev.seeight.renderer.renderer.gl.OpenGLRenderer2;

@Deprecated
public class OpenGLRenderer2CropContext implements ICropContext {
	private final OpenGLRenderer2 g;
	private boolean cropping;

	public OpenGLRenderer2CropContext(OpenGLRenderer2 g) {
		this.g = g;
	}

	@Override
	public void startCropping(float x, float y, float width, float height) {
		g.crop(x, y, width, height);
		cropping = true;
	}

	@Override
	public void stopCropping() {
		g.stopCrop();
		cropping = false;
	}

	@Override
	public boolean isCropping() {
		return cropping;
	}
}
