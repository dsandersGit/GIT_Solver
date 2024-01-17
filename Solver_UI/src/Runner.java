import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Thread: data-classification-training. 
 *  + data classification training 
 *  
 *  Copyright(c) 2009-2023, Daniel Sanders, All rights reserved.
 *  https://github.com/dsandersGit/GIT_Solver
 */

public class Runner {
	public int targetColorIndex 	= -1;
	public String targetName		 	= null;
	public double[][] mcEigenVec 	= null;
	public double[][] mcEigenVecOld = null;
	public double[][] mcPCA 		= null;
	public static int[][] booster	= null;
	int zuFiAgain 					= -1;
	double curr_Dist 				= 0;
	double[] distances 				= null;
	double distanceOld				= 0;
	//double anotherIndication		=0;
	double dstLatestMax				= 0;
	double innerDstOld				= 0;
	int notBetterCount				= 0;
	double accuracy					= 0;
	int[] classification 			= null;
	boolean[] trainSet 				= null;
	float split						= 0;

	public long plotTime			= -1;
	double maxDist 					= 0;
	
	int numTarget					= -1;
	int numElseTrain				= -1;
	int numElseTest					= -1;
	int numTrain					= -1;
	int numTest						= -1;
	
	int[][] tp_fp_tn_fn 			= null;
	
	static ArrayList<Float> accuracyTrain = new ArrayList<Float>();
    static ArrayList<Float> accuracyTest = new ArrayList<Float>();
    static ArrayList<Float> dstTrain = new ArrayList<Float>();
    static boolean doDistxAccur = false;									// activation function
    static boolean doAccur = false;
    double[] mappedSigmoid = null;
    float[] xSigmoid = null;
    float[] ySigmoid = null;
    
    int absCount =0 ;
    
	
	public Runner(int target, String tName, boolean activationIsDst) {
		absCount =0 ;
		mappedSigmoid = new double[200];
		for (double i=-10;i<10;i+=0.1) {
			mappedSigmoid[(int)((i + 10)*10.)] = getSigmoid(i);	
		}
		xSigmoid = new float[200];
        ySigmoid = new float[200];
        for (int i=0;i<xSigmoid.length;i++) {
        	xSigmoid[i] = (float) ((float)i/200.);
        	ySigmoid[i] = DS.numSamples * (float) getMappedSigmoid(((float)i/200. - 0.5f)*10);
        	
        }
		
		targetColorIndex 		= target;
		if ( tName == null) {
			targetName = "c"+target;
		}else {
			targetName = tName;
		}
		if ( mcEigenVec == null) {
	        mcEigenVec             	= new double [DS.numVars][Opts.numDims];
	        mcEigenVecOld         	= new double [DS.numVars][Opts.numDims];
	        mcPCA                 	= new double [Opts.numDims][DS.numSamples];
		}
		if ( booster == null) {
			booster = new int[DS.numSamples][DS.numClasses];
		}
        if ( activationIsDst) {
        	doDistxAccur = true;
        	doAccur = false;
        }else {
        	doAccur = true;
			doDistxAccur = false;
        }
        
      //72: Train/Test change only cycle wise
//        if  (Opts.fixTrainSet) {
//        	int i = Tools.getIndexOfTarget (target);
//        	if ( DS.fixedTrainSet == null)
//        		DS.getFixedTrainSet();
//        	trainSet = DS.fixedTrainSet[i];
//        }else {
//        	trainSet = getTrainSet();
//        }
        int i = Tools.getIndexOfTarget (target);
        trainSet = DS.fixedTrainSet[i];

        int step = 100;
        while ( reDo( -1 ) && !SolverStart.immediateStop ) {
        	absCount++;
        	if ( absCount%step == 0 )UI.labRun.setText("Run: " + absCount);
        	if ( absCount > 3000 ) step = 1000;
        }
        finish();
        if ( !SolverStart.immediateStop ) doFreeze();
	}
	public static void cleanRunner () {
		accuracyTrain.clear();
		accuracyTest.clear();
		dstTrain.clear();
		booster = null;
	}

