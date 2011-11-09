package org.anddev.andengine.opengl.texture;

import java.io.IOException;

import org.anddev.andengine.opengl.texture.atlas.source.ITextureAtlasSource;
import org.anddev.andengine.opengl.util.GLState;

import android.opengl.GLES20;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 14:55:02 - 08.03.2010
 */
public abstract class Texture implements ITexture {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final PixelFormat mPixelFormat;
	protected final TextureOptions mTextureOptions;

	protected int mTextureID = -1;
	protected boolean mLoadedToHardware;
	protected boolean mUpdateOnHardwareNeeded = false;

	protected final ITextureStateListener mTextureStateListener;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * @param pPixelFormat
	 * @param pTextureOptions the (quality) settings of the Texture.
	 * @param pTextureStateListener to be informed when this {@link Texture} is loaded, unloaded or a {@link ITextureAtlasSource} failed to load.
	 */
	public Texture(final PixelFormat pPixelFormat, final TextureOptions pTextureOptions, final ITextureStateListener pTextureStateListener) throws IllegalArgumentException {
		this.mPixelFormat = pPixelFormat;
		this.mTextureOptions = pTextureOptions;
		this.mTextureStateListener = pTextureStateListener;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public int getHardwareTextureID() {
		return this.mTextureID;
	}

	@Override
	public boolean isLoadedToHardware() {
		return this.mLoadedToHardware;
	}

	@Override
	public void setLoadedToHardware(final boolean pLoadedToHardware) {
		this.mLoadedToHardware = pLoadedToHardware;
	}

	@Override
	public boolean isUpdateOnHardwareNeeded() {
		return this.mUpdateOnHardwareNeeded;
	}

	@Override
	public void setUpdateOnHardwareNeeded(final boolean pUpdateOnHardwareNeeded) {
		this.mUpdateOnHardwareNeeded = pUpdateOnHardwareNeeded;
	}

	public PixelFormat getPixelFormat() {
		return this.mPixelFormat;
	}

	@Override
	public TextureOptions getTextureOptions() {
		return this.mTextureOptions;
	}

	@Override
	public ITextureStateListener getTextureStateListener() {
		return this.mTextureStateListener;
	}

	@Override
	public boolean hasTextureStateListener() {
		return this.mTextureStateListener != null;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	protected abstract void writeTextureToHardware() throws IOException;

	@Override
	public Texture load() {
		TextureManager.loadTexture(this);

		return this;
	}

	@Override
	public Texture unload() {
		TextureManager.unloadTexture(this);

		return this;
	}

	@Override
	public void loadToHardware() throws IOException {
		GLState.enableTextures();

		this.mTextureID = GLState.generateTexture();

		GLState.bindTexture(this.mTextureID);

		this.writeTextureToHardware();

		this.mTextureOptions.apply();

		this.mUpdateOnHardwareNeeded = false;
		this.mLoadedToHardware = true;

		if(this.mTextureStateListener != null) {
			this.mTextureStateListener.onLoadedToHardware(this);
		}
	}

	@Override
	public void unloadFromHardware() {
		GLState.enableTextures();

		GLState.deleteTexture(this.mTextureID);

		this.mTextureID = -1;

		this.mLoadedToHardware = false;

		if(this.mTextureStateListener != null) {
			this.mTextureStateListener.onUnloadedFromHardware(this);
		}
	}

	@Override
	public void reloadToHardware() throws IOException {
		this.unloadFromHardware();
		this.loadToHardware();
	}

	@Override
	public void bind() {
		GLState.bindTexture(this.mTextureID);
	}

	@Override
	public void bind(final int pGLActiveTexture) {
		GLES20.glActiveTexture(pGLActiveTexture);
		GLState.bindTexture(this.mTextureID);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}