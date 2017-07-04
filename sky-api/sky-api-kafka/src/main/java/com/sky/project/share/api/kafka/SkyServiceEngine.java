package com.sky.project.share.api.kafka;

public final class SkyServiceEngine {

	public SkyServiceEngine init() {
		return this;
	}

	public SkyServiceEngine consume() {
		return this;
	}

	public static SkyServiceEngine getInstance() {
		return SkyServiceEngineNest.instance;
	}

	private static class SkyServiceEngineNest {
		private static final SkyServiceEngine instance = new SkyServiceEngine();
	}

	private SkyServiceEngine() {
	}

}