	private boolean reDo(int src){														// src > Daten aus Freeze extrahieren
		if ( src < 0 ){
			zuFiAgain = zufi(zuFiAgain);
	    }else{
	    	for (int a=0;a<DS.numVars;a++){
	    		for (int i=0;i<Opts.numDims;i++) {
	    			mcEigenVec[a][i]     = DS.freezs.get(src).eigenVec[a][i];
	                mcEigenVecOld[a][i] = 0;
	            }
	        }
	    	curr_Dist = 0;
	    }
		
		 calcPlot();
	        double ndst = 0;
	        if ( Opts.dstType.contentEquals("EGO"))         ndst = getDistancesEGO(null);
	        if ( Opts.dstType.contentEquals("GROUP"))       ndst = getDistances(null);
	        getSplit();
	        accuracy = doClassify(true, false);
	        
	        
	        if (doDistxAccur)
				ndst *= (accuracy);
	        if (doAccur)
				ndst = accuracy;

	        
	        // distanceOld!=0
	        if ( (ndst  > distanceOld )  ){
	        	//System.out.println(ndst+"\t"+distanceOld);
	            accuracyTrain.add((float) accuracy);
	            accuracyTest.add((float) doClassify(false, false));
	            dstTrain.add((float) ndst);

	            logZufi();
	            notBetterCount = 0;
	            distanceOld = ndst;
	            
	            if ( (System.currentTimeMillis() - SolverStart.plotTimer) > Opts.plotTimer || SolverStart.plotTimer < 0) {
	            	SolverStart.plotTimer = System.currentTimeMillis(); 
	            	doStreamPlot(false);
	            }
	            
	        }else{
	        	//System.out.println("out " + ndst+"\t"+distanceOld+"\t"+notBetterCount);
	            zuFiAgain = -1;
	            undoZufi();
	            notBetterCount ++;

	       }
	        if ( notBetterCount > Opts.noBetterStop) {
	        	if ( dstLatestMax  > distanceOld* Opts.minBetter && dstLatestMax > 0 ) {
	        		finish();
	                return false;
	            }
	            dstLatestMax = distanceOld;
	            notBetterCount=0;
	        }
        
        return true;
	}
	
	private void finish() {
		// Final Calc
		double ndst=0;;
        calcPlot();
        if ( Opts.dstType.contentEquals("EGO"))         ndst = getDistancesEGO(null);
        if ( Opts.dstType.contentEquals("GROUP"))         ndst = getDistances(null);
        
    
        getSplit();
        accuracy = doClassify(true, true);
        if (doDistxAccur)
			ndst *= (accuracy);
        if (doAccur)
			ndst = accuracy;
        //--------------------------------------------
        tp_fp_tn_fn = new int[4][2];
		for (int f=0;f<DS.numSamples;f++){
			if ( trainSet[f]) {
				//for (int c=0;c<DS.numClasses;c++){
					int target = targetColorIndex;
					if ( DS.classIndex[f] == target) {
						if ( classification[f] == target ) 								// TP
							tp_fp_tn_fn[0][0]++;
						if ( classification[f] != target ) 								// FN
							tp_fp_tn_fn[3][0]++;
					}else {
						if ( classification[f] == target ) 								// FP
							tp_fp_tn_fn[1][0]++;
						if ( classification[f] != target ) 								// TN
							tp_fp_tn_fn[2][0]++;
					}
				//}
			}else {
				//for (int c=0;c<DS.numClasses;c++){
					int target = targetColorIndex;
					if ( DS.classIndex[f] == target) {
						if ( classification[f] == target ) 								// TP
							tp_fp_tn_fn[0][1]++;
						if ( classification[f] != target ) 								// FN
							tp_fp_tn_fn[3][1]++;
					}else {
						if ( classification[f] == target ) 								// FP
							tp_fp_tn_fn[1][1]++;
						if ( classification[f] != target ) 								// TN
							tp_fp_tn_fn[2][1]++;
					}
				//}
			}
		}
		doStreamPlot(true);			// darw one last time
	}
	
