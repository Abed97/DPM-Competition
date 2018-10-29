package ringCapture;

import java.util.HashMap;
import java.util.Map;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/** code to search for and capture rings
 * 
 * @author emmaeagles
 *
 */
public class SearchTree {
	private SampleProvider ring_color_sample_provider;
	//private static final Port csPort = LocalEV3.get().getPort("S3");
	private float[] color_samples;
	
	private final String SEARCHCOLOR = "Yellow";
	private int[] searchColorVal;
	
	private Map<String, int[]> colorMap = new HashMap<String, int[]>();
	private int[] greenRing = {52988, 115452, 15269, 13376, 26599, 1532}; // Rm, Gm,Bm, Rsd, Gsd, Bsd values (times 10^6 for each)
	private int[] orangeRing = {111483, 36549, 7096, 17996, 6088, 999};
	private int[] blueRing = {26097, 119187, 79725, 7591, 25473, 8012};
	private int[] yellowRing = {148692, 107749, 18901, 35384, 23879, 1561};
	
	//under demo conditions
	private int[] greenRing_1 = {63283, 143777, 14267, 12707, 24629, 4062};
	private int[] orangeRing_1 = {120244, 41029, 7697, 23859, 9602, 2545};
	private int[] blueRing_1 = {30487, 127991, 78627, 6892, 18154, 10471};
	private int[] yellowRing_1 = {149021, 113338, 15292, 26255, 18191, 3927};
	
	private int redS, greenS, blueS;
	/** Constructor
	 * 
	 */
	public SearchTree() {
		
		colorMap.put("Green", greenRing_1);
		colorMap.put("Orange", orangeRing_1);
		colorMap.put("Blue", blueRing_1);
		colorMap.put("Yellow", yellowRing_1);
		
		searchColorVal = colorMap.get(SEARCHCOLOR);
	}
	
	public int ColorID() {
		
		return 2;
	}
	public void fetchLightData() {
		ring_color_sample_provider.fetchSample(color_samples, 0);
		this.redS = (int) (color_samples[0] * 1000000);
		this.greenS = (int) (color_samples[1] * 1000000);
		this.blueS = (int) (color_samples[2] * 1000000);
	}
	boolean detectColor(boolean keepLooking) {

		if (keepLooking == true)
		{
			fetchLightData();
			
			// checking if sensor RGB value is greater than 2 standard deviation from mean
			if (Math.abs(searchColorVal[0] - this.redS) <= (int) (3 * searchColorVal[3]) &&
				Math.abs(searchColorVal[1] - this.greenS) <= (int) (3 * searchColorVal[4]) &&
				Math.abs(searchColorVal[2] - this.blueS) <= (int) (3 * searchColorVal[5]))
			{
				Sound.beep();
				return false;
				
			}
			
			else {
				Sound.twoBeeps();
				return true;
			}
		}
		
		return false;
	}
}
