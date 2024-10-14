package dev.seeight.astrakit.box.crop;

import dev.seeight.astrakit.box.UIBoxContext;
import org.lwjgl.opengl.GL11;

public class GenericOpenGLCropContext implements ICropContext {
	private final UIBoxContext ctx;
	private final boolean invertedY;
	private boolean cropping;

	public GenericOpenGLCropContext(UIBoxContext ctx, boolean invertedY) {
		this.ctx = ctx;
		this.invertedY = invertedY;
	}

	@Override
	public void startCropping(float x, float y, float width, float height) {
		cropping = true;
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		if (this.invertedY) {
			GL11.glScissor((int) x, (int) (this.ctx.getWindowHeight() - y - height), (int) width, (int) height);
		} else {
			GL11.glScissor((int) x, (int) y, (int) width, (int) height);
		}
	}

	@Override
	public void stopCropping() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		cropping = false;
	}

	@Override
	public boolean isCropping() {
		return cropping;
	}
}
