
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class MazeGen {
	private int size;
	private int paths;
	private int recursionChance;
	private long seed;
	private char[][] level;
	private int posX;
	private int posY;

	private ArrayList<Point> mainPath = new ArrayList<Point>();
	private int startPosX;
	private int startPosY;
	private int goalPosX;
	private int goalPosY;
	private int pathLength;
	private int steps;
    private int coins = 0;
    private int maxCoins = 0;
	private int minBlocks;
	private int minPathLength;
	private int coinChance;
	private int trapChance;
	private boolean generateGoal = false;
	private int recurseTimes = 0;
	private boolean recurseflag=false;
	private double xCoord;
	private double nValue = 0;
	private int currentPaths;
	private double c;
	private double d = 0.001;
	private int counter = 0;
	private int exponent = 5;
	Random rng = new Random();

	//level char matrix contains blocks, paths and traps 't' and coins 'c'
	//starting point is denoted by 'S'
	//goal is marked by 'G'



	public MazeGen(int size,int paths,int recursionChance, int minBlocks, int minPathLength, long seed, int trapChance, int coinChance) {
		this.size = size;
		this.currentPaths = paths*(int)(Math.exp(0.0425*size));
		this.recursionChance = recursionChance;
		this.seed = seed;
		this.minBlocks = minBlocks;
		this.minPathLength = minPathLength*(int)(Math.exp(0.045*size));
		this.trapChance = trapChance;
		this.coinChance = coinChance;
		this.xCoord = Math.abs(seed);
		rng.setSeed(seed);
	}

	public MazeGen(long seed) {
		this.size = 48;
		this.currentPaths = 15*(int)(Math.exp(0.0425*size));
		this.recursionChance = 21;
		this.minBlocks = 4;
		this.minPathLength = 20;
		this.seed = seed;
		this.trapChance = 4;
		this.coinChance = 2;
		this.xCoord = Math.abs(seed);
		rng.setSeed(seed);
	}



	public void printLevel() {
		for(int i = 0; i<size; i++) {
			for(int j = 0; j<size; j++) {
				System.out.print(level[j][i]+" ");
			}
			System.out.println("");
		}
	}


	//returns a char matrix, the level
	public char[][] generate() {
		Point p;
		level = new char[size][size];


		//fill with walls
		for(int i = 0; i<size; i++) {
			for(int j = 0; j<size; j++) {
				level[j][i] = '#';
			}
		}
		
		
		//roll start position
		startPosX = rng.nextInt(size-2)+1;
		startPosY = rng.nextInt(size-2)+1;
		level[startPosX][startPosY] = 'S';
		pathLength = (int) (rng.nextInt(10) + 42*Math.log(size)); 		//path length for the path between start and goal


		//generate path from startPosX, startPosY to goalPosX,goalPosY as first generation
		posX = startPosX;
		posY = startPosY;
		generateGoal = true;
		generatePath(startPosX,startPosY);


		//generate first ½ of paths from interval+start position beginning from a point in the main path, adding to main
		
		for(int i = 0; i<currentPaths; i++) {
			do {
				p = new Point(rng.nextInt(size-2)+1,rng.nextInt(size-2)+1);
			}
			while(level[p.getX()][p.getY()]!=' ');
			generatePath(p.getX(),p.getY());
		}
		


		

		//insert the S label if missing
		if(level[startPosX][startPosY]!='S') {
			level[startPosX][startPosY] = 'S';
		}

		//insert the G label if missing
		if(level[goalPosX][goalPosY]!='G') {
			level[goalPosX][goalPosY] = 'G';
		}


		//remove traps from the main path
		for(int i = 0; i<mainPath.size();i++) {
			if(level[mainPath.get(i).getX()][mainPath.get(i).getY()] == 't' || level[mainPath.get(i).getX()][mainPath.get(i).getY()] == '#') {
				level[mainPath.get(i).getX()][mainPath.get(i).getY()] = ' '; 
			}
		}
		
		System.out.println(mainPath);
		System.out.println("array length: "+mainPath.size());
		System.out.println("Starting position "+level[startPosX][startPosY]+" x: "+startPosX+" y: "+startPosY);
		System.out.println("Goal position "+level[goalPosX][goalPosY]+" x: "+goalPosX+" y: "+goalPosY);
		System.out.println("recursed "+recurseTimes+" times");
		System.out.println("stopped in place for "+counter+" times");
		System.out.println("paths:"+currentPaths);
		System.out.println("exponent: "+exponent);
		System.out.println("distance from start to goal: "+Math.hypot(Math.abs(startPosX-goalPosX),Math.abs(startPosY-goalPosY)));
		return level;


	}

	  public char[][] generateTrainingLevel() {
	        coins = 0;
	        level = new char[size][size];

	        for (int i = 0; i < size; i++) {
	            for (int j = 0; j < size; j++) {
	                level[j][i] = ' ';
	            }
	        }
	        for (int j = 0; j < size; j++) {
	            level[j][0] = '#';
	            level[j][size - 1] = '#';
	            level[0][j] = '#';
	            level[size - 1][j] = '#';
	        }

	        for (int i = 0; i < size; i++) {
	            for (int j = 0; j < size; j++) {
	                if (rng.nextInt(100) < trapChance) {
	                    level[rng.nextInt(size - 2) + 1][rng.nextInt(size - 2) + 1] = 't';
	                }

	                if (rng.nextInt(100) < coinChance) {
	                    level[rng.nextInt(size - 2) + 1][rng.nextInt(size - 2) + 1] = 'c';
	                    coins++;
	                }
	            }
	        }
	        startPosX = rng.nextInt(size - 2) + 1;
	        startPosY = rng.nextInt(size - 2) + 1;
	        if (level[startPosX][startPosY] == 'c') {
	            level[startPosX][startPosY] = 'S';
	            coins--;
	        } else {
	            level[startPosX][startPosY] = 'S';
	        }
	        maxCoins = coins;
	        return level;
	}

	private void generatePath(int x, int y) {
		Point p;
		posX = x;
		posY = y;
		int index;
		int direction;
		double dist2 = 0.45;
		
		


		if(recurseflag) {
			System.out.println("recursing with start x:"+posX+" and y:"+posY);
			//level[posX][posY] = '&';
		}


		if(generateGoal == true) {
			steps = pathLength;
			System.out.println("called generatePath(main) with length "+steps);
		}
		else {
			steps = rng.nextInt(25)+minPathLength;
			System.out.println("called generatePath with length "+steps);

		}

		
		//get direction(left,down,right,up,stay still) from ln(x/e) function, check if possible move as long as steps are available
		while(steps>0) {
			
			//get direction from function
			direction = prgF(xCoord,5);
			//increase n by ln{parameter} amount
			increaseNValue(125);
			//change x value input to function
			xCoord = xCoord + prgF(nValue/2048.0,25);//rng.nextInt(25);
			
			switch(direction) {

			case 0: //left
				mover(-1,0);
				break;
			case 1: //down
				mover(0,1);
				break;
			case 2: //right
				mover(1,0);
				break;
			case 3: //up
				mover(0,-1);
				break;
			}

			//pick an empty block, with distance to start > 0.25*size: commit recursion
			if(57*Math.log(rng.nextInt(100))<recursionChance) {
				recurseflag = true;
				recurseTimes++;
				increaseNValue(125);
				
				
				do {
					p = new Point(rng.nextInt(size-2)+1,rng.nextInt(size-2)+1);
				}
				while(level[p.getX()][p.getY()]!=' ');
					
				
				if(generateGoal && (Math.abs(startPosX-p.getX()) > dist2*size || Math.abs(startPosY-p.getY()) > dist2*size)) {
					generatePath(p.getX(),p.getY());
				}
				
				dist2-=0.1;
		
				//p = new Point(posX,posY);
				//mainPath.add(p);
			}


			if(generateGoal && steps==1) {
				//roll random index from main path, try to set the goalPos as far as possible from the start
				double dist = 0.9;
				
				for(index = 0; index<mainPath.size();index++) {
					goalPosX = mainPath.get(index).getX();
					goalPosY = mainPath.get(index).getY();
					if(Math.abs(startPosX-goalPosX) > dist*size || Math.abs(startPosY-goalPosY) > dist*size) {
						break;
					}
					else {
						dist-=0.000001;
					}
				}
				
				
				generateGoal = false;
			}

			
			if(rng.nextInt(100)<trapChance && level[posX][posY]==' ') {
				level[posX][posY] = 't';
			}

			if(rng.nextInt(100)<coinChance && level[posX][posY]==' ') {
				level[posX][posY] = 'c';
			}
			
			

			recurseflag = false;
			steps--;
			
		
			
		}




	}

	private void mover(int dx, int dy) {
		boolean flag = false;
		double b = 0.0;

		for(int i = 0; i<rng.nextInt(5)+minBlocks;i++) {
			if(posX + dx < size-1 && posX + dx > 0 && posY + dy < size-1 && posY + dy > 0) {
				
				c = 0.5*(Math.cos(posX+posY))+0.25;
				b = c*Math.cos(posX+posY+dx+dy)+((0.75-d)-0.7*c);
				
	
				//if the block is blank, enforce stricter rules (function gives lower values) 
				if(level[posX+dx][posY+dy] == ' ') {
					d += 0.1;
					break;
				}
				


				if(dx!=0) {
					flag = true;
					

					if(b<-0.33) {	
						if(level[posX+dx][posY+1] == '#' && level[posX+dx][posY-1] == '#' && level[posX+dx*2][posY] == '#') {
							level[posX+dx][posY+dy] = ' ';
							posX += dx;
							flag = false;
						}

					}
					else if(b>=-0.33&&b<0.33) {
						if(level[posX+dx][posY+1] == '#' && level[posX+dx][posY-1] == '#') {
							level[posX+dx][posY+dy] = ' ';
							posX += dx;
							flag = false;

						}
					}
					else if(b>=0.33&&b<1.0) {
						if(level[posX+dx*2][posY] == '#') {
							level[posX+dx][posY+dy] = ' ';
							posX += dx;
							flag = false;
						}
					}
				}
				else {
					flag = true;


					if(b<-0.33) {	
						if(level[posX+1][posY+dy] == '#' && level[posX-1][posY+dy] == '#' && level[posX][posY+dy*2] == '#') {
							level[posX+dx][posY+dy] = ' ';
							posY += dy;
							flag = false;
						}
					}
					else if(b>=-0.33 && b < 0.33) {
						if(level[posX+1][posY+dy] == '#' && level[posX-1][posY+dy] == '#') {
							level[posX+dx][posY+dy] = ' ';
							posY += dy;
							flag = false;
						}
					}
					else if(b>=0.33 && b<1.0) {
						if(level[posX][posY+dy*2] == '#') {
							level[posX+dx][posY+dy] = ' ';
							posY += dy;
							flag = false;
						}
					}
				}
				
				if(b>=1.0) {
					level[posX+dx][posY+dy] = ' ';
					posX += dx;
					flag = false;
				}
				
				//if generator did not find an appropriate move, loosen the rules (function gives higher values)
				if(flag) {
					d -= 0.0001;
					counter++;
					break;
				}
				
			}
			
			//generate main path Point arraylist for later use
			if(generateGoal) {
				mainPath.add(new Point(posX,posY));
			}
			else {
				//link paths crossing with the main path
				for(int n = 0; n<mainPath.size();n++) {
					if(posX==mainPath.get(n).getX() && posY==mainPath.get(n).getY()) {
						mainPath.add(new Point(posX,posY));
						break;
					}
				}
			}
			
			
		}
	}
	
	private int prgF(double x,int bound) {
		/**decimal function:
		 * generate values from [0,bound)
		 * calculate natural logarithm of x divided by e
		 * result is then multiplied by 10^t, where t is 2*cuberoot of sin(x), the result becomes a
		 * floor(a) becomes b
		 * b is then subtracted from a
		 * multiply by bound
		 * output floor(result)
		 * 
		 */
		return (int) Math.floor(bound*((Math.pow(10,2*(Math.cbrt(Math.abs(Math.sin(x)))))*Math.log(x/Math.E))-Math.floor(Math.pow(10,2*(Math.cbrt(Math.abs(Math.sin(x)))))*Math.log(x/Math.E))));
	}
	
	private void increaseNValue(int x){
		nValue = nValue + 6*Math.log(x);
	}


	public int getPathLength() {
		return pathLength;
	}

	public int getGoalX() {
		return goalPosX;
	}

	public int getGoalY() {
		return goalPosY;
	}
	

	public char[][] getLevel(){
		return level;
	}
	
    public int getStartX() {
        return startPosX;
    }

    public int getStartY() {
        return startPosY;
}
	
	public int getSize() {
		return size;
	}
	
    public int getCoins() {
        return coins;
    }

    public int getMaxcoins() {
        return maxCoins;
    }

    public void decreaseCoins() {
        coins--;
}
	

}

class Point {
	int x;
	int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}
}