	private void doStreamPlot(boolean doDraw){
	   	if ( doDraw || (UI.maintabbed.getSelectedIndex()==UI.tab_Distance || UI.maintabbed.getSelectedIndex()==UI.tab_Train)) {
	   		int index = Classify.getTargetColorIndexPos (targetColorIndex);
            float[] yDst = new float[dstTrain.size()];
            float[] yTrain = new float[accuracyTrain.size()];
            
            float[] yTest = new float[accuracyTrain.size()];
            float[] x = new float[accuracyTrain.size()];
            for (int i=0;i<yTrain.length;i++) {
            	yTrain[i] = accuracyTrain.get(i);
            	
            	yTest[i] = accuracyTest.get(i);
            	yDst[i] = dstTrain.get(i);
            	x[i] = i;
            }
            UI.sp.dats.clear();
           	UI.sp.setXY(x, yDst, 4, SolverStart.frontColor, "gain", false, true, false);
            UI.sp.setXY(x, yTrain, 4, Color.red, "accuracyTrain", false, true, false);
            UI.sp.setXY(x, yTest, 4, Color.orange, "accuracyTest", false, true, false);
            UI.sp.refreshPlot();
    	//}
        plotTime = System.currentTimeMillis();
            if ( distances != null)
            	if ( DS.numSamples == distances.length) {
		            float[] xTrain = null;
		            yTrain = null;
		            float[] xTest = null;
		            yTest = null;
		            float[] xOTrain = null;
		            Color[] yTrainCol = null;
		            float[] yOTrain = null;
		            float[] xOTest = null;
		            float[] yOTest = null;
		            
		            if ( numTrain < 0 ) {
			            xTrain = new float[distances.length];
			            yTrain = new float[distances.length];
			            xTest = new float[distances.length];
			            yTest = new float[distances.length];
			            xOTrain = new float[distances.length];
			            yOTrain = new float[distances.length];
			            yTrainCol = new Color[distances.length];
			            xOTest = new float[distances.length];
			            yOTest = new float[distances.length];
            		}else {
            			xTrain = new float[numTrain];
			            yTrain = new float[numTrain];
			            xTest = new float[numTest];
			            yTest = new float[numTest];
			            xOTrain = new float[numElseTrain];
			            yOTrain = new float[numElseTrain];
			            yTrainCol = new Color[numElseTrain];
			            xOTest = new float[numElseTest];
			            yOTest = new float[numElseTest];
            		}
		            int ttr = 0; int tte = 0;int oCTr = 0;int oCTe = 0;
		            for (int f=0;f<DS.numSamples;f++){
		                if ( DS.classIndex[f] == targetColorIndex) {
		                	if ( trainSet[f]) {
			                	xTrain[ttr] = (float)distances[f];
			                	yTrain[ttr] = f;
			                	ttr++;
		                	}else {
		                		xTest[tte] = (float)distances[f];
			                	yTest[tte] = f;
			                	tte++;
		                	}
		                }else {
		                	if ( trainSet[f]) {
			                	xOTrain[oCTr] = (float)distances[f];
			                	yTrainCol[oCTr] = Tools.getClassColor(Classify.getTargetColorIndexPos(DS.classIndex[f]));
			                	yOTrain[oCTr] = f;
			                	oCTr++;
		                	}else{
		                		xOTest[oCTe] = (float)distances[f];
			                	yOTest[oCTe] = f;
			                	oCTe++;
		                	}
		                }
		                
		            }
		            numTrain = ttr;
		            numTest = tte;
		            numElseTrain = oCTr;
		            numElseTest = oCTe;
		            UI.spDst.dats.clear();
		            int pSize = 8;
		            if ( DS.numSamples>1000)pSize = 4;
		            
		            UI.spDst.setXY(xTest,yTest, pSize, Color.LIGHT_GRAY, null, true, false, false);
		            UI.spDst.setXY(xOTest,yOTest, pSize, Color.LIGHT_GRAY, null, true, false, false);
		            //71: ClassColor in LIVE
		            //UI.spDst.setXY(xTrain,yTrain, pSize, new Color(0, 130, 0), "Train: " + DS.classAllIndNme[Classify.getTargetColorIndexPos (targetColorIndex)], true, false, false);
		            //71: ClassColor in LIVE
		            UI.spDst.setXY(xTrain,yTrain, pSize, Tools.getClassColor(index), "Train: " + DS.classAllIndNme[index], true, false, false);
		           // UI.spDst.setXY(xOTrain,yOTrain, pSize, new Color(220,54,39), "OtherTrain", true, false, false);
		            UI.spDst.setXY(xOTrain,yOTrain, pSize, yTrainCol, "Testing", true, false, false);
		            
		            UI.spDst.setXY(xSigmoid,ySigmoid, pSize, Color.black, null, false, true, false);
		            UI.spDst.refreshPlot();

	            }
            	
	            // Integral
	            float[] fiendRatio = new float[101];
	            float[] foeRatio = new float[101];
	            float[] fiendRatioTst = new float[101];
	            float[] foeRatioTst = new float[101];
	            float[] fx = new float[101];
	            int fiendTrainCount =0;
	            int foeTrainCount =0;
	            int fiendTstCount =0;
	            int foeTstCount =0;
	            double imax = 0;
	            
	           
	            for (int i=0;i<distances.length;i++) {
	                if ( imax < distances[i])  imax = distances[i];
	            }
	            for (int i=0;i<distances.length;i++) {
	                if ( DS.classIndex[i] == targetColorIndex && trainSet[i]) {
	                    int pos =(int) (100*distances[i]/imax) ;
	                    fiendRatio[pos]++;
	                    fiendTrainCount++;
	                }
	                if ( DS.classIndex[i] != targetColorIndex && trainSet[i]) {
	                    int pos =(int) (100*distances[i]/imax) ;
	                    foeRatio[pos]++;
	                    foeTrainCount++;
	                }
	                if ( DS.classIndex[i] == targetColorIndex && !trainSet[i]) {
	                    int pos =(int) (100*distances[i]/imax) ;
	                    fiendRatioTst[pos]++;
	                    fiendTstCount++;
	                }
	                if ( DS.classIndex[i] != targetColorIndex && !trainSet[i]) {
	                    int pos =(int) (100*distances[i]/imax) ;
	                    foeRatioTst[pos]++;
	                    foeTstCount++;
	                }
	            }
	            int sum =0 ;
	            if ( fiendTrainCount > 0) {      
	                for (int i=0;i<fiendRatio.length;i++) {
	                    sum +=fiendRatio[i];
	                    fiendRatio[i] = DS.numSamples-DS.numSamples*sum/fiendTrainCount;
	                }
				}
	            if ( foeTrainCount > 0) {
	                sum =0 ;
	                for (int i=0;i<foeRatio.length;i++) {
	                    sum +=foeRatio[i];
	                    foeRatio[i] = DS.numSamples*sum/foeTrainCount;
	                }
	            }
	            if ( fiendTstCount > 0) {
	                sum =0 ;
	                for (int i=0;i<foeRatio.length;i++) {
	                    sum +=fiendRatioTst[i];
	                    fiendRatioTst[i] = DS.numSamples-DS.numSamples*sum/fiendTstCount;
	                }
	            }
	            if ( foeTstCount > 0) {
	                sum =0 ;
	                for (int i=0;i<foeRatioTst.length;i++) {
	                    sum +=foeRatioTst[i];
	                    foeRatioTst[i] = DS.numSamples*sum/foeTstCount;
	                }
	            }

	            for (int i=0;i<fx.length;i++) {
	            	fx[i] = (float)(i/100.);
	            	
	            }
	            if ( fiendTrainCount > 0) 	UI.spDst.setXY(fx ,fiendRatio, 6, Tools.getClassColor(index), null, false, true, false);
	            if ( foeTrainCount > 0) 	UI.spDst.setXY(fx,foeRatio, 6, new Color(220,54,39), null, false, true, false);
	            if ( fiendTstCount > 0) 	UI.spDst.setXY(fx,fiendRatioTst, 6, Color.LIGHT_GRAY, null, false, true, false);
	            if ( foeTstCount > 0) 		UI.spDst.setXY(fx,foeRatioTst, 6, Color.orange, null, false, true, false);
	            UI.spDst.refreshPlot();
	   	}
	   	if ( UI.maintabbed.getSelectedIndex()==UI.tab_3D) {
	   		set3D();UI.tab3D.repaint();
	   	}
	}
	private int zufi(int again){
    	
		double large = 0.2;
		double small = large*0.1;
        int z0     = (int)Math.floor(Math.random()*11);                // zufi
        Random random = ThreadLocalRandom.current();
        int a = random.nextInt(DS.numVars);
        int pc = random.nextInt(Opts.numDims);
        Random r = new Random();
        
        if  ( again > -1)a = again;
       
        if ( absCount < Opts.noBetterStop &&  absCount < 100 ) z0 = 99;			// Max Variation on StartUp, first noBetterStop just gambling
        
        switch(z0) {
        case 0:                                                            // EIN Area, ALLE PCA's Zufallswert
            for (int i=0;i<Opts.numDims;i++) {
                //mcEigenVec [a][i] +=     0.5 - Math.random();
            	mcEigenVec [a][i] +=      large - Math.random()*2*large;
            }
            break;
        case 1:
            //mcEigenVec [a][pc]  +=     1.-Math.random()*2;                // EIN Area, EIN PCA's Zufallswert
            mcEigenVec [a][pc] = -r.nextDouble()*large+r.nextDouble()*large;
            break;
        case 2:
            for (int sa=0;sa<DS.numVars;sa++){                        // EIN Area, EIN PCA's Zufallswert
                mcEigenVec [sa][pc] += small-Math.random()*2*small;
            }
            break;
        case 3:
            mcEigenVec [a][pc]  *=    -large;
            break;        
        case 4:
            for (int sa=0;sa<DS.numVars;sa++){                        // EIN Area, EIN PCA's Zufallswert
                if ( sa != a)
                for (int i=0;i<Opts.numDims;i++) {
                    mcEigenVec [sa][i] = small- Math.random()*2*small;
                }
            }
            break; 
        case 5:
            for (int sa=0;sa<DS.numVars;sa++){                        // EIN Area, EIN PCA's Zufallswert
                if ( sa != a)
                for (int i=0;i<Opts.numDims;i++) {
                    mcEigenVec [sa][i] = large- Math.random()-2*large;
                }
            }
            break;    
        case 6: 
//        	if ( Opts.doTheLeft )
//        		a = toTheLeft();
        	for (int i=0;i<Opts.numDims;i++) {
                mcEigenVec [a][i] = small- Math.random()*2*small;
            }
        	break;
        case 7: 
        	for (int i=0;i<Opts.numDims;i++) {
                mcEigenVec [a][i] = 0;
            }
        	break;
        case 8: 
        	for (int i=0;i<DS.numVars;i++) {
        		for (int j=0;j<Opts.numDims;j++) {
        			if (a!=i)mcEigenVec [i][j] = 0;
        		}
            }
        	break;      
        case 9: 
    		for (int j=0;j<Opts.numDims;j++) {
    			mcEigenVec [a][j] *= 0.1;
    		}

        	break;      
        default:
            for (int sa=0;sa<DS.numVars;sa++){
                  for (int i=0;i<Opts.numDims;i++) {
                      mcEigenVec [sa][i] =     large- Math.random()*2*large;
                  }
            }
            break;
        }
        

        
        return a;
    }
	private int toTheLeft(){
		// TODO 
		double maxWin = 0;int maxPos = 0;				// die Variable, deren 1% Änderung am meisten bringt
		for (int a=0;a<DS.numVars;a++){
			undoZufi();
			double baseDst = doFullCalc();
            mcEigenVec [a][0] =  0.1;
            double postDst = doFullCalc();
            if (Math.abs(postDst-baseDst)>maxWin) {
            	maxWin = Math.abs(postDst-baseDst);
            	maxPos = a;
            }
		}
		undoZufi();
		 mcEigenVec [maxPos][0] =  0.1;
		return maxPos;
	}
	private double toTheTop(int a){
		// TODO 
		double maxWin = 0;int maxPos = 0;				// die Variable, deren 1% Änderung am meisten bringt
		for (double i=-1;i<1;i+=0.05){
			undoZufi();
			double baseDst = doFullCalc();
            mcEigenVec [a][0] = i;
            double postDst = doFullCalc();
            if (Math.abs(postDst-baseDst)>maxWin) {
            	maxWin = Math.abs(postDst-baseDst);
            	maxPos = a;
            }
		}
		undoZufi();
		return maxPos;
	}
	private double doFullCalc() {
		double ndst = 0 ;
		 calcPlot();
         if ( Opts.dstType.contentEquals("EGO"))         ndst = getDistancesEGO(null);
         if ( Opts.dstType.contentEquals("GROUP"))         ndst = getDistances(null);
         getSplit();
         accuracy = doClassify(true, false);
         ndst *= accuracy;
         return ndst;
	}
	private void logZufi(){
		for (int a=0;a<DS.numVars;a++){
			for (int i=0;i<Opts.numDims;i++) {
				mcEigenVecOld [a][i] = mcEigenVec [a][i];
	        }
	    }
	}
    private void undoZufi(){
        for (int a=0;a<DS.numVars;a++){
            for (int i=0;i<Opts.numDims;i++) {
                mcEigenVec [a][i] = mcEigenVecOld [a][i];
            }
        }
    }
    private void doFreeze(){

        float accuracyTest = doClassify(false, false);
        float accuracyTrain = doClassify(true, false);
        
       
        DS.freezs.add(new MC_Freeze(distanceOld,mcEigenVec.clone() ));
        for (int a=0;a<DS.numVars;a++){
            for (int i=0;i<Opts.numDims;i++) {
                DS.freezs.get(DS.freezs.size()-1).eigenVec [a][i] = mcEigenVec[a][i];
            }
        }
        DS.freezs.get(DS.freezs.size()-1).targetColorIndex 		= targetColorIndex;
        DS.freezs.get(DS.freezs.size()-1).sampleColorIndices 	= DS.classIndex;
        DS.freezs.get(DS.freezs.size()-1).targetLabel 			= targetName;
        DS.freezs.get(DS.freezs.size()-1).distances 			= distances;
        DS.freezs.get(DS.freezs.size()-1).isTrain 				= trainSet;
        DS.freezs.get(DS.freezs.size()-1).distance 				= distanceOld;
        DS.freezs.get(DS.freezs.size()-1).maxDistance			= maxDist;
        DS.freezs.get(DS.freezs.size()-1).averages 				= getAverageTarget().clone();
        DS.freezs.get(DS.freezs.size()-1).accuracyTrain 		= accuracyTrain;
        DS.freezs.get(DS.freezs.size()-1).accuracyTest     		= accuracyTest;
        DS.freezs.get(DS.freezs.size()-1).split    				= split;
        DS.freezs.get(DS.freezs.size()-1).classification   		= classification.clone();
        DS.freezs.get(DS.freezs.size()-1).tp_fp_tn_fn   		= tp_fp_tn_fn.clone();
        //DS.freezs.get(DS.freezs.size()-1).area	   				= anotherIndication;
//System.out.println(distanceOld + "\t" + anotherIndication);
       
    }
    private void calcPlot(){												// Calculate responce of Neurons
        for (int f=0;f<DS.numSamples;f++){
            double[] sum = new double[Opts.numDims];
            for (int i=0;i<Opts.numDims;i++) {
                for (int a=0;a<DS.numVars;a++){
                    sum[i] += DS.normData[f][a] * mcEigenVec[a][i];
                }
                mcPCA[i][f] = sum[i];
            }
        }
    }
    private double getDistancesEGO(double[] avg){					// Egositic, individual focused split
    	// ACHTUNG EGO fucks Classification
    	// > Aber: hier wird weniger gut generalisiert!
    	
        float dstTarget = 0;	
        float dstOther = 0;
        float targetCount = 0;
        float OtherCount = 0;
       if ( avg == null) avg = getAverageTarget();                        // Abstand vom Schwerpunkt Auswahl Target, Zahl zw 0 - 1
       //double[][] avg = getMedianTarget();
       maxDist = 0;
       distances = new double[DS.numSamples];
       for (int f=0;f<DS.numSamples;f++){
               double val = 0;
               for (int i=0;i<Opts.numDims;i++) {
                   val += (Math.pow(mcPCA[i][f]-avg[i], 2) );                
               }
           distances[f]=Math.pow(val, .5);
           //if ( distances[f]>maxDist && trainSet[f]) maxDist = distances[f];
           if ( distances[f]>maxDist ) maxDist = distances[f];
       }
       if ( maxDist == 0)return 0 ;
       double fak = 1./maxDist;
       for (int f=0;f<DS.numSamples;f++){
           distances[f] *= fak;
           if ( trainSet[f]) {
           	if ( DS.classIndex[f] == targetColorIndex ) {
           		dstTarget += getMappedSigmoid(20*(distances[f]-0.5));
           		targetCount++;
           	}
	            if ( DS.classIndex[f] != targetColorIndex ) {
	            	dstOther += getMappedSigmoid(20*(distances[f]-0.5));
	            	OtherCount++;
	            }
           }
       }
       return dstOther/OtherCount - dstTarget/targetCount;
    }
    private double getDistances(double[] avg){
    	maxDist = 0;
        float dstTarget = 0;
        float dstOther = 0;
        float targetCount = 0;
        float OtherCount = 0;
       if ( avg == null) avg = getAverageTarget();                        // Abstand vom Schwerpunkt Auswahl Target, Zahl zw 0 - 1
       //double[][] avg = getMedianTarget();
       
       distances = new double[DS.numSamples];
       for (int f=0;f<DS.numSamples;f++){
               double val = 0;
               for (int i=0;i<Opts.numDims;i++) {
                   val += (Math.pow(mcPCA[i][f]-avg[i], 2) );                
               }
           distances[f]=Math.pow(val, .5);
           if ( distances[f]>maxDist ) maxDist = distances[f];
       }
       if ( maxDist == 0)return 0 ;
       double fak = 1./maxDist;
       int indexOfTarget = Tools.getIndexOfTarget(targetColorIndex); 
       for (int f=0;f<DS.numSamples;f++){
    	   if (booster[f][indexOfTarget] == 0) booster[f][indexOfTarget] = 1;
           distances[f] *= fak;
           if ( trainSet[f]) {
           	if ( DS.classIndex[f] == targetColorIndex ) {
           		dstTarget += (booster[f][indexOfTarget] * distances[f]);
           		targetCount+=booster[f][indexOfTarget];
           	}
	            if ( DS.classIndex[f] != targetColorIndex ) {
	            	dstOther += distances[f];
	            	OtherCount++;
	            }
           }
           
       }
       dstTarget	/= targetCount;
       dstOther		/= OtherCount;
       dstTarget 	-= 0.5;
       dstOther 	-= 0.5;
       dstTarget 	*= 20;
       dstOther 	*= 20;
       return getMappedSigmoid(dstOther) - getMappedSigmoid(dstTarget);
       
    }
    private double getDistancesDDBAK(double[] avg){
    	maxDist = 0;
        float[] dstPerClass = new float[DS.classAllIndices.length];
        float[] classCount = new float[DS.classAllIndices.length];
       if ( avg == null) avg = getAverageTarget();                        // Abstand vom Schwerpunkt Auswahl Target, Zahl zw 0 - 1
       
       distances = new double[DS.numSamples];
       for (int f=0;f<DS.numSamples;f++){
               double val = 0;
               for (int i=0;i<Opts.numDims;i++) {
                   val += (Math.pow(mcPCA[i][f]-avg[i], 2) );                
               }
           distances[f]=Math.pow(val, .5);
           if ( distances[f]>maxDist ) maxDist = distances[f];
       }
       if ( maxDist == 0)return 0 ;
       double fak = 1./maxDist;
       int indexOfTarget = Tools.getIndexOfTarget(targetColorIndex); 
       for (int f=0;f<DS.numSamples;f++){
    	   if (booster[f][indexOfTarget] == 0) booster[f][indexOfTarget] = 1;		// init
           distances[f] *= fak;
           if ( trainSet[f]) {
           	if ( DS.classIndex[f] == targetColorIndex ) {
           		dstPerClass[indexOfTarget] += (booster[f][indexOfTarget] * distances[f]);
           		classCount[indexOfTarget]+=booster[f][indexOfTarget];
           	}
	            if ( DS.classIndex[f] != targetColorIndex ) {
	            	int indexOfOther = Tools.getIndexOfTarget(DS.classIndex[f]);
	            	dstPerClass[indexOfOther] += distances[f];
	            	classCount[indexOfOther]++;
	            }
           }
           
       }
       double sum=0;
       for (int i=0;i<DS.classAllIndices.length;i++) {
    	   dstPerClass[i] 	/= classCount[i];
    	   dstPerClass[i]	-= 0.5;
    	   dstPerClass[i]	*= 20.;
       }
       for (int i=0;i<DS.classAllIndices.length;i++) {
    	   if ( i != indexOfTarget )
    		   sum +=  (getMappedSigmoid(dstPerClass[i]) - getMappedSigmoid(dstPerClass[indexOfTarget]));
       }
       sum /= (DS.classAllIndices.length-1);
//       dstTarget	/= targetCount;
//       dstOther		/= OtherCount;
//       dstTarget 	-= 0.5;
//       dstOther 	-= 0.5;
//       dstTarget 	*= 20;
//       dstOther 	*= 20;
       return sum;
       
    }
    private float doClassify(boolean training, boolean boost) {
        classification = new int [DS.numSamples];
        float tp=0, fp=0, tn=0, fn=0;
        float correct = 0;float all =0;
        int indexOfTarget = Tools.getIndexOfTarget(targetColorIndex); 
        for (int i=0;i<DS.numSamples;i++){
            //if (trainSet[i]) {
                if ( distances[i] < split/100) {
                    classification[i] = targetColorIndex;
                    if (trainSet[i] == training)                    
                        if (DS.classIndex[i] == targetColorIndex) {
                            tp++;correct++;
                        }else {
                            fn++;
                        }
                }else {
                    classification[i] = -1;
                    if ( boost &&  Opts.doBoost && DS.classIndex[i] == targetColorIndex ) booster[i][indexOfTarget] ++;
                    if (trainSet[i] == training) {
                        if (DS.classIndex[i] == targetColorIndex) {
                            fp++;
                        }else {
                            tn++;//correct++;
                        }
                    }
                }
                if (trainSet[i] == training && DS.classIndex[i] == targetColorIndex) all++;
        }

        if ( all == 0) return 0;
        return (correct/all); // balanced accuracy

    }
    private double[] getAverageTarget() {
    	
    	if ( Opts.useMedian) return getMedianTarget();
    	
        double[] avg = new double[Opts.numDims];
        double count = 0;
        if ( targetColorIndex>-1) {
            for (int f=0;f<DS.numSamples;f++){
                if ( DS.classIndex[f] == targetColorIndex && trainSet[f] ){
                    for (int i=0;i<Opts.numDims;i++) {
                        avg[i]  += mcPCA[i][f];
                    }
                    count ++;
                }
            }
            count = 1./count;
            for (int i=0;i<Opts.numDims;i++) {
                avg[i] *= count;
            }

        }
        return avg;
    }
    private double[] getMedianTarget() {
        double[] avg = new double[Opts.numDims];
        if ( targetColorIndex>-1) {
            for (int i=0;i<Opts.numDims;i++) {
                ArrayList<Double> mid = new ArrayList<Double>();
                for (int f=0;f<DS.numSamples;f++){
                    if ( DS.classIndex[f] == targetColorIndex && trainSet[f] ){
                        mid.add(mcPCA[i][f]);
                    }
                }
                double[] midA = new double[mid.size()];
                for (int j=0;j<midA.length;j++) {
                    midA[j] = mid.get(j);
                }
                Arrays.sort(midA);
                avg[i] = midA[(int) (0.5*midA.length)];

            }
        }
        return avg;
    }
    private void getSplit() {
   	 float[] fiendRatio = new float[101];
        float[] foeRatio = new float[101];
        int fiendTrainCount =0;
        int foeTrainCount =0;
        double imax = 0;
        for (int i=0;i<distances.length;i++) {
            if ( imax < distances[i])  imax = distances[i];
        }
        for (int i=0;i<distances.length;i++) {
            if ( DS.classIndex[i] == targetColorIndex && trainSet[i]) {
                int pos =(int) (100*distances[i]/imax) ;
                fiendRatio[pos]++;
                fiendTrainCount++;
            }
            if ( DS.classIndex[i] != targetColorIndex && trainSet[i]) {
                int pos =(int) (100*distances[i]/imax) ;
                foeRatio[pos]++;
                foeTrainCount++;
            }
        }
        
        if ( fiendTrainCount == 0)  fiendTrainCount =1;
        if ( foeTrainCount == 0)  foeTrainCount =1;
      
            int sum =0 ;
            for (int i=0;i<fiendRatio.length;i++) {
                sum +=fiendRatio[i];
                fiendRatio[i] = 100-100*sum/fiendTrainCount;
            }
            sum =0 ;
            for (int i=0;i<foeRatio.length;i++) {
                sum +=foeRatio[i];
                foeRatio[i] = 100*sum/foeTrainCount;
            }
            
//        	for (int i=0;i<fiendRatio.length;i++) {
//        		if ( fiendRatio[i] >= foeRatio[i]) {
//        			anotherIndication +=(100-fiendRatio[i]);
//        		}else {
//        			anotherIndication +=(100-foeRatio[i]);
//        		}
//            }
//            anotherIndication /= 101;
            
            split =0 ;
            boolean has0Split = false;int startSplit = 0;
            for (int i=0;i<foeRatio.length;i++) {
                if ( !has0Split )
                    if ( foeRatio[i]==0 && fiendRatio[i]==0) {
                        startSplit = i;
                        has0Split = true;
                    }
                if (fiendRatio[i]>=foeRatio[i] ) {
                   split = i;
                }
            }
            if ( has0Split ) split = (split+startSplit)/2; 
   }
    private double getMappedSigmoid( double val ) {
    	// TODO: Mapping
    	int v = (int)((val + 10)*10);
    	if ( v < 0)	return 0.;
    	if ( v > 199) {
    		return 1.;
    	}else {
    		return mappedSigmoid[v];
    	}
    }
    public static double getSigmoid( double val ) {
    	// TODO: Mapping
        return 1 / ( 1 + Math.exp( val * -1.));
    }
    private boolean[] getTrainSet() {
       
        boolean[] tSet = new boolean[DS.numSamples];
        float numOfTargets = 0;
        for (int f=0;f<DS.numSamples;f++){                                // anzahl targets
            if ( DS.classIndex[f] == targetColorIndex ) numOfTargets++;
        }
        float targetCount = 0;float foeCount=0;
        ArrayList<Integer> tgts = new ArrayList<Integer>();
        ArrayList<Integer> rest = new ArrayList<Integer>();
        for (int f=0;f<DS.numSamples;f++){
            if ( DS.classIndex[f] == targetColorIndex ) {
                tgts.add(f);
                targetCount++;
            }else {
                rest.add(f);// TODO: Training Daten nicht nur aus Targets, sondern auch Foes
                foeCount++;
            }
        }
        int count = 0;

        while (count < targetCount*(1.-Opts.trainRatio)) {// TODO: Training Daten nicht nur aus Targets, sondern auch Foes
            int rnd = (int) (Math.random()*tgts.size());
            tgts.remove(rnd);
            count++;
        }
        
        count = 0;
        while (count < foeCount*(1.-Opts.trainRatio)) {// TODO: Training Daten nicht nur aus Targets, sondern auch Foes
            int rnd = (int) (Math.random()*rest.size());
            rest.remove(rnd);
            count++;
        }
        for (int f=0;f<DS.numSamples;f++){
            tSet[f] = false;
            for (int i=0;i<tgts.size();i++){
                if (f == tgts.get(i))tSet[f] = true;
            }
            for (int i=0;i<rest.size();i++){
                if (f == rest.get(i))tSet[f] = true;
            }
        }

        return tSet;
    }
    public void set3D() {
	
		UI.tab3D.clearAll();
		UI.tab3D.set2DText(10, 20, "pseudo 3D can only be plotted for three dimensions (numDims = 3)");
		
		if (Opts.numDims != 3) return;
		UI.tab3D.clearAll();
		
		for(int i=0;i<DS.numClasses;i++){
			UI.tab3D.setLegend(DS.legendImage[i])	;
		}
	
		
		double[] avg = getAverageTarget();
		int max = 0;
        for(int i=0;i<mcPCA[0].length;i++){
        	for(int j=0;j<mcPCA.length;j++){
        		if ( max < Math.abs(mcPCA[j][i]-avg[j]) ) max = (int) Math.abs(mcPCA[j][i]-avg[j]);
        	}
        }
        // ACHSE Central
        UI.tab3D.setPreLine(0,0,0, true, 255, 0, 0);
        UI.tab3D.setPreLine(0,max,0, true, 255, 0, 0);
        UI.tab3D.setPreLine(0,0,0, true, 255, 0, 0);
        UI.tab3D.setPreLine(0,0,max, true, 0, 255, 0);
        UI.tab3D.setPreLine(0,0,0, true, 0, 255, 0);
        UI.tab3D.setPreLine(-3*max,0,0, true, 0, 0, 255);
        UI.tab3D.setPreLine(3*max,0,0, true, 0, 0, 255);

        
        UI.tab3D.setText(max, max, -max,  "MC_1");
        UI.tab3D.setText(-max, -max, -max,  "MC_2");
        UI.tab3D.setText(-max, max, max, "MC_3");
       // UI.tab3D.set2DText(10, 20, "Scale: -"+max+" - "+max );
        UI.tab3D.set2DText(10, 20, "Target: " + DS.classAllIndNme[Tools.getIndexOfTarget(targetColorIndex)] );
        
        UI.tab3D.faktor = UI.tab3D.getWidth()/(6.*max);
        UI.tab3D.cFak = 0.5/UI.tab3D.faktor;

        double step = (2*max)/10.;
        for (int ma = 0;ma<11; ma++) {
       	 int m = (int) (ma-max+(ma*step));
       	UI.tab3D.setPreLine(-max,-max,-m, false, 200, 200, 200);
       	UI.tab3D.setPreLine(-max,max,-m, true, 200, 200, 200);
       	 
       	UI.tab3D.setPreLine(-max,m,-max, false,  200, 200, 200);
       	UI.tab3D.setPreLine(-max,m,max, true,  200, 200, 200);
       	 
       	UI.tab3D.setPreLine(m,-max,-max, false,  200, 200, 200);
       	UI.tab3D.setPreLine(m,-max,max, true,  200, 200, 200);
       	 
       	UI.tab3D.setPreLine(-max,-max,-m, false,  200, 200, 200);
       	UI.tab3D.setPreLine(max,-max,-m, true,  200, 200, 200);
       	 
       	UI.tab3D.setPreLine(-max,m,-max, false, 200, 200, 200);
       	UI.tab3D.setPreLine(max,m,-max, true,  200, 200, 200);
       	 
       	UI.tab3D.setPreLine(m,-max,-max, false,  200, 200, 200);
       	UI.tab3D.setPreLine(m,max,-max, true, 200, 200, 200);
        }
        
        Color cn = null;
        
        int pointSize = 6;
        
        for(int i=0;i<mcPCA[0].length;i++){
			int x = (int)(mcPCA[0][i]-avg[0]);
			int y = (int)(mcPCA[1][i]-avg[1]);
			int z = (int)(mcPCA[2][i]-avg[2]);
//			if ( targetColorIndex == DS.classIndex[i]) {
//				cn = Color.green;
//			}else {
//				cn = Color.orange;
//			}
			cn = Tools.getClassColor(Classify.getTargetColorIndexPos(DS.classIndex[i]));
    		UI.tab3D.setCube(x, y, z, pointSize*5, cn, null);
        }

	}
}
