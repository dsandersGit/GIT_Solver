

/*
 * options storage 
 *  + holds options
 *  + delivers options as JSON 
 *  
 *  Copyright(c) 2009-2023, Daniel Sanders, All rights reserved.
 *  https://github.com/dsandersGit/GIT_Solver
 */

public class Opts {
	// INIT
	public static String	dstType			 		= "GROUP"; 				// "EGO"; "GROUP"
	public static String	normType		 		= "Pareto"; 			// "MaxMinNorm"; "Pareto"; "None"
	public static String	activation	 			= "D+A";				// Activation Function "DxA" or "A" or "D", "D+A"
	//
	public static int 		numDims			 		= 3; 					// Number of Neurons / Dimensions
	public static double 	trainRatio				= 0.7;					// test / train > ratio of training to sample data  0 = full target 1 - no rget
	public static int 		numEnsemble				= 5;					// Count of runs per index
	public static int 		noBetterStop 			= 500;					// Akzeptierte Fehlversuche
	public static double	minBetter 				= 0.999d;				// Mindest Verbesserung nach akzeptierten Fehlveruchen
	public static int		plotTimer				= 50;					// Plot wird gezeigt alle NNN Millisekunden
	public static boolean	fixTrainSet 			= false;					// One Trainset for full ensemble (true) or per cycle (false)

	public static double	largeStep	 			= 0.2;					// Random step
	public static boolean	useMedian	 			= false;				// Median / Average for Center of Target
	
	public static int  		minPopulation 			= 25;					// No User Option! minimal Population of classes for split
	public static int  		minVariableCount		= 2;					// No User Option! minimal number of variables
	public static int  		retainModelNum 			= 3;					// Model per class in reduced ensemble

//	public static boolean	doTheLeft	 			= false;				// One Trainset for full ensemble (true) or per cycle (false)
//	public static boolean	kickStart	 			= false;					// One Trainset for full ensemble (true) or per cycle (false)
	

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
//		out.put("doTheLeft", 	doTheLeft);
		out.put("plotTimer", 	plotTimer);
//		out.put("kickStart", 	kickStart);
		out.put("activation", 	activation);
		out.put("largeStep", 	largeStep);
		out.put("useMedian", 	useMedian);
		

		return out;
	}
	
	
}
