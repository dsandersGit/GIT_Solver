

public class Opts {
	// INIT
	public static String	dstType			 		= "GROUP"; 				// "EGO-NotSupported"; "GROUP"
	public static String	normType		 		= "MaxMinNorm"; 			// "MaxMinNorm"; "Pareto"; "None"
	public static int 		numDims			 		= 3; 					// Number of Neurons / Dimensions
	public static double 	trainRatio				= 0.7;					// test / train > ratio of training to sample data  0 = full target 1 - no rget
	public static int 		numEnsemble				= 11;					// Count of runs per index
	public static int 		noBetterStop 			= 1500;					// Akzeptierte Fehlversuche
	public static double	minBetter 				= 0.999d;				// Mindest Verbesserung nach akzeptierten Fehlveruchen
	public static int		plotTimer				= 300;					// Plot wird gezeigt alle NNN Millisekunden
	public static boolean	fixTrainSet 			= false;					// One Trainset for full ensemble (true) or per cycle (false)
	public static boolean	doTheLeft	 			= false;				// One Trainset for full ensemble (true) or per cycle (false)
	public static boolean	kickStart	 			= false;				// One Trainset for full ensemble (true) or per cycle (false)
	public static String	activation	 			= "DxA";				// Activation Function "DxA" or "A" or "D"
	public static double	largeStep	 			= 0.2;					// Random step
	
	public static int  		minPopulation 			= 25;					// No User Option! minimal Population of classes for split
	public static int  		minVariableCount		= 2;					// No User Option! minimal number of variables
	
	

	public static JSONObject getOptsAsJson() {
		JSONObject out = new JSONObject();
		out.put("dstType", 		dstType);
		out.put("normType", 	normType);
		out.put("numDims", 		numDims);
		out.put("trainRatio", 	trainRatio);
		out.put("numEnsemble", 	numEnsemble);
		out.put("noBetterStop", noBetterStop);
		out.put("minBetter", 	minBetter);
		out.put("fixTrainSet", 	fixTrainSet);
		out.put("doTheLeft", 	doTheLeft);
		out.put("plotTimer", 	plotTimer);
		out.put("kickStart", 	kickStart);
		out.put("activation", 	activation);
		out.put("largeStep", 	largeStep);
		

		return out;
	}
	
	
}
