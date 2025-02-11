package rendering.Utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Gardenia {
	
	private int score;
	
	private String sequence1;
	private String sequence2;
	
	private int wArcBrk;
	private int wArcRmv;
	private int wArcAlt;
	private int wNodeDel;
	private int wArcMatch;
	private int n;
	private int m;
	private int [] minutiaIdArray1;
	private int [] minutiaIdArray2;
	
	private Vector<Integer> dag1;
	private Vector<Integer> dag2;
	private Map<Double, Integer> values;
	
		public Gardenia() {
		this.score = -1;
		this.sequence1 = null;
		this.sequence2 = null;
		this.wNodeDel = 2;
		this.wArcBrk = 5;
		this.wArcRmv = 1;
		this.wArcAlt = 7;
		this.wArcMatch = 0;
		values = new HashMap<Double,Integer>();
	}
		

		
	
	public void setIndexMinutiaArray(int[] array1, int[] array2) {
			this.minutiaIdArray1 = array1;
			this.minutiaIdArray2 = array2;
	}
	
	public void setSequences(String sequence1, String sequence2) {
		this.sequence1 = sequence1;
		this.sequence2 = sequence2;
		this.n = sequence1.length();
		this.m = sequence2.length();
		this.dag1 = new Vector<Integer>(sequence1.length());
		this.dag2 = new Vector<Integer>(sequence2.length());
	}
	
	public void setWeight(int wArcBrk, int wArcRmv, int wArcAlt, int wNodeDel) {
		this.wArcAlt = wArcAlt;
		this.wArcBrk = wArcBrk;
		this.wArcRmv = wArcRmv;
		this.wNodeDel = wNodeDel;
	}
	
	public int getScore() {
		if(this.score<0) {
			try {
				makeArrays();
				/*System.out.println("Dag1");
				for(int i = 0; i<dag1.size(); i++) {
					System.out.println(i + " " +dag1.get(i));
				}
				System.out.println("Dag2");
				for(int i = 0; i<dag2.size(); i++) {
					System.out.println(i + " " +dag2.get(i));
				}*/
				//this.score = gardenia(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
				this.score = gardenia(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
				System.out.println(values.size());
				return this.score;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.score;
	}
	
	public Operation getAllOperatios() {
		try {
			makeArrays();
			Operation finalScore = gardenia1(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
			return finalScore;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public void printMatches() {
		try {
			makeArrays();
			setInitialArraySize(10000);
			initArray();
			int startIndex = gardeniaIndex(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
			System.out.println(operations[startIndex].getPartialScore());
			System.out.println("");
			printMatchArray(startIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*	private int gardenia(int i1, int j1, int i2, int j2, int alfa, int currentScore){
		
		int ik = (i1*(n+1))+j1;
		int jk = (i2*(m+1))+j2;
		double key = ik*((m+1)*m)+jk;
		
		if(values.containsKey(key)) {
			return values.get(key);
		}
		
		if(j1<i1 && j2<i2) {
			this.values.put(key,this.wArcMatch);
			return this.wArcMatch;
		}
		
		if(j1<i1) {
			//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
			int countArc = 0;
			for(int i = i2; i<=j2; i++) {
				if(dag2.get(i)>-1) countArc++;
			}
			this.values.put(key, ((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
			if(values.get(key)<0) 
				System.out.println(key + " " + values.get(key) + " "+i1 + " " + j1 +" ;" + i2 + " " + j2);
			//return ((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
			return ((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
		}
		if(j2<i2) {
			//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
			int countArc = 0;
			for(int i = i1; i<=j1; i++) {
				if(dag1.get(i)>-1) countArc++;
			}
			this.values.put(key, ((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
			if(values.get(key)<0) 
				System.out.println(key + " " + values.get(key) + " "+i1 + " " + j1 +" ;" + i2 + " " + j2);
			//return ((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
			return ((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
		}
		
		int minScore = 1000000;
		//esistono entrambi gli archi
		if(this.dag1.get(i1) > -1 && this.dag2.get(i2)> -1) {
			
			int countBranch = 0;
			
			int min;
			//System.out.println("strada arcMatch");
			if (currentScore + this.wArcMatch < alfa) {
				countBranch++;
				int arcMatch = gardenia(i1+1,dag1.get(i1)-1,i2+1,dag2.get(i2)-1, alfa,currentScore + this.wArcMatch); 
				arcMatch = arcMatch + gardenia(dag1.get(i1)+1,j1,dag2.get(i2)+1,j2, alfa, currentScore + arcMatch+this.wArcMatch)+this.wArcMatch;
				if(arcMatch+currentScore <  alfa) alfa = (int) (currentScore + arcMatch);
				if(arcMatch<minScore) minScore=arcMatch;
			}
			
			
			int arcAlt1 = 10000000;
			min=0;
			if(currentScore+this.wArcAlt+ this.wArcMatch < alfa) {
				countBranch++;
				for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
					//System.out.println("strada arcAlt1 " + m1);
					min = gardenia(i1,m1-1,i2+1,dag2.get(i2)-1, alfa, currentScore+this.wArcAlt);
					min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2, alfa, currentScore +min+this.wArcAlt)+this.wArcAlt;
					if(min<arcAlt1) {arcAlt1 = min;};
					if(currentScore+arcAlt1 < alfa) alfa = currentScore+arcAlt1;
					if(arcAlt1<minScore) minScore=arcAlt1;
				}
			}
			
			
			
			int arcAlt2 = 10000000;
			min = 0;
			if(currentScore+this.wArcAlt+ this.wArcMatch<alfa){
				countBranch++;
				for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
					//System.out.println("strada arcAlt2 " + m2);
					min = gardenia(i1+1,dag1.get(i1)-1,i2,m2-1,alfa,currentScore+this.wArcAlt);
					min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2,alfa,currentScore+min+this.wArcAlt)+this.wArcAlt;
					if (min<arcAlt2) {
						arcAlt2 = min;
					}
					if(currentScore+arcAlt2 < alfa) alfa = currentScore+arcAlt2;
					if(arcAlt2<minScore) minScore=arcAlt2;
				}
			}
			
			//System.out.println("strada arcRemoveA2");
			if((currentScore+this.wArcRmv+ this.wArcMatch )< alfa) {
				countBranch++;
				int arcRmvA2 = gardenia(i1,i1-1,i2+1,dag2.get(i2)-1, alfa, currentScore+this.wArcRmv); 
				arcRmvA2 = arcRmvA2 + gardenia(i1,j1,dag2.get(i2)+1,j2, alfa, currentScore + arcRmvA2+this.wArcRmv) + this.wArcRmv;
			
				if(currentScore + arcRmvA2 < alfa) alfa = currentScore + arcRmvA2;
				if(arcRmvA2<minScore) minScore=arcRmvA2;
			}
			
		//	System.out.println("strada arcRemoveA1");
			if ((currentScore+this.wArcRmv+ this.wArcMatch) < alfa) {
				countBranch++;
				int arcRmvA1 = gardenia(i1+1,dag1.get(i1)-1,i2,i2-1, alfa, currentScore+this.wArcRmv);
				arcRmvA1 = arcRmvA1+gardenia(dag1.get(i1)+1,j1,i2,j2, alfa, currentScore  + arcRmvA1+this.wArcRmv ) + this.wArcRmv;
				if(currentScore + arcRmvA1 < alfa) alfa = currentScore + arcRmvA1;
				if(arcRmvA1<minScore) minScore=arcRmvA1;
			}
			
			
			
			min = 0;
			int arcRmv3 = 1000000;
			if (currentScore+this.wArcRmv + this.wArcMatch< alfa) {
				countBranch++;
				for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
					//	System.out.println("strada arcRemove3 " + m1);
						min = gardenia(i1,m1,i2+1,dag2.get(i2)-1, alfa, currentScore+this.wArcRmv ); 
						min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2, alfa,currentScore+ min+this.wArcRmv )+this.wArcRmv;
						if(min<arcRmv3) {arcRmv3 = min;}
						if(currentScore + arcRmv3<alfa) alfa = currentScore + arcRmv3;
						if(arcRmv3<minScore) minScore=arcRmv3;
					}
			}
			
			
			
			
			int arcRmv4 = 10000000;
			min = 0;
			if(currentScore+this.wArcRmv + this.wArcMatch< alfa) {
				countBranch++;
				for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
					//System.out.println("strada arcRemove4 " + m2);
					min= gardenia(i1+1,dag1.get(i1)-1,i2,m2,alfa,currentScore+this.wArcRmv);
					min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2, alfa, currentScore + min+this.wArcRmv )+this.wArcRmv;
					if(min<arcRmv4) {arcRmv4 = min;};
					if(currentScore + arcRmv4 < alfa) alfa = currentScore + arcRmv4;
					if(arcRmv4<minScore) minScore=arcRmv4;
				}
			}
			
			//System.out.println("Esistono entrambi gli archi");
			//System.out.println(operation);
			//if( == 7 )
				this.values.put(key,minScore);
			return minScore;
		
		}else if (this.dag1.get(i1) > -1 && this.dag2.get(i2) == -1) {
			
			int  countBranch= 0;
			//esiste solo a1
				int min;
				
				//node delete
				//System.out.println("strada delete node");
				if(currentScore+this.wNodeDel+ this.wArcMatch<alfa) {
					countBranch++;
					int deleteNode = gardenia(i1,j1,i2+1,j2,alfa,currentScore+this.wNodeDel)+this.wNodeDel;
					if(currentScore + deleteNode<alfa) alfa = currentScore + deleteNode;
					if(deleteNode<minScore) minScore=deleteNode;
				}
				
				//arc breaking 
				int arcBrk = 100000000;
				min=0;
				if(currentScore+this.wArcBrk+ this.wArcMatch<alfa) {
					countBranch++;
					for(int m2 = i2+1; m2<=j2; m2++) {
						//	System.out.println("strada arcBrk " + m2);
							min = gardenia(i1+1,dag1.get(i1)-1,i2+1,m2-1,alfa,currentScore+this.wArcBrk);
							min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2,alfa,currentScore + min+this.wArcBrk)+this.wArcBrk;
							if(min<arcBrk) arcBrk = min;
							if(currentScore + arcBrk<alfa) alfa=currentScore + arcBrk;
							if(arcBrk<minScore) minScore=arcBrk;
						}
				}
				
				//arc altering 1
				
				int arcAlt1 = 100000000;
				min=0;
				if(currentScore+this.wArcAlt+ this.wArcMatch<alfa) {
					countBranch++;
					for(int m2 = i2; m2<=j2; m2++) {
						//	System.out.println("strada arcAlt1 " + m2);
							min = gardenia(i1+1,dag1.get(i1)-1,i2+1,m2,alfa,currentScore+this.wArcAlt);
							min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2,alfa,currentScore + min+this.wArcAlt)+this.wArcAlt;
							if(min<arcAlt1) {arcAlt1=min;}
							if(currentScore + arcAlt1<alfa) alfa=currentScore + arcAlt1;
							if(arcAlt1<minScore) minScore=arcAlt1;
					}
				}
				
				
				//arc altering 2
				int arcAlt2 = 100000000;
				min=0;
				if(currentScore+this.wArcAlt+ this.wArcMatch<alfa) {
					countBranch++;
					for(int m2 = i2; m2<=j2; m2++) {
					//	System.out.println("strada arcAlt2 " + m2);
						min = gardenia(i1+1,dag1.get(i1)-1,i2,m2-1,alfa,currentScore+this.wArcAlt);
						min=min+gardenia(dag1.get(i1)+1,j1,m2+1,j2,alfa,currentScore + min+this.wArcAlt)+this.wArcAlt;
						if(min<arcAlt2) arcAlt2=min;
						if(currentScore + arcAlt2<alfa) alfa = currentScore + arcAlt2;
						if(arcAlt2<minScore) minScore=arcAlt2;
					}
				}
				
				//arc remove
				int arcRmv = 10000000;
			    min = 0;
				if(currentScore+this.wArcRmv + this.wArcMatch< alfa) {
					countBranch++;
					for(int m2 = i2-1; m2<=j2; m2++) {
						//	System.out.println("strada arcRmv " + m2);
							min = gardenia(i1+1,dag1.get(i1)-1,i2,m2, alfa, currentScore+this.wArcRmv);
							min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2,alfa,currentScore+  min+this.wArcRmv)+this.wArcRmv;
							if(min<arcRmv) {arcRmv = min;}
							if(currentScore + arcRmv < alfa) alfa=currentScore + arcRmv;
							if(arcRmv<minScore) minScore=arcRmv;
						}
				}
				
				//System.out.println("esiste solo a1");
				//System.out.println(operation);
				//if( == 5)
					this.values.put(key,minScore);
				return minScore;
			}else if (this.dag1.get(i1) == -1 && this.dag2.get(i2) > -1) {
				int countBranch = 0;
					//esiste solo a2
					int min;
					//node delete
				//	System.out.println("strada delete node");
					if(currentScore+this.wNodeDel<alfa) {
						countBranch++;
						int deleteNode = gardenia(i1+1,j1,i2,j2,alfa,currentScore+this.wNodeDel)+this.wNodeDel;
						if(currentScore + deleteNode < alfa) alfa=currentScore + deleteNode;
						if(deleteNode<minScore) minScore=deleteNode;
					}
					
					//arc breaking 
					int arcBrk = 1000000000;
					min=0;
					if(currentScore+this.wArcBrk+ this.wArcMatch<alfa) {
						for(int m1 = i1+1; m1<=j1; m1++) {
							countBranch++;
						//	System.out.println("strada arcBrk " + m1);
							min = gardenia(i1+1,m1-1,i2+1,dag2.get(i2)-1,alfa,currentScore+this.wArcBrk);
							min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2,alfa,currentScore + min+this.wArcBrk)+this.wArcBrk;
							if(min<arcBrk) arcBrk = min;
							if(currentScore + arcBrk<alfa) alfa=currentScore + arcBrk;
							if(arcBrk<minScore) minScore=arcBrk;
						}		
					}
					
					//arc altering 1
					int arcAlt1 = 1000000000;
					min=0;
					if(currentScore+this.wArcAlt+ this.wArcMatch<alfa) {
						countBranch++;
						for(int m1 = i1; m1<=j1; m1++) {
							//		System.out.println("strada arcAlt1 " + m1);
									min = gardenia(i1+1,m1,i2+1,dag2.get(i2)-1,alfa,currentScore+this.wArcAlt);
									min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2,alfa,currentScore + min+this.wArcAlt)+this.wArcAlt;
									if(min<arcAlt1) arcAlt1=min;
									if(currentScore + arcAlt1<alfa) alfa=currentScore + arcAlt1;
									if(arcAlt1<minScore) minScore=arcAlt1;
						}
					}
					
					
					//arc altering 2
					
					int arcAlt2 = 100000000;
					min=0;
					if(currentScore+this.wArcAlt+ this.wArcMatch<alfa) {
						countBranch++;
						for(int m1 = i1; m1<=j1; m1++) {
							//		System.out.println("strada arcAlt2 " + m1);
									min = gardenia(i1,m1-1,i2+1,dag2.get(i2)-1,alfa,currentScore+this.wArcAlt);
									min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2,alfa,currentScore + min+this.wArcAlt)+this.wArcAlt;
									if(min<arcAlt2) arcAlt2=min;
									if(currentScore + arcAlt2<alfa) alfa=currentScore + arcAlt2;
									if(arcAlt2<minScore) minScore=arcAlt2;
								}
					}
					
					//arc remove
					int arcRmv = 10000000;
					min = 0;
					if(currentScore+this.wArcRmv + this.wArcMatch< alfa) {
						countBranch++;
						for(int m1 = i1-1; m1<=j1; m1++) {
							//		System.out.println("strada arcRmv " + m1);
									min = gardenia(i1,m1,i2+1,dag2.get(i2)-1, alfa, currentScore+this.wArcRmv);
									min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2,alfa, currentScore + min+this.wArcRmv)+this.wArcRmv;
									
									if(min<arcRmv) {
										arcRmv = min;
									}
									if(currentScore + arcRmv < alfa) alfa=currentScore + arcRmv;
									if(arcRmv<minScore) minScore=arcRmv;
								}	
					}
					
					//System.out.println("Esiste solo a2");
					//System.out.println(operation);
					//if( == 5)
						this.values.put(key,minScore);
					return minScore;
				}else {
					//non esiste nessun arco
					//System.out.println("non ci sono archi in nessuno vado avanti");
					return gardenia(i1+1, j1, i2+1, j2,alfa,currentScore);
				}
	}*/

	
private int gardenia(int i1, int j1, int i2, int j2){
		
		int ik = (i1*(n+1))+j1;
		int jk = (i2*(m+1))+j2;
		double key = ik*((m+1)*(m+1))+jk;
		
		if(values.containsKey(key)) {
			return values.get(key);
		}
		
		if(j1<i1 && j2<i2) {
			this.values.put(key,this.wArcMatch);
			return this.wArcMatch;
		}
		
		if(j1<i1) {
			//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
			int countArc = 0;
			for(int i = i2; i<=j2; i++) {
				if(dag2.get(i)>-1) countArc++;
			}
			this.values.put(key, ((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
			if(values.get(key)<0) 
				System.out.println(key + " " + values.get(key) + " "+i1 + " " + j1 +" ;" + i2 + " " + j2);
			//return ((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
			return ((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
		}
		if(j2<i2) {
			//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
			int countArc = 0;
			for(int i = i1; i<=j1; i++) {
				if(dag1.get(i)>-1) countArc++;
			}
			this.values.put(key, ((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
			if(values.get(key)<0) 
				System.out.println(key + " " + values.get(key) + " "+i1 + " " + j1 +" ;" + i2 + " " + j2);
			//return ((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
			return ((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv);
		}
		
		int minScore = 1000000;
		//esistono entrambi gli archi
		if(this.dag1.get(i1) > -1 && this.dag2.get(i2)> -1) {
			
			int min;
				int arcMatch = gardenia(i1+1,dag1.get(i1)-1,i2+1,dag2.get(i2)-1); 
				arcMatch = arcMatch + gardenia(dag1.get(i1)+1,j1,dag2.get(i2)+1,j2)+this.wArcMatch;
				if(arcMatch<minScore) minScore=arcMatch;
			
			
			int arcAlt1 = 10000000;
			min=0;
				for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
					if(dag1.get(m1)==-1) {
					//System.out.println("strada arcAlt1 " + m1);
					min = gardenia(i1,m1-1,i2+1,dag2.get(i2)-1);
					min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2)+this.wArcAlt;
					if(min<arcAlt1) {arcAlt1 = min;};
					if(arcAlt1<minScore) minScore=arcAlt1;
					}
				}
			
			
			
			int arcAlt2 = 10000000;
			min = 0;
				for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
					if(dag2.get(m2) ==-1) {
					//System.out.println("strada arcAlt2 " + m2);
					min = gardenia(i1+1,dag1.get(i1)-1,i2,m2-1);
					min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2)+this.wArcAlt;
					if (min<arcAlt2) {
						arcAlt2 = min;
					}
					if(arcAlt2<minScore) minScore=arcAlt2;
					}
				}
				
				
				//System.out.println("strada arcRemoveA2");
				int arcRmvA2 = gardenia(i1,i1-1,i2+1,dag2.get(i2)-1); 
				arcRmvA2 = arcRmvA2 + gardenia(i1,j1,dag2.get(i2)+1,j2) + this.wArcRmv;
				if(arcRmvA2<minScore) minScore=arcRmvA2;

				
				//	System.out.println("strada arcRemoveA1");
				int arcRmvA1 = gardenia(i1+1,dag1.get(i1)-1,i2,i2-1);
				arcRmvA1 = arcRmvA1+gardenia(dag1.get(i1)+1,j1,i2,j2) + this.wArcRmv;
				if(arcRmvA1<minScore) minScore=arcRmvA1;

			
			
			min = 0;
			int arcRmv3 = 1000000;
				for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
					//	System.out.println("strada arcRemove3 " + m1);
						min = gardenia(i1,m1,i2+1,dag2.get(i2)-1); 
						min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2)+this.wArcRmv;
						if(min<arcRmv3) {arcRmv3 = min;}
						if(arcRmv3<minScore) minScore=arcRmv3;
					}
			
			
			
			
			int arcRmv4 = 10000000;
			min = 0;
				for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
					//System.out.println("strada arcRemove4 " + m2);
					min= gardenia(i1+1,dag1.get(i1)-1,i2,m2);
					min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2)+this.wArcRmv;
					if(min<arcRmv4) {arcRmv4 = min;};
					if(arcRmv4<minScore) minScore=arcRmv4;
				}
			
				this.values.put(key,minScore);
			return minScore;
		
		}else if (this.dag1.get(i1) > -1 && this.dag2.get(i2) == -1) {
			
				int min;
				
				//node delete
					int deleteNode = gardenia(i1,j1,i2+1,j2)+this.wNodeDel;
					if(deleteNode<minScore) minScore=deleteNode;
				
				//arc breaking 
				int arcBrk = 100000000;
				min=0;
					for(int m2 = i2+1; m2<=j2; m2++) {
							min = gardenia(i1+1,dag1.get(i1)-1,i2+1,m2-1);
							min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2)+this.wArcBrk;
							if(min<arcBrk) arcBrk = min;
							if(arcBrk<minScore) minScore=arcBrk;
						}
				
				
					//arc altering 1
				int arcAlt1 = 100000000;
				min=0;
					for(int m2 = i2; m2<=j2; m2++) {
							min = gardenia(i1+1,dag1.get(i1)-1,i2+1,m2);
							min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2)+this.wArcAlt;
							if(min<arcAlt1) {arcAlt1=min;}
							if(arcAlt1<minScore) minScore=arcAlt1;
					}
				
				
				//arc altering 2
				int arcAlt2 = 100000000;
				min=0;
					for(int m2 = i2; m2<=j2; m2++) {
						min = gardenia(i1+1,dag1.get(i1)-1,i2,m2-1);
						min=min+gardenia(dag1.get(i1)+1,j1,m2+1,j2)+this.wArcAlt;
						if(min<arcAlt2) arcAlt2=min;
						if(arcAlt2<minScore) minScore=arcAlt2;
					}
				
				//arc remove
				int arcRmv = 10000000;
			    min = 0;
					for(int m2 = i2-1; m2<=j2; m2++) {
						min = gardenia(i1+1,dag1.get(i1)-1,i2,m2);
						min = min + gardenia(dag1.get(i1)+1,j1,m2+1,j2)+this.wArcRmv;
						if(min<arcRmv) {arcRmv = min;}
						if(arcRmv<minScore) minScore=arcRmv;
					}
				
				
				this.values.put(key,minScore);
				return minScore;
			}else if (this.dag1.get(i1) == -1 && this.dag2.get(i2) > -1) {
					
					//esiste solo a2
					int min;
					
					//node delete
					int deleteNode = gardenia(i1+1,j1,i2,j2)+this.wNodeDel;
					if(deleteNode<minScore) minScore=deleteNode;
					
					//arc breaking 
					int arcBrk = 1000000000;
					min=0;
					for(int m1 = i1+1; m1<=j1; m1++) {
						min = gardenia(i1+1,m1-1,i2+1,dag2.get(i2)-1);
						min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2)+this.wArcBrk;
						if(min<arcBrk) arcBrk = min;
						if(arcBrk<minScore) minScore=arcBrk;
					}		
					
					//arc altering 1
					int arcAlt1 = 1000000000;
					min=0;
					for(int m1 = i1; m1<=j1; m1++) {
						min = gardenia(i1+1,m1,i2+1,dag2.get(i2)-1);
						min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2)+this.wArcAlt;
						if(min<arcAlt1) arcAlt1=min;
						if(arcAlt1<minScore) minScore=arcAlt1;
					}
					
					
					//arc altering 2
					
					int arcAlt2 = 100000000;
					min=0;
					for(int m1 = i1; m1<=j1; m1++) {
						min = gardenia(i1,m1-1,i2+1,dag2.get(i2)-1);
						min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2)+this.wArcAlt;
						if(min<arcAlt2) arcAlt2=min;
						if(arcAlt2<minScore) minScore=arcAlt2;
					}
					
					//arc remove
					int arcRmv = 10000000;
					min = 0;
					for(int m1 = i1-1; m1<=j1; m1++) {
						min = gardenia(i1,m1,i2+1,dag2.get(i2)-1);
						min = min + gardenia(m1+1,j1,dag2.get(i2)+1,j2)+this.wArcRmv;		
						if(min<arcRmv) {
							arcRmv = min;
						}
						if(arcRmv<minScore) minScore=arcRmv;
					}	
					
				this.values.put(key,minScore);
				return minScore;
				}else {
					
					return gardenia(i1+1, j1, i2+1, j2);
				}
	}




private Operation gardenia1(int i1, int j1, int i2, int j2){
	
	
	if(j1<i1 && j2<i2) {
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("Match");
		operation.setPartialScore(this.wArcMatch);
		operation.setOperationLeft(null);
		operation.setOperationRight(null);
		return operation;
	}
	
	if(j1<i1) {
		//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
		int countArc = 0;
		for(int i = i2; i<=j2; i++) {
			if(dag2.get(i)>-1) countArc++;
		}
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("remove");
		operation.setPartialScore(((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
		operation.setOperationLeft(null);
		operation.setOperationRight(null);
		return operation;
	}
	if(j2<i2) {
		//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
		int countArc = 0;
		for(int i = i1; i<=j1; i++) {
			if(dag1.get(i)>-1) countArc++;
		}
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("remove");
		operation.setPartialScore(((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
		operation.setOperationLeft(null);
		operation.setOperationRight(null);
		return operation;
	}
	
	Operation operation = new Operation();
	
	int minScore = 1000000;
	//esistono entrambi gli archi
	if(this.dag1.get(i1) > -1 && this.dag2.get(i2)> -1) {
		
			Operation arcMatch1 = gardenia1(i1+1,dag1.get(i1)-1,i2+1,dag2.get(i2)-1); 
			Operation arcMatch2 = gardenia1(dag1.get(i1)+1,j1,dag2.get(i2)+1,j2);
			int arcMatch = arcMatch1.getPartialScore() + arcMatch2.getPartialScore() + this.wArcMatch;
			if(arcMatch<minScore) {
				minScore=arcMatch;
				//operation.setIndex(i1, dag1.get(i1), i2, dag2.get(i2));
				operation.setIndex(i1, dag1.get(i1), i2, dag2.get(i2));
				operation.setOperation("Arc Match");
				operation.setOperationLeft(arcMatch1);
				operation.setOperationRight(arcMatch2);
			};
		
		int arcAlt1;
			for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
				if(dag1.get(m1) == -1) {
				//System.out.println("strada arcAlt1 " + m1);
				Operation min1 = gardenia1(i1,m1-1,i2+1,dag2.get(i2)-1);
				Operation min2 = gardenia1(m1+1,j1,dag2.get(i2)+1,j2);
				arcAlt1 = min1.getPartialScore() + min2.getPartialScore() +this.wArcAlt; 
				if(arcAlt1<minScore) {
					minScore=arcAlt1;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Altering 1");
					operation.setOperationLeft(min1);
					operation.setOperationRight(min2);
				}
				}
			}
		
		
		
		int arcAlt2;
			for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
				if(dag2.get(m2) == -1) {
				//System.out.println("strada arcAlt2 " + m2);
				Operation min1 = gardenia1(i1+1,dag1.get(i1)-1,i2,m2-1);
				Operation min2 = gardenia1(dag1.get(i1)+1,j1,m2+1,j2);
				arcAlt2 = min1.getPartialScore() + min2.getPartialScore() + this.wArcAlt; 
				if(arcAlt2<minScore) {
					minScore=arcAlt2;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Altering 2");
					operation.setOperationLeft(min1);
					operation.setOperationRight(min2);
				}
				}
			}
			
			
			//System.out.println("strada arcRemoveA2");
			Operation arcRmvA21 = gardenia1(i1,i1-1,i2+1,dag2.get(i2)-1); 
			Operation arcRmvA22 = gardenia1(i1,j1,dag2.get(i2)+1,j2) ;
			int arcRmvA2 = arcRmvA21.getPartialScore() + arcRmvA22.getPartialScore() + this.wArcRmv;
			if(arcRmvA2<minScore) {
				minScore=arcRmvA2;
				operation.setIndex(i1, j1, i2, j2);
				operation.setOperation("Arc Remove A2");
				operation.setOperationLeft(arcRmvA21);
				operation.setOperationRight(arcRmvA22);
			}

			
			//	System.out.println("strada arcRemoveA1");
			Operation arcRmvA11 = gardenia1(i1+1,dag1.get(i1)-1,i2,i2-1);
			Operation arcRmvA12 = gardenia1(dag1.get(i1)+1,j1,i2,j2);
			int arcRmvA1 = arcRmvA11.getPartialScore() + arcRmvA12.getPartialScore() + this.wArcRmv;
			if(arcRmvA1<minScore) {
				minScore=arcRmvA1;
				operation.setIndex(i1, j1, i2, j2);
				operation.setOperation("Arc Remove A1");
				operation.setOperationLeft(arcRmvA11);
				operation.setOperationRight(arcRmvA12);
			}

		
		
		int arcRmv3;
			for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
				//	System.out.println("strada arcRemove3 " + m1);
					Operation arcRmv31 = gardenia1(i1,m1,i2+1,dag2.get(i2)-1); 
					Operation arcRmv32 = gardenia1(m1+1,j1,dag2.get(i2)+1,j2);
					arcRmv3 = arcRmv31.getPartialScore() + arcRmv32.getPartialScore() + this.wArcRmv;
					if(arcRmv3<minScore) {
						minScore=arcRmv3;
						operation.setIndex(i1, j1, i2, j2);
						operation.setOperation("Arc Remove 3");
						operation.setOperationLeft(arcRmv31);
						operation.setOperationRight(arcRmv32);
					}
				}
		
		
		
		
		int arcRmv4;
			for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
				//System.out.println("strada arcRemove4 " + m2);
				Operation arcRmv41= gardenia1(i1+1,dag1.get(i1)-1,i2,m2);
				Operation arcRmv42 = gardenia1(dag1.get(i1)+1,j1,m2+1,j2);
				arcRmv4 = arcRmv41.getPartialScore() + arcRmv42.getPartialScore() + this.wArcRmv;
				if(arcRmv4<minScore) {
					minScore=arcRmv4;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Remove 4");
					operation.setOperationLeft(arcRmv41);
					operation.setOperationRight(arcRmv42);
				}
			}
		
		operation.setPartialScore(minScore);
		return operation;
	
	}else if (this.dag1.get(i1) > -1 && this.dag2.get(i2) == -1) {
		
		operation.setIndex(i1, j1, i2, j2);
			//node delete
				Operation deleteNode1 = gardenia1(i1,j1,i2+1,j2);
				int deleteNode = deleteNode1.getPartialScore() + this.wNodeDel;
				if(deleteNode<minScore) {
					minScore=deleteNode;
					operation.setOperation("Delete Node");
					operation.setOperationLeft(deleteNode1);
					operation.setOperationRight(null);
				}
			
			//arc breaking 
			int arcBrk;
				for(int m2 = i2+1; m2<=j2; m2++) {
						Operation arcBrk1 = gardenia1(i1+1,dag1.get(i1)-1,i2+1,m2-1);
						Operation arcBrk2 = gardenia1(dag1.get(i1)+1,j1,m2+1,j2);
						arcBrk = arcBrk1.getPartialScore() + arcBrk2.getPartialScore() + this.wArcBrk;
						if(arcBrk<minScore) {
							minScore=arcBrk;
							operation.setOperation("Arc Breaking");
							operation.setOperationLeft(arcBrk1);
							operation.setOperationRight(arcBrk2);
						}
					}
			
			
				//arc altering 1
			int arcAlt1;
				for(int m2 = i2; m2<=j2; m2++) {
						Operation arcAlt11 = gardenia1(i1+1,dag1.get(i1)-1,i2+1,m2);
						Operation arcAlt12 = gardenia1(dag1.get(i1)+1,j1,m2+1,j2);
						arcAlt1 = arcAlt11.getPartialScore() + arcAlt12.getPartialScore() + this.wArcAlt;
						if(arcAlt1<minScore) {
							minScore=arcAlt1;
							operation.setOperation("Arc Altering 1");
							operation.setOperationLeft(arcAlt11);
							operation.setOperationRight(arcAlt12);
						}
				}
			
			
			//arc altering 2
			int arcAlt2;
				for(int m2 = i2; m2<=j2; m2++) {
					Operation arcAlt21 = gardenia1(i1+1,dag1.get(i1)-1,i2,m2-1);
					Operation arcAlt22=gardenia1(dag1.get(i1)+1,j1,m2+1,j2);
					arcAlt2 = arcAlt21.getPartialScore() + arcAlt22.getPartialScore() + this.wArcAlt;
					if(arcAlt2<minScore) {
						minScore=arcAlt2;
						operation.setOperation("Arc Altering2");
						operation.setOperationLeft(arcAlt21);
						operation.setOperationRight(arcAlt22);
					}
				}
			
			//arc remove
			int arcRmv;
				for(int m2 = i2-1; m2<=j2; m2++) {
					Operation arcRmv1 = gardenia1(i1+1,dag1.get(i1)-1,i2,m2);
					Operation arcRmv2 = gardenia1(dag1.get(i1)+1,j1,m2+1,j2);
					arcRmv = arcRmv1.getPartialScore() + arcRmv2.getPartialScore() + this.wArcRmv;
					if(arcRmv<minScore) {
						minScore=arcRmv;
						operation.setOperation("Arc Remove");
						operation.setOperationLeft(arcRmv1);
						operation.setOperationRight(arcRmv2);
					}
				}
			
			operation.setPartialScore(minScore);
			return operation;
		}else if (this.dag1.get(i1) == -1 && this.dag2.get(i2) > -1) {
				
				operation.setIndex(i1, j1, i2, j2);
				//esiste solo a2
			
				//node delete
			
				Operation deleteNode1 = gardenia1(i1+1,j1,i2,j2);
				int deleteNode = deleteNode1.getPartialScore() + this.wNodeDel;
				if(deleteNode<minScore) {
					minScore=deleteNode;
					operation.setOperation("Delete Node");
					operation.setOperationLeft(deleteNode1);
					operation.setOperationRight(null);
				}
				
				//arc breaking 
				int arcBrk;
				for(int m1 = i1+1; m1<=j1; m1++) {
					Operation arcBrk1 = gardenia1(i1+1,m1-1,i2+1,dag2.get(i2)-1);
					Operation arcBrk2 = gardenia1(m1+1,j1,dag2.get(i2)+1,j2);
					arcBrk = arcBrk1.getPartialScore() + arcBrk2.getPartialScore() + this.wArcBrk;
					if(arcBrk<minScore) {
						minScore=arcBrk;
						operation.setOperation("Arc Breaking");
						operation.setOperationLeft(arcBrk1);
						operation.setOperationRight(arcBrk2);
					}
				}		
				
				//arc altering 1
				int arcAlt1;
				for(int m1 = i1; m1<=j1; m1++) {
					Operation arcAlt11 = gardenia1(i1+1,m1,i2+1,dag2.get(i2)-1);
					Operation arcAlt12 = gardenia1(m1+1,j1,dag2.get(i2)+1,j2);
					arcAlt1 = arcAlt11.getPartialScore() + arcAlt12.getPartialScore() + this.wArcAlt;
					if(arcAlt1<minScore) {
						minScore=arcAlt1;
						operation.setOperation("Arc Altering 1");
						operation.setOperationLeft(arcAlt11);
						operation.setOperationRight(arcAlt12);
					}
				}
				
				
				//arc altering 2
				
				int arcAlt2;
				for(int m1 = i1; m1<=j1; m1++) {
					Operation arcAlt21 = gardenia1(i1,m1-1,i2+1,dag2.get(i2)-1);
					Operation arcAlt22 = gardenia1(m1+1,j1,dag2.get(i2)+1,j2);
					arcAlt2 = arcAlt21.getPartialScore() + arcAlt22.getPartialScore() + this.wArcAlt;
					if(arcAlt2<minScore) {
						minScore=arcAlt2;
						operation.setOperation("Arc Altering 2");
						operation.setOperationLeft(arcAlt21);
						operation.setOperationRight(arcAlt22);
					}
				}
				
				//arc remove
				int arcRmv;
				for(int m1 = i1-1; m1<=j1; m1++) {
					Operation arcRmv1 = gardenia1(i1,m1,i2+1,dag2.get(i2)-1);
					Operation arcRmv2 = gardenia1(m1+1,j1,dag2.get(i2)+1,j2);
					arcRmv = arcRmv1.getPartialScore() + arcRmv2.getPartialScore() + this.wArcRmv;
					if(arcRmv<minScore) {
						minScore=arcRmv;
						operation.setOperation("Arc Removing");
						operation.setOperationLeft(arcRmv1);
						operation.setOperationRight(arcRmv2);
					}
				}	
				
				
			operation.setPartialScore(minScore);
			return operation;
			}else {
				operation.setIndex(i1, j1, i2, j2);
				Operation nodeMatch = gardenia1(i1+1, j1, i2+1, j2);
				operation.setOperation("Node Match");
				operation.setOperationLeft(nodeMatch);
				operation.setOperationRight(null);
				operation.setPartialScore(nodeMatch.getPartialScore());
				return operation;
			}
}




private int arraySize;
private int initialArraySize;
private Operation[] operations;
private int indexIteration = -1;

public void setInitialArraySize(int arraySize) {
	this.arraySize = arraySize;
	this.initialArraySize = arraySize;
}

private void initArray(){
	operations = new Operation[arraySize];
	for(int i=0; i<arraySize; i++) {
		operations[i]=null;
	}
}

private void allargaArray(int addSize) {
	int newSize = arraySize + addSize;
	Operation[] temp = new Operation[newSize];
	
	for(int i = 0; i<newSize; i++) {
		if(i<arraySize) {
			temp[i]=operations[i];
		}else {
			temp[i] = null;
		}
	}
	arraySize = newSize;
	operations = temp;
}

private int containsIndex(int i1, int j1, int i2, int j2) {
	for(int i = 0; i<arraySize; i++) {
		if(operations[i]==null) {
			return -1;
		}
		if(operations[i].geti1() == i1 && operations[i].getj1() == j1 && operations[i].geti2() == i2 && operations[i].getj2() == j2) {
			return i;
		}
	}
	return -1;
}

public void printMatchArray(int index) {
	if(index == -1) return;
	if(operations[index].getOperationString().contentEquals("Arc Match")) {
		System.out.println(operations[index].getIndexString());
		System.out.println(operations[index].getOperationString());
		System.out.println("");
	}
	printMatchArray(operations[index].getIndexLeft());
	printMatchArray(operations[index].getIndexRight());
}

private int gardeniaIndex(int i1, int j1, int i2, int j2){
	
	int indexC = containsIndex(i1, j1, i2, j2);
	if(indexC != -1) {
		return indexC;
	}
	
	
	
	if(j1<i1 && j2<i2) {
		indexIteration++;
		if(indexIteration == arraySize) {
			allargaArray(arraySize/2);
			System.out.println(arraySize);
		}
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("Match");
		operation.setPartialScore(this.wArcMatch);
		operation.setIndexLeft(-1);
		operation.setIndexRight(-1);
		operations[indexIteration] = operation;
		return indexIteration;
	}
	
	if(j1<i1) {
		indexIteration++;
		if(indexIteration == arraySize) {
			allargaArray(arraySize/2);
			System.out.println(arraySize);
		}
		//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
		int countArc = 0;
		for(int i = i2; i<=j2; i++) {
			if(dag2.get(i)>-1) countArc++;
		}
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("remove");
		operation.setPartialScore(((((j2-i2)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
		operation.setIndexLeft(-1);
		operation.setIndexRight(-1);
		operations[indexIteration] = operation;
		return indexIteration;
	}
	if(j2<i2) {
		indexIteration++;
		if(indexIteration == arraySize) {
			allargaArray(arraySize/2);
			System.out.println(arraySize);
		}
		//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
		int countArc = 0;
		for(int i = i1; i<=j1; i++) {
			if(dag1.get(i)>-1) countArc++;
		}
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("remove");
		operation.setPartialScore(((((j1-i1)+1)-countArc*2)*this.wNodeDel + countArc*this.wArcRmv));
		operation.setIndexLeft(-1);
		operation.setIndexRight(-1);
		operations[indexIteration] = operation;
		return indexIteration;
	}
	
	Operation operation = new Operation();
	
	int minScore = 1000000;
	//esistono entrambi gli archi
	if(this.dag1.get(i1) > -1 && this.dag2.get(i2)> -1) {
		
			int arcMatch1 = gardeniaIndex(i1+1,dag1.get(i1)-1,i2+1,dag2.get(i2)-1); 
			int arcMatch2 = gardeniaIndex(dag1.get(i1)+1,j1,dag2.get(i2)+1,j2);
			int arcMatch = operations[arcMatch1].getPartialScore() + operations[arcMatch2].getPartialScore() + this.wArcMatch;
			if(arcMatch<minScore) {
				minScore=arcMatch;
				operation.setIndex(i1, j1, i2, j2);
				operation.setOperation("Arc Match");
				operation.setIndexLeft(arcMatch1);
				operation.setIndexRight(arcMatch2);
			}
		
		int arcAlt1;
			for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
				if(dag1.get(m1) == -1) {
				//System.out.println("strada arcAlt1 " + m1);
				int min1 = gardeniaIndex(i1,m1-1,i2+1,dag2.get(i2)-1);
				int min2 = gardeniaIndex(m1+1,j1,dag2.get(i2)+1,j2);
				arcAlt1 = operations[min1].getPartialScore() + operations[min2].getPartialScore() +this.wArcAlt; 
				if(arcAlt1<minScore) {
					minScore=arcAlt1;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Altering 1");
					operation.setIndexLeft(min1);
					operation.setIndexRight(min2);
				}
				}
			}
		
		
		
		int arcAlt2;
			for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
				if(dag2.get(m2) == -1) {
				//System.out.println("strada arcAlt2 " + m2);
				int min1 = gardeniaIndex(i1+1,dag1.get(i1)-1,i2,m2-1);
				int min2 = gardeniaIndex(dag1.get(i1)+1,j1,m2+1,j2);
				arcAlt2 = operations[min1].getPartialScore() + operations[min2].getPartialScore() + this.wArcAlt; 
				if(arcAlt2<minScore) {
					minScore=arcAlt2;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Altering 2");
					operation.setIndexLeft(min1);
					operation.setIndexRight(min2);
				}
				}
			}
			
			
			//System.out.println("strada arcRemoveA2");
			int arcRmvA21 = gardeniaIndex(i1,i1-1,i2+1,dag2.get(i2)-1); 
			int arcRmvA22 = gardeniaIndex(i1,j1,dag2.get(i2)+1,j2) ;
			int arcRmvA2 = operations[arcRmvA21].getPartialScore() + operations[arcRmvA22].getPartialScore() + this.wArcRmv;
			if(arcRmvA2<minScore) {
				minScore=arcRmvA2;
				operation.setIndex(i1, j1, i2, j2);
				operation.setOperation("Arc Remove A2");
				operation.setIndexLeft(arcRmvA21);
				operation.setIndexRight(arcRmvA22);
			}

			
			//	System.out.println("strada arcRemoveA1");
			int arcRmvA11 = gardeniaIndex(i1+1,dag1.get(i1)-1,i2,i2-1);
			int arcRmvA12 = gardeniaIndex(dag1.get(i1)+1,j1,i2,j2);
			int arcRmvA1 = operations[arcRmvA11].getPartialScore() + operations[arcRmvA12].getPartialScore() + this.wArcRmv;
			if(arcRmvA1<minScore) {
				minScore=arcRmvA1;
				operation.setIndex(i1, j1, i2, j2);
				operation.setOperation("Arc Remove A1");
				operation.setIndexLeft(arcRmvA11);
				operation.setIndexRight(arcRmvA12);
			}

		
		
		int arcRmv3;
			for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
				//	System.out.println("strada arcRemove3 " + m1);
					int arcRmv31 = gardeniaIndex(i1,m1,i2+1,dag2.get(i2)-1); 
					int arcRmv32 = gardeniaIndex(m1+1,j1,dag2.get(i2)+1,j2);
					arcRmv3 = operations[arcRmv31].getPartialScore() + operations[arcRmv32].getPartialScore() + this.wArcRmv;
					if(arcRmv3<minScore) {
						minScore=arcRmv3;
						operation.setIndex(i1, j1, i2, j2);
						operation.setOperation("Arc Remove 3");
						operation.setIndexLeft(arcRmv31);
						operation.setIndexRight(arcRmv32);
					}
				}
		
		
		
		
		int arcRmv4;
			for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
				//System.out.println("strada arcRemove4 " + m2);
				int arcRmv41= gardeniaIndex(i1+1,dag1.get(i1)-1,i2,m2);
				int arcRmv42 = gardeniaIndex(dag1.get(i1)+1,j1,m2+1,j2);
				arcRmv4 = operations[arcRmv41].getPartialScore() + operations[arcRmv42].getPartialScore() + this.wArcRmv;
				if(arcRmv4<minScore) {
					minScore=arcRmv4;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Remove 4");
					operation.setIndexLeft(arcRmv41);
					operation.setIndexRight(arcRmv42);
				}
			}
		
		indexIteration++;
		if(indexIteration == arraySize) {
			allargaArray(arraySize/2);
			System.out.println(arraySize);
		}
		operation.setPartialScore(minScore);
		operations[indexIteration] = operation;
		return indexIteration;
	
	}else if (this.dag1.get(i1) > -1 && this.dag2.get(i2) == -1) {
		
		operation.setIndex(i1, j1, i2, j2);
			//node delete
				int deleteNode1 = gardeniaIndex(i1,j1,i2+1,j2);
				int deleteNode = operations[deleteNode1].getPartialScore() + this.wNodeDel;
				if(deleteNode<minScore) {
					minScore=deleteNode;
					operation.setOperation("Delete Node");
					operation.setIndexLeft(deleteNode1);
					operation.setIndexRight(-1);
				}
			
			//arc breaking 
			int arcBrk;
				for(int m2 = i2+1; m2<=j2; m2++) {
						int arcBrk1 = gardeniaIndex(i1+1,dag1.get(i1)-1,i2+1,m2-1);
						int arcBrk2 = gardeniaIndex(dag1.get(i1)+1,j1,m2+1,j2);
						arcBrk = operations[arcBrk1].getPartialScore() + operations[arcBrk2].getPartialScore() + this.wArcBrk;
						if(arcBrk<minScore) {
							minScore=arcBrk;
							operation.setOperation("Arc Breaking");
							operation.setIndexLeft(arcBrk1);
							operation.setIndexRight(arcBrk2);
						}
					}
			
			
				//arc altering 1
			int arcAlt1;
				for(int m2 = i2; m2<=j2; m2++) {
						int arcAlt11 = gardeniaIndex(i1+1,dag1.get(i1)-1,i2+1,m2);
						int arcAlt12 = gardeniaIndex(dag1.get(i1)+1,j1,m2+1,j2);
						arcAlt1 = operations[arcAlt11].getPartialScore() + operations[arcAlt12].getPartialScore() + this.wArcAlt;
						if(arcAlt1<minScore) {
							minScore=arcAlt1;
							operation.setOperation("Arc Altering 1");
							operation.setIndexLeft(arcAlt11);
							operation.setIndexRight(arcAlt12);
						}
				}
			
			
			//arc altering 2
			int arcAlt2;
				for(int m2 = i2; m2<=j2; m2++) {
					int arcAlt21 = gardeniaIndex(i1+1,dag1.get(i1)-1,i2,m2-1);
					int arcAlt22=gardeniaIndex(dag1.get(i1)+1,j1,m2+1,j2);
					arcAlt2 = operations[arcAlt21].getPartialScore() + operations[arcAlt22].getPartialScore() + this.wArcAlt;
					if(arcAlt2<minScore) {
						minScore=arcAlt2;
						operation.setOperation("Arc Altering2");
						operation.setIndexLeft(arcAlt21);
						operation.setIndexRight(arcAlt22);
					}
				}
			
			//arc remove
			int arcRmv;
				for(int m2 = i2-1; m2<=j2; m2++) {
					int arcRmv1 = gardeniaIndex(i1+1,dag1.get(i1)-1,i2,m2);
					int arcRmv2 = gardeniaIndex(dag1.get(i1)+1,j1,m2+1,j2);
					arcRmv = operations[arcRmv1].getPartialScore() + operations[arcRmv2].getPartialScore() + this.wArcRmv;
					if(arcRmv<minScore) {
						minScore=arcRmv;
						operation.setOperation("Arc Remove");
						operation.setIndexLeft(arcRmv1);
						operation.setIndexRight(arcRmv2);
					}
				}
			indexIteration++;
			if(indexIteration == arraySize) {
				allargaArray(arraySize/2);
				System.out.println(arraySize);
			}
			operation.setPartialScore(minScore);
			operations[indexIteration] = operation;
			return indexIteration;
		}else if (this.dag1.get(i1) == -1 && this.dag2.get(i2) > -1) {
				
				operation.setIndex(i1, j1, i2, j2);
				//esiste solo a2
			
				//node delete
			
				int deleteNode1 = gardeniaIndex(i1+1,j1,i2,j2);
				int deleteNode = operations[deleteNode1].getPartialScore() + this.wNodeDel;
				if(deleteNode<minScore) {
					minScore=deleteNode;
					operation.setOperation("Delete Node");
					operation.setIndexLeft(deleteNode1);
					operation.setIndexRight(-1);
				}
				
				//arc breaking 
				int arcBrk;
				for(int m1 = i1+1; m1<=j1; m1++) {
					int arcBrk1 = gardeniaIndex(i1+1,m1-1,i2+1,dag2.get(i2)-1);
					int arcBrk2 = gardeniaIndex(m1+1,j1,dag2.get(i2)+1,j2);
					arcBrk = operations[arcBrk1].getPartialScore() + operations[arcBrk2].getPartialScore() + this.wArcBrk;
					if(arcBrk<minScore) {
						minScore=arcBrk;
						operation.setOperation("Arc Breaking");
						operation.setIndexLeft(arcBrk1);
						operation.setIndexRight(arcBrk2);
					}
				}		
				
				//arc altering 1
				int arcAlt1;
				for(int m1 = i1; m1<=j1; m1++) {
					int arcAlt11 = gardeniaIndex(i1+1,m1,i2+1,dag2.get(i2)-1);
					int arcAlt12 = gardeniaIndex(m1+1,j1,dag2.get(i2)+1,j2);
					arcAlt1 = operations[arcAlt11].getPartialScore() + operations[arcAlt12].getPartialScore() + this.wArcAlt;
					if(arcAlt1<minScore) {
						minScore=arcAlt1;
						operation.setOperation("Arc Altering 1");
						operation.setIndexLeft(arcAlt11);
						operation.setIndexRight(arcAlt12);
					}
				}
				
				
				//arc altering 2
				
				int arcAlt2;
				for(int m1 = i1; m1<=j1; m1++) {
					int arcAlt21 = gardeniaIndex(i1,m1-1,i2+1,dag2.get(i2)-1);
					int arcAlt22 = gardeniaIndex(m1+1,j1,dag2.get(i2)+1,j2);
					arcAlt2 = operations[arcAlt21].getPartialScore() + operations[arcAlt22].getPartialScore() + this.wArcAlt;
					if(arcAlt2<minScore) {
						minScore=arcAlt2;
						operation.setOperation("Arc Altering 2");
						operation.setIndexLeft(arcAlt21);
						operation.setIndexRight(arcAlt22);
					}
				}
				
				//arc remove
				int arcRmv;
				for(int m1 = i1-1; m1<=j1; m1++) {
					int arcRmv1 = gardeniaIndex(i1,m1,i2+1,dag2.get(i2)-1);
					int arcRmv2 = gardeniaIndex(m1+1,j1,dag2.get(i2)+1,j2);
					arcRmv = operations[arcRmv1].getPartialScore() + operations[arcRmv2].getPartialScore() + this.wArcRmv;
					if(arcRmv<minScore) {
						minScore=arcRmv;
						operation.setOperation("Arc Removing");
						operation.setIndexLeft(arcRmv1);
						operation.setIndexRight(arcRmv2);
					}
				}	
				
			indexIteration++;
			if(indexIteration == arraySize) {
				allargaArray(arraySize/2);
				System.out.println(arraySize);
			}	
			operation.setPartialScore(minScore);
			operations[indexIteration] = operation;
			return indexIteration;
			}else {
				operation.setIndex(i1, j1, i2, j2);
				int nodeMatch = gardeniaIndex(i1+1, j1, i2+1, j2);
				operation.setOperation("Node Match");
				operation.setIndexLeft(nodeMatch);
				operation.setIndexRight(-1);
				operation.setPartialScore(operations[nodeMatch].getPartialScore());
				indexIteration++;
				if(indexIteration == arraySize) {
					allargaArray(arraySize/2);
					System.out.println(arraySize);
				}
				operations[indexIteration] = operation; 
				return indexIteration;
			}
}




private Map<Double,Operation> operationMap = new HashMap<Double, Operation>();


public void printMatchMapMinutiaOnFile(BufferedWriter bw, String file1, String file2) {
	
	try {
		makeArrays();
		
		double startIndex = gardeniaKey(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
		bw.write("score: " + operationMap.get(startIndex).getPartialScore() + "\n \n");
		printMatchMapMinutia(startIndex,bw,file1,file2);
		bw.flush();
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void printMatchMapMinutia(double index, BufferedWriter bw, String file1, String file2) {
	if(index == -1) return;
	if(operationMap.get(index).getOperationString().contentEquals("Arc Match")) {
		try {
			bw.write("match("+this.minutiaIdArray1[operationMap.get(index).geti1()]+", "
					+this.minutiaIdArray1[operationMap.get(index).getj1()]+", "
					+file1 + ", "
					+this.minutiaIdArray2[operationMap.get(index).geti2()]+", "
					+this.minutiaIdArray2[operationMap.get(index).getj2()]+", "
					+file2 + ")"+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	printMatchMapMinutia(operationMap.get(index).keyLeft,bw, file1,file2);
	printMatchMapMinutia(operationMap.get(index).keyRight,bw, file1,file2);
}




public void printMatchMapOnFile(BufferedWriter bw, String file1, String file2) {
	
	try {
		makeArrays();
		
		double startIndex = gardeniaKey(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
		bw.write("score: " + operationMap.get(startIndex).getPartialScore() + "\n \n");
		printMatchMap(startIndex,bw,file1,file2);
		bw.flush();
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void printMatchMap(double index, BufferedWriter bw, String file1, String file2) {
	if(index == -1) return;
	if(operationMap.get(index).getOperationString().contentEquals("Arc Match")) {
		try {
			bw.write("match("+operationMap.get(index).geti1()+", "
					+operationMap.get(index).getj1()+", "
					+file1 + ", "
					+operationMap.get(index).geti2()+", "
					+operationMap.get(index).getj2()+", "
					+file2 + ")"+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	printMatchMap(operationMap.get(index).keyLeft,bw, file1,file2);
	printMatchMap(operationMap.get(index).keyRight,bw, file1,file2);
}

public void getMatchMap() {
	try {
		makeArrays();
		double startIndex = gardeniaKey(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
		System.out.println(operationMap.get(startIndex).getPartialScore());
		System.out.println("");
		printMatchMap(startIndex);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public int getScoreKey() {
	try {
		makeArrays();
		double startIndex = gardeniaKey(0,this.sequence1.length()-1,0,this.sequence2.length()-1);
		return operationMap.get(startIndex).getPartialScore();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return -1000000;
}

private void printMatchMap(double index) {
	if(index == -1) return;
	//if(operationMap.get(index).getOperationString().contentEquals("Arc Match")) {
		System.out.println(operationMap.get(index).getIndexString());
		System.out.println(operationMap.get(index).getOperationString());
		System.out.println("");
	//}
	printMatchMap(operationMap.get(index).keyLeft);
	printMatchMap(operationMap.get(index).keyRight);
}


private double gardeniaKey(int i1, int j1, int i2, int j2){
	
	int ik = (i1*(n+1))+j1;
	int jk = (i2*(m+1))+j2;
	double key = ik*((m+1)*(m+1))+jk;
	
	if(operationMap.containsKey(key)) {
		return key;
	}
	
	
	if(j1<i1 && j2<i2) {
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("Match");
		operation.setPartialScore(this.wArcMatch);
		operation.keyLeft = -1;
		operation.keyRight = -1;
		operationMap.put(key, operation);
		return key;
	}
	
	if(j1<i1) {
		//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
		int countArc = 0;
		for(int i = i2; i<=j2; i++) {
			if(dag2.get(i)>-1) countArc++;
		}
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("remove");
		
		operation.setPartialScore(((((j2-i2)+1)-countArc)*this.wNodeDel + countArc*10));
		operation.keyLeft = -1;
		operation.keyRight = -1;
		operationMap.put(key, operation);
		return key;
	}
	if(j2<i2) {
		//System.out.println("sono arrivato alla fine con indici "+ i1 + " "+ j1 + " " + i2 + " " + j2);
		int countArc = 0;
		for(int i = i1; i<=j1; i++) {
			if(dag1.get(i)>-1) countArc++;
		}
		Operation operation = new Operation();
		operation.setIndex(i1, j1, i2, j2);
		operation.setOperation("remove");
		operation.setPartialScore(((((j1-i1)+1)-countArc)*this.wNodeDel + countArc*10));
		operation.keyLeft = -1;
		operation.keyRight = -1;
		operationMap.put(key, operation);
		return key;
	}
	
	Operation operation = new Operation();
	
	int minScore = 1000000;
	//esistono entrambi gli archi
	if(this.dag1.get(i1) > -1 && this.dag2.get(i2)> -1) {
		
			double arcMatch1 = gardeniaKey(i1+1,dag1.get(i1)-1,i2+1,dag2.get(i2)-1); 
			double arcMatch2 = gardeniaKey(dag1.get(i1)+1,j1,dag2.get(i2)+1,j2);
			int arcMatch = operationMap.get(arcMatch1).getPartialScore() + operationMap.get(arcMatch2).getPartialScore() + this.wArcMatch;
			if(arcMatch<minScore) {
				minScore=arcMatch;
				operation.setIndex(i1, dag1.get(i1), i2, dag2.get(i2));
				operation.setOperation("Arc Match");
				operation.keyLeft=arcMatch1;
				operation.keyRight=arcMatch2;
			}
		
		
			
			//System.out.println("strada arcRemoveA2");
			double arcRmvA21 = gardeniaKey(i1,i1-1,i2+1,dag2.get(i2)-1); 
			double arcRmvA22 = gardeniaKey(i1,j1,dag2.get(i2)+1,j2) ;
			int arcRmvA2 = operationMap.get(arcRmvA21).getPartialScore() + operationMap.get(arcRmvA22).getPartialScore() + this.wArcRmv;
			if(arcRmvA2<minScore) {
				minScore=arcRmvA2;
				operation.setIndex(i1, j1, i2, j2);
				operation.setOperation("Arc Remove A2");
				operation.keyLeft = arcRmvA21;
				operation.keyRight = arcRmvA22;
			}

			
			//	System.out.println("strada arcRemoveA1");
			double arcRmvA11 = gardeniaKey(i1+1,dag1.get(i1)-1,i2,i2-1);
			double arcRmvA12 = gardeniaKey(dag1.get(i1)+1,j1,i2,j2);
			int arcRmvA1 = operationMap.get(arcRmvA11).getPartialScore() + operationMap.get(arcRmvA12).getPartialScore() + this.wArcRmv;
			if(arcRmvA1<minScore) {
				minScore=arcRmvA1;
				operation.setIndex(i1, j1, i2, j2);
				operation.setOperation("Arc Remove A1");
				operation.keyLeft = arcRmvA11;
				operation.keyRight = arcRmvA12;
			}

		
		
		int arcRmv3;
			for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
				//	System.out.println("strada arcRemove3 " + m1);
					double arcRmv31 = gardeniaKey(i1,m1,i2+1,dag2.get(i2)-1); 
					double arcRmv32 = gardeniaKey(m1+1,j1,dag2.get(i2)+1,j2);
					arcRmv3 = operationMap.get(arcRmv31).getPartialScore() + operationMap.get(arcRmv32).getPartialScore() + this.wArcRmv;
					if(arcRmv3<minScore) {
						minScore=arcRmv3;
						operation.setIndex(i1, j1, i2, j2);
						operation.setOperation("Arc Remove 3");
						operation.keyLeft=arcRmv31;
						operation.keyRight=arcRmv32;
					}
				}
		
		
		
		
		int arcRmv4;
			for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
				//System.out.println("strada arcRemove4 " + m2);
				double arcRmv41= gardeniaKey(i1+1,dag1.get(i1)-1,i2,m2);
				double arcRmv42 = gardeniaKey(dag1.get(i1)+1,j1,m2+1,j2);
				arcRmv4 = operationMap.get(arcRmv41).getPartialScore() + operationMap.get(arcRmv42).getPartialScore() + this.wArcRmv;
				if(arcRmv4<minScore) {
					minScore=arcRmv4;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Remove 4");
					operation.keyLeft=arcRmv41;
					operation.keyRight=arcRmv42;
				}
			}
		
			int arcAlt1;
			for(int m1 = dag1.get(i1)+1; m1<=j1; m1++) {
				//System.out.println("strada arcAlt1 " + m1);
				double min1 = gardeniaKey(i1,m1-1,i2+1,dag2.get(i2)-1);
				double min2 = gardeniaKey(m1+1,j1,dag2.get(i2),j2);
				arcAlt1 = operationMap.get(min1).getPartialScore() + operationMap.get(min2).getPartialScore() +this.wArcAlt; 
				if(arcAlt1<minScore) {
					minScore=arcAlt1;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Altering 1");
					operation.keyLeft=min1;
					operation.keyRight=min2;
				}
			}
		
		
		
		int arcAlt2;
			for(int m2 = dag2.get(i2)+1; m2<=j2; m2++) {
				//System.out.println("strada arcAlt2 " + m2);
				double min1 = gardeniaKey(i1+1,dag1.get(i1)-1,i2,m2-1);
				double min2 = gardeniaKey(dag1.get(i1),j1,m2+1,j2);
				arcAlt2 = operationMap.get(min1).getPartialScore() + operationMap.get(min2).getPartialScore() + this.wArcAlt; 
				if(arcAlt2<minScore) {
					minScore=arcAlt2;
					operation.setIndex(i1, j1, i2, j2);
					operation.setOperation("Arc Altering 2");
					operation.keyLeft = min1;
					operation.keyRight=min2;
				}
			}
			
		
		operation.setPartialScore(minScore);
		operationMap.put(key, operation);
		return key;
	
	}else if (this.dag1.get(i1) > -1 && this.dag2.get(i2) == -1) {
		
		operation.setIndex(i1, j1, i2, j2);
			//node delete
				double deleteNode1 = gardeniaKey(i1,j1,i2+1,j2);
				int deleteNode = operationMap.get(deleteNode1).getPartialScore() + this.wNodeDel;
				if(deleteNode<minScore) {
					minScore=deleteNode;
					operation.setOperation("Delete Node");
					operation.keyLeft=deleteNode1;
					operation.keyRight =-1;
				}
			
			//arc breaking 
			int arcBrk;
				for(int m2 = i2+1; m2<=j2; m2++) {
						double arcBrk1 = gardeniaKey(i1+1,dag1.get(i1)-1,i2+1,m2-1);
						double arcBrk2 = gardeniaKey(dag1.get(i1),j1,m2+1,j2);
						arcBrk = operationMap.get(arcBrk1).getPartialScore() + operationMap.get(arcBrk2).getPartialScore() + this.wArcBrk;
						if(arcBrk<minScore) {
							minScore=arcBrk;
							operation.setOperation("Arc Breaking");
							operation.keyLeft = arcBrk1;
							operation.keyRight = arcBrk2;
						}
					}
			
			
				//arc altering 1
			int arcAlt1;
				for(int m2 = i2; m2<=j2; m2++) {
						double arcAlt11 = gardeniaKey(i1+1,dag1.get(i1)-1,i2+1,m2);
						double arcAlt12 = gardeniaKey(dag1.get(i1)+1,j1,m2+1,j2);
						arcAlt1 = operationMap.get(arcAlt11).getPartialScore() + operationMap.get(arcAlt12).getPartialScore() + this.wArcAlt;
						if(arcAlt1<minScore) {
							minScore=arcAlt1;
							operation.setOperation("Arc Altering 1");
							operation.keyLeft=arcAlt11;
							operation.keyRight=arcAlt12;
						}
				}
			
			
			//arc altering 2
			int arcAlt2;
				for(int m2 = i2; m2<=j2; m2++) {
					double arcAlt21 = gardeniaKey(i1+1,dag1.get(i1)-1,i2,m2-1);
					double arcAlt22=gardeniaKey(dag1.get(i1)+1,j1,m2+1,j2);
					arcAlt2 = operationMap.get(arcAlt21).getPartialScore() + operationMap.get(arcAlt22).getPartialScore() + this.wArcAlt;
					if(arcAlt2<minScore) {
						minScore=arcAlt2;
						operation.setOperation("Arc Altering2");
						operation.keyLeft = arcAlt21;
						operation.keyRight = arcAlt22;
					}
				}
			
			//arc remove
			int arcRmv;
				for(int m2 = i2-1; m2<=j2; m2++) {
					double arcRmv1 = gardeniaKey(i1+1,dag1.get(i1)-1,i2,m2);
					double arcRmv2 = gardeniaKey(dag1.get(i1)+1,j1,m2+1,j2);
					arcRmv = operationMap.get(arcRmv1).getPartialScore() + operationMap.get(arcRmv2).getPartialScore() + this.wArcRmv;
					if(arcRmv<minScore) {
						minScore=arcRmv;
						operation.setOperation("Arc Remove");
						operation.keyLeft=arcRmv1;
						operation.keyRight=arcRmv2;
					}
				}
			
			operation.setPartialScore(minScore);
			operationMap.put(key, operation);
			return key;
		}else if (this.dag1.get(i1) == -1 && this.dag2.get(i2) > -1) {
				
				operation.setIndex(i1, j1, i2, j2);
				//esiste solo a2
			
				//node delete
			
				double deleteNode1 = gardeniaKey(i1+1,j1,i2,j2);
				int deleteNode = operationMap.get(deleteNode1).getPartialScore() + this.wNodeDel;
				if(deleteNode<minScore) {
					minScore=deleteNode;
					operation.setOperation("Delete Node");
					operation.keyLeft = deleteNode1;
					operation.keyRight = -1;
				}
				
				
				
				//arc altering 1
				
				
				//arc remove
				int arcRmv;
				for(int m1 = i1-1; m1<=j1; m1++) {
					double arcRmv1 = gardeniaKey(i1,m1,i2+1,dag2.get(i2)-1);
					double arcRmv2 = gardeniaKey(m1+1,j1,dag2.get(i2)+1,j2);
					arcRmv = operationMap.get(arcRmv1).getPartialScore() + operationMap.get(arcRmv2).getPartialScore() + this.wArcRmv;
					if(arcRmv<minScore) {
						minScore=arcRmv;
						operation.setOperation("Arc Removing");
						operation.keyLeft = arcRmv1;
						operation.keyRight = arcRmv2;
					}
				}	
				
				int arcAlt1;
				for(int m1 = i1; m1<=j1; m1++) {
					double arcAlt11 = gardeniaKey(i1+1,m1,i2+1,dag2.get(i2)-1);
					double arcAlt12 = gardeniaKey(m1+1,j1,dag2.get(i2)+1,j2);
					arcAlt1 = operationMap.get(arcAlt11).getPartialScore() + operationMap.get(arcAlt12).getPartialScore() + this.wArcAlt;
					if(arcAlt1<minScore) {
						minScore=arcAlt1;
						operation.setOperation("Arc Altering 1");
						operation.keyLeft = arcAlt11;
						operation.keyRight = arcAlt12;
					}
				}
				
				
				//arc altering 2
				
				int arcAlt2;
				for(int m1 = i1; m1<=j1; m1++) {
					double arcAlt21 = gardeniaKey(i1,m1-1,i2+1,dag2.get(i2)-1);
					double arcAlt22 = gardeniaKey(m1+1,j1,dag2.get(i2)+1,j2);
					arcAlt2 = operationMap.get(arcAlt21).getPartialScore() + operationMap.get(arcAlt22).getPartialScore() + this.wArcAlt;
					if(arcAlt2<minScore) {
						minScore=arcAlt2;
						operation.setOperation("Arc Altering 2");
						operation.keyLeft = arcAlt21;
						operation.keyRight=arcAlt22;
					}
				}
				
				//arc breaking 
				int arcBrk;
				for(int m1 = i1+1; m1<=j1; m1++) {
					double arcBrk1 = gardeniaKey(i1+1,m1-1,i2+1,dag2.get(i2)-1);
					double arcBrk2 = gardeniaKey(m1+1,j1,dag2.get(i2),j2);
					arcBrk = operationMap.get(arcBrk1).getPartialScore() + operationMap.get(arcBrk2).getPartialScore() + this.wArcBrk;
					if(arcBrk<minScore) {
						minScore=arcBrk;
						operation.setOperation("Arc Breaking");
						operation.keyLeft = arcBrk1;
						operation.keyRight = arcBrk2;
					}
				}	
				
				
			operation.setPartialScore(minScore);
			operationMap.put(key, operation);
			return key;
			}else {
				operation.setIndex(i1, j1, i2, j2);
				double nodeMatch = gardeniaKey(i1+1, j1, i2+1, j2);
				operation.setOperation("Node Match");
				operation.keyLeft = nodeMatch;
				operation.keyRight = (-1);
				operation.setPartialScore(operationMap.get(nodeMatch).getPartialScore());
				operationMap.put(key, operation);
				return key;
			}
}


	
	private void makeArrays() throws Exception {
		for(int i=0; i<this.sequence1.length(); i++) {
			if(this.sequence1.charAt(i)=='.' || this.sequence1.charAt(i)==')')
				this.dag1.add(-1);
			else 
				if (this.sequence1.charAt(i)=='(') {
						int index = takeIndexArc(sequence1, i);
						this.dag1.add(index);
					}
				
		}
		for(int i=0; i<this.sequence2.length(); i++) {
			if(this.sequence2.charAt(i)=='.' || this.sequence2.charAt(i)==')')
				this.dag2.add(-1);
			else 
				if (this.sequence2.charAt(i)=='(') {
						int index = takeIndexArc(sequence2, i);
						this.dag2.add(index);
					}
				
		}
	}
	
	private int takeIndexArc(String sequence, int index) throws Exception {
		int count = 1;
		for(int j = index+1; j < sequence.length(); j++) {
			if(sequence.charAt(j)=='(')
				count++;
			else
				if(sequence.charAt(j)==')') {
					count--;
					if(count==0) {
						return j;
					}
				}
					
		}
		throw new Exception("Wrong sequence. No arc for index " + index);
	}

	
}
