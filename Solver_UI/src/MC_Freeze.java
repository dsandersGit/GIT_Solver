
public class MC_Freeze {
	public  float 		accuracyTrain;
	public  float 		accuracyTest;
	public float 		accuracy;
	public int 			targetColorIndex;
	public String 		targetLabel = "-none-";
	public int[] 		classification;
	public  double[] 	averages;
	public  float 		split;
	double 				distance  = 0;
	double 				maxDistance  = 0;
	double[][] 			eigenVec = null;
	double[] 			distances = null;
	boolean[] 			isTrain = null;
	int[] 				sampleColorIndices = null;
	public int[][] 		tp_fp_tn_fn = null;
	
	public MC_Freeze(double dst, double[][] mc){
		this.distance 	= dst;
		this.eigenVec	= new double[mc.length][mc[0].length];
	}
	public String getModelAsString() {
		StringBuffer out = new StringBuffer();
		out.append("<<<" + "\n");
//		out.append("Model" + SolverStart.app + SolverStart.appAdd + "\n");
//		out.append("accuracyTrain," + accuracyTrain + "\n");
//		out.append("accuracyTest, " + accuracyTest + "\n");
//		out.append("numDims," + Opts.numDims + "\n");
//		out.append("split," + split + "\n");
//		out.append("targetColorIndex," + targetColorIndex + "\n");
//		out.append("targetLabel," + targetLabel + "\n");
//		
		out.append("Model, accuracyTrain, accuracyTest,	numDims, split, targetColorIndex, targetLabel" + "\n");
		out.append(SolverStart.app + SolverStart.appAdd + "," );
		out.append(accuracyTrain + "," );
		out.append(accuracyTest+ "," );
		out.append(Opts.numDims + "," );
		out.append(split + "," );
		out.append(targetColorIndex + "," );
		out.append(targetLabel + "\n" );
		
		
		out.append("TAG, Dimension,average");
		for (int i=0;i<DS.numVars;i++) {
			out.append(",VECTOR: " + DS.AreaNames[i]);	
		}
		out.append("\n");
		
        for (int i=0;i<Opts.numDims;i++) {
        	out.append(">EC,"+i +","+ averages[i]);
            for (int a=0;a<DS.numVars;a++){
                out.append( "," + eigenVec[a][i]);
            }
            out.append("\n");
        }
        out.append(">>>" + "\n");
		return out.toString();
	}
	public JSONObject getModelAsJson() {
		JSONObject out = new JSONObject();

		
		out.put("accuracyTrain", 	accuracyTrain);
		out.put("accuracyTest", 	accuracyTest);
		out.put("Opts.numDims", 	Opts.numDims);
		out.put("split", 			split);
		out.put("targetColorIndex", targetColorIndex);
		out.put("targetLabel", 		targetLabel);
		out.put("maxDistance", 		maxDistance);
	
		StringBuffer tmp = new StringBuffer();
        for (int i=0;i<Opts.numDims;i++) {
        	tmp = new StringBuffer();
            for (int a=0;a<DS.numVars;a++){
            	if ( a>0)tmp.append(",");
            	tmp.append( eigenVec[a][i]);
            }
            out.put("Vector"+i, 		tmp.toString());
        }
        tmp = new StringBuffer();
        for (int i=0;i<Opts.numDims;i++) {
        	if ( i>0)tmp.append(",");
        	tmp.append( averages[i]);
        }
        out.put("Averages", 		tmp.toString());
        
        tmp = new StringBuffer();
        for (int i=0;i<isTrain.length;i++) {
        	if ( i>0)tmp.append(",");
        	String val = "0";
        	if ( isTrain[i])val= "1";
        	tmp.append( val);
        }
        out.put("TrainSet", 		tmp.toString());
        
        tmp = new StringBuffer();
        for (int i=0;i<tp_fp_tn_fn.length;i++) {
        	if ( i>0)tmp.append(",");
        	String val = ""+tp_fp_tn_fn[i][0];
        	tmp.append( val);
        }
        out.put("tp_fp_tn_fn_Train", 		tmp.toString());
        
        tmp = new StringBuffer();
        for (int i=0;i<tp_fp_tn_fn.length;i++) {
        	if ( i>0)tmp.append(",");
        	String val = ""+tp_fp_tn_fn[i][1];
        	tmp.append( val);
        }
        out.put("tp_fp_tn_fn_Test", 		tmp.toString());
		return out;
	}
}
