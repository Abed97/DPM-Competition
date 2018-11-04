package ca.mcgill.ecse211.ringCapture;

import java.util.HashMap;
import java.util.Map;

import lejos.robotics.SampleProvider;

public class RingColours {
	
	private SampleProvider ring_color_sample_provider;
	private float[] color_samples;

	private int redS, greenS, blueS;
	
	private Map<String, int[]> colorMap = new HashMap<String, int[]>();
	private int[] greenRing = {52988, 115452, 15269, 13376, 26599, 1532}; // Rm, Gm,Bm, Rsd, Gsd, Bsd values (times 10^6 for each)
	private int[] orangeRing = {111483, 36549, 7096, 17996, 6088, 999};
	private int[] blueRing = {26097, 119187, 79725, 7591, 25473, 8012};
	private int[] yellowRing = {148692, 107749, 18901, 35384, 23879, 1561};
	
	public RingColours(SampleProvider sp, float[] data) {
		this.ring_color_sample_provider = sp;
		this.color_samples = data;
		colorMap.put("Green", greenRing);
		colorMap.put("Orange", orangeRing);
		colorMap.put("Blue", blueRing);
		colorMap.put("Yellow", yellowRing);
		fetchLightData();
	}
	
	public void fetchLightData() {
		ring_color_sample_provider.fetchSample(color_samples, 0);
		this.redS = (int) (color_samples[0] * 1000000);
		this.greenS = (int) (color_samples[1] * 1000000);
		this.blueS = (int) (color_samples[2] * 1000000);
	}
	
	public boolean colourDetected(String colour) {
		
		int[] searchColorVal = colorMap.get(colour);
		
		if (Math.abs(searchColorVal[0] - this.redS) <= (int) (3 * searchColorVal[3]) &&
				Math.abs(searchColorVal[1] - this.greenS) <= (int) (3 * searchColorVal[4]) &&
				Math.abs(searchColorVal[2] - this.blueS) <= (int) (3 * searchColorVal[5]))
			{
				return false;
			}else {
				return true;
			}
	}
}
