package dev.seeight.astrakit.box.crop;

public interface ICropContext {
	void startCropping(float x, float y, float width, float height);

	void stopCropping();

	boolean isCropping();
}
