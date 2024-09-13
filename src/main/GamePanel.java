package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener{
	
	private enum GameState {
        RUNNING, PAUSED, CONFIGURE
    }
	
	static final int SCREEN_WIDTH = 1600;

	static final int SCREEN_HEIGHT = 1000;

	static final int UNIT_SIZE = 10;

	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);

	static int delay = 200;
	
	static int cacheDelay;
	
	static double FPS = (1.0/delay)*1000;
	
	GameState gameState = GameState.RUNNING;
	
	int[][] matrix = new int[SCREEN_WIDTH/UNIT_SIZE][SCREEN_HEIGHT/UNIT_SIZE];
	
	int generation = 0;
	
	boolean showGrid = false;
	
	static Timer timer;
	
	Random random;
	
	
	GamePanel(){
		
		random = new Random();

		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));

		this.setBackground(Color.black);

		this.setFocusable(true);
		
		this.addKeyListener(new MyKeyAdapter());
		
		this.addMouseListener(new MyMouseAdapter());
		
		/*
		this.matrix[2][3] = 1;
		this.matrix[3][4] = 1;
		this.matrix[3][5] = 1;
		this.matrix[2][5] = 1;
		this.matrix[1][5] = 1;

		*/
		
		startGame();

	}
	
	public void startGame(){
		System.out.println(matrix.length);

		System.out.println(matrix[1].length);
		
		initialConfig();
		
		timer = new Timer(delay,this);

		timer.start();
		
		
	}
	
	private void initialConfig() {
		
		int restrict = 10;
		
		for(int i = restrict; i<matrix.length-restrict;i++) {
			
			for(int j = restrict; j < matrix[1].length-restrict;j++) {
				
				matrix[i][j] = random.nextInt(2);
			};
		};
		
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		draw(g);

	}

	private void draw(Graphics g) {
		
		
		
		// Affichage des cellules
		
		for(int i=0;i<matrix.length;i++) {
			
			for(int j=0;j<matrix[1].length;j++) {
				
				if(matrix[i][j] == 1) {
					g.setColor(Color.white);
					//g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
					g.fillRect(i*UNIT_SIZE, j*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
				}else {
					g.setColor(Color.black);
					g.fillRect(i*UNIT_SIZE, j*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
				};
			}
		}
		
		// Affichage des lignes du quadrillage.
			if(showGrid) {
				for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
					g.setColor(Color.white);
					//g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
					g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);

				}
				for(int i=0;i<SCREEN_WIDTH/UNIT_SIZE;i++) {
					g.setColor(Color.white);
					g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);	
				}
			};
				
		// Affichage des générations et infos:
				
				g.setColor(Color.red);

				g.setFont(new Font("Ink Free",Font.BOLD, 20));

				//FontMetrics metrics = getFontMetrics(g.getFont());

				g.drawString("Génération : "+generation, 10, g.getFont().getSize());
				g.drawString("FPS : "+(int) FPS, 10, g.getFont().getSize()*2);
				g.drawString("Délai : "+(delay), 10,g.getFont().getSize()*3);
				
				g.setColor(Color.green);
				g.drawString("État : "+gameState, 10,g.getFont().getSize()*4);
	}
	
	
	public void calcNextGeneration() {
		
		int[][] matrixClone = new int[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
		    matrixClone[i] = matrix[i].clone();
		}

		
		for(int i = 1;i < matrix.length-1;i++) {//Pas la première colonne, ni la dernière
			
			for(int j = 1; j < matrix[1].length-1;j++) {//Pas la première rangée, ni la dernière
				
				int nbrAliveCells = 0;
				
				nbrAliveCells = matrix[i-1][j-1] + matrix[i][j-1] + matrix[i+1][j-1] 
						+ matrix[i-1][j] + matrix[i+1][j] 
						+ matrix[i-1][j+1] + matrix[i][j+1] + matrix[i+1][j+1];
				
				if(matrixClone[i][j] == 0 && nbrAliveCells == 3) { // Si cellule morte et exactement 3 voisines, alors devient vivante
					matrixClone[i][j] = 1;
				}else if(matrixClone[i][j] == 1 && (nbrAliveCells == 2 || nbrAliveCells == 3)) { //Si vivante et avec 2 ou 3 voisines, alors reste vivante
					matrixClone[i][j] = 1;
				}else {// Les autres cas, la cellules meurt ou reste morte.
					matrixClone[i][j] = 0;
				};
				
				
			};
			
		};
		this.matrix = matrixClone;
	};
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(gameState.equals(GameState.RUNNING)) {
			calcNextGeneration();
			generation++;

			repaint();
		}else if(gameState.equals(GameState.PAUSED)) {
			repaint();
		}else if(gameState.equals(GameState.CONFIGURE)) {
			repaint();
		};

		
	};
	
	public class MyKeyAdapter extends KeyAdapter{

		@Override

		public void keyPressed(KeyEvent e) {
			
			switch(e.getKeyCode()) {
			
			case KeyEvent.VK_SPACE:
				
				if(gameState.equals(GameState.RUNNING)) {
					gameState = GameState.PAUSED;
					GamePanel.setCacheDelay(delay);
					GamePanel.setDelay(10000);
				}else {
					gameState = GameState.RUNNING;
					GamePanel.setDelay(cacheDelay);
				};
				break;
			
			case KeyEvent.VK_RIGHT:
				if(gameState.equals(GameState.RUNNING)) {
					if(GamePanel.delay > 110) {
						GamePanel.setDelay(GamePanel.delay - 100);
						GamePanel.timer.setDelay(GamePanel.delay);
						GamePanel.setFPS(Math.round((1.0/GamePanel.delay)*1000));
					}else if(GamePanel.delay < 110 && GamePanel.delay > 20) {
						GamePanel.setDelay(GamePanel.delay - 10);
						GamePanel.timer.setDelay(GamePanel.delay);
						GamePanel.setFPS(Math.round((1.0/GamePanel.delay)*1000));
					}
					else if(GamePanel.delay <= 20 && GamePanel.delay > 10){
						GamePanel.setDelay(GamePanel.delay - 1);
						GamePanel.timer.setDelay(GamePanel.delay);
						GamePanel.setFPS(Math.round((1.0/GamePanel.delay)*1000));
					}else {
						System.out.println("Déjà assez vite ...");
					};
				};
				break;
				
			case KeyEvent.VK_LEFT:
				if(gameState.equals(GameState.RUNNING)) {	
					if(GamePanel.delay < 100) {
						GamePanel.setDelay(100);
						GamePanel.setFPS(Math.round((1.0/GamePanel.delay)*1000));
					}else if(GamePanel.delay < 2000) {
						GamePanel.setDelay(GamePanel.delay + 100);
						GamePanel.timer.setDelay(GamePanel.delay);
						GamePanel.setFPS(Math.round((1.0/GamePanel.delay)*1000));
					}else {
						System.out.println("Déjà assez lent ...");
					};
				};
				break;
				
			case KeyEvent.VK_S:
				
					showGrid = !showGrid;
				
				break;
			case KeyEvent.VK_C:
				if(gameState != GameState.CONFIGURE) {
					GamePanel.setDelay(50);
					gameState = GameState.CONFIGURE;
					showGrid = true;
				}else {
					GamePanel.setDelay(200);
					gameState = GameState.PAUSED;
					showGrid = false;
				};
			
			break;
			};
			
		};
	}

	public static void setDelay(int cdelay) {
		delay = cdelay;
	}

	public static void setFPS(double fPS) {
		FPS = fPS;
	}

	public static void setCacheDelay(int cacheDelay) {
		GamePanel.cacheDelay = cacheDelay;
	}

	private class MyMouseAdapter extends MouseAdapter{
		
		public void mouseClicked(MouseEvent e) {
			if(gameState == GameState.CONFIGURE) {
				int x = e.getX();
				int y = e.getY()-UNIT_SIZE/2;
				int coordX = (int) Math.floor(x/UNIT_SIZE);
				int coordY = (int) Math.floor(y/UNIT_SIZE);
				
				matrix[coordX][coordY] = 1 - matrix[coordX][coordY];
				repaint();
			};
	};
	
		
	}

	
	
}
