//
//  Created by manolo on Mar 11, 2014.
//  Copyright (c) 2014 manolo. All rights reserved.
//

package flappysub;

/**
 *
 * @author manolo
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import javax.swing.JFrame;


/**
 *
 * @author manolo
 */
public class FlappySub extends JFrame implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;
//	Se declaran las variables.
	private Image dbImage;				// Imagen a proyectar
	private Image background;			// Imagen de fondo
	private Graphics dbg;				// Objeto grafico
	private Base sub;					// Objeto sub
	private int gravity;
	private int push;
	private LinkedList<Mine> mines;		// Objeto minas
	private int nMines;
	private int minesV;
	private int minesGap;				// Separacion entre minas arriba/abajo
	private SoundClip choque;			// Sonido de choque con minas
	private long tiempoActual;			// el tiempo actual que esta corriendo el jar
	private long tiempoInicial;			// el tiempo inicial
	private boolean sound;				// si el sonido esta activado
	private int score;					// el puntaje
	private int level;
	private boolean changeLvl;
	
//	checar si se necesitan
	private Base pausa;					// Objeto que pinta el pausa
	private Base instruc;				// Objeto que pinta las instrucciones
	private Base gameo;					// Objeto que pinta el Game over
	private Base gamew;					// Objeto que pinta el Game over win
	private int highestscore;           // El puntuaje mas alto
	private int estado;					// el estado actual del juego (0 = corriendo, 1 = pausa, 2 = informacion,3 = creditos)
	private boolean cargar;				// variable que carga el archivo

	
	public FlappySub() {
		init();
		start();
	}
	
	
	public void init() {
//		Inicializacion de variables
		setSize(1200,720);
		
		score = 0;
		level = 0;
		changeLvl = true;
		estado = 2;
		
		sound = false;
		cargar = false;
		
		choque = new SoundClip("resources/dano1.wav");	// choque con minas
		choque.setLooping(false);
		
//		background = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/BackGroundBB.jpg"));
		
//		Se cargan las imágenes para la animación
		Image sub0 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/submarine0.png"));
		Image sub1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/submarine1.png"));
		Image sub2 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/submarine2.png"));
		Image sub3 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/submarine3.png"));
		Image sub4 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/submarine4.png"));
		
		Image iT = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/mineT.png"));
		Image iB = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/mineB.png"));
		
		Image instru= Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/instrucciones.png"));

//		Image pausa1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/pausa.png"));
//		Image instruc1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/instrucciones.png"));
//		Image gameo1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/gameover.png"));
//		Image gameo2 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/gameover2.png"));
                
//		Se crea la animación
		Animacion animS = new Animacion(), animT = new Animacion(), animB = new Animacion();
		Animacion animI = new Animacion(); 
		int subTime = 100, mineTime = 0;
		animS.sumaCuadro(sub0, subTime);
		animS.sumaCuadro(sub1, subTime);
		animS.sumaCuadro(sub2, subTime);
		animS.sumaCuadro(sub3, subTime);
		animS.sumaCuadro(sub4, subTime);
		
		animT.sumaCuadro(iT, mineTime);
		animB.sumaCuadro(iB, mineTime);
		
		animI.sumaCuadro(instru,0);
        
		
		gravity = 4;
		push = 0;
		sub = new Base(563,400,1,animS);
		nMines = 4;
		minesV = -3;
		minesGap = 128;
		mines = new LinkedList();
		for (int i=0; i<nMines; i++) {
			Base top = new Base(0,0,0,animT);
			Base bottom = new Base(0,0,0,animB);
			mines.add(new Mine(0, 0, minesGap, top, bottom));
			int r = (int)(Math.random()*300)+150;
			mines.get(i).setY(r);
			mines.get(i).setX(1250+i*300);
		}
		
		
		instruc = new Base(0,20,0,animI);
		
//		gameo = new Base(0,20,0,animG);
//		gamew = new Base(0,20,0,animG2);
//		pausa = new Base(0,20,0,animP);
        
		setResizable(false);
		setBackground(new Color(43, 48, 51));
		addKeyListener(this);
	}
	
	/** 
	 * Metodo <I>start</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
	public void start () {
//		Declaras un hilo
		Thread th = new Thread(this);
//		Empieza el hilo
		th.start();
	}
		
	/** 
	 * Metodo <I>run</I> sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, es un ciclo indefinido donde se incrementa
     * la posicion en x o y dependiendo de la direccion, finalmente 
     * se repinta el <code>Applet</code> y luego manda a dormir el hilo.
     * 
     */
	@Override
	@SuppressWarnings("SleepWhileInLoop")
	public void run () {
		while (true) {
			actualiza();
			checaColision();
//			Se actualiza el <code>Applet</code> repintando el contenido.
			repaint();
			try	{
//				El thread se duerme.
				Thread.sleep (20);
			}
			catch (InterruptedException ex)	{
				System.out.println("Error en " + ex.toString());
			}
		}
	}
	
	/**
	 * Metodo usado para actualizar la posicion de objetos
	 * 
	 */
	public void actualiza() {
		if (estado == 0) {
//			Determina el tiempo que ha transcurrido desde que el Applet inicio su ejecución
			long tiempoTranscurrido = System.currentTimeMillis() - tiempoActual;
            
//			Guarda el tiempo actual
			tiempoActual += tiempoTranscurrido;

//			Actualiza la posicion y la animación en base al tiempo transcurrido
			int y = gravity+push;
			if (push<0) {
				push++;
			}
			sub.addY(y);
			sub.actualiza(tiempoActual);
			
			for (int i=0; i<nMines; i++) {
				Mine mine = mines.get(i);
				mine.addX(minesV);
				if (mine.getX() == 558 || mine.getX() == 559 || mine.getX() == 560) {
					score++;
					changeLvl = false;
				} else if (mine.getX() < -34) {
					mine.setX(1250);
					mine.setGap(128-level*8);
					minesV = -3-level;
					int r = (int)(Math.random()*300)+150;
					mine.setY(r);
				}
			}
		}
		if (sub.getLives() == 0) {
			estado = 3;
			try {
				grabaArchivo();
			} catch(IOException e) {
				System.out.println("Error en guardar");
			}
		}
		
		if (!changeLvl && (score%5)==0) {
			level++;
			changeLvl = true;
		}
		
		if(cargar){
			cargar = false;
			try {
			leeArchivo();
			} catch(IOException e) {
				System.out.println("Error en cargar");
			}
		}
	}
	
	/**
	 * Metodo usado para checar las colisiones del objeto elefante y asteroid
	 * con las orillas del <code>Applet</code>.
	 */
	public void checaColision() {
		if (sub.getY()<0 || sub.getY()>668) {
			sub.addLives(-1);
			if (sound) {
				choque.play();
			}
		}
		
		
		for (int i=0; i<nMines; i++) {
			if (mines.get(i).intersecta(sub)) {
				sub.addLives(-1);
				if (sound) {
					choque.play();
				}
			}
		}
	}
	
	/**
	 * Metodo <I>keyPressed</I> sobrescrito de la interface <code>KeyListener</code>.<P>
	 * En este metodo maneja el evento que se genera al presionar cualquier la tecla.
	 * @param e es el <code>evento</code> generado al presionar las teclas.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_S) {			//Presiono tecla s para quitar sonido
			sound = !sound;
		} else if (e.getKeyCode() == KeyEvent.VK_I) {
//			Mostrar/Quitar las instrucciones del juego
			if (estado == 2) {
				estado = 0;
			} else {
				estado = 2;
//				cargar=true;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_P) {	//Presiono tecla P para parar el juego en ejecuccion
			if (estado == 1) {
				estado = 0;
			} else {
				estado = 1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			push -= 10;
		} else if (e.getKeyCode()== KeyEvent.VK_R) {
			if (estado!=0) {
				sub.setX(563);
				sub.setY(400);
				sub.setLives(1);
				int r = (int)(Math.random()*300)+150;
				for (int i=0; i<nMines; i++) {
					mines.get(i).setY(r);
					mines.get(i).setX(1250+i*300);
				}
				estado = 0;
			}
		}
                
    }
	@Override
	public void keyReleased(KeyEvent e){}
	@Override
	public void keyTyped(KeyEvent e){}
	
	public void leeArchivo() throws IOException {
//		Lectura del archivo el cual tiene las variables del juego guardado
		BufferedReader fileIn;
		try {
			fileIn = new BufferedReader(new FileReader("Guardado.txt"));
		} catch (FileNotFoundException e){
			File puntos = new File("Guardado.txt");
			PrintWriter fileOut = new PrintWriter(puntos);
			fileOut.println("100,demo");
			fileOut.close();
			fileIn = new BufferedReader(new FileReader("Guardado.txt"));
		}
		String dato = fileIn.readLine();
		tiempoActual = (Long.parseLong(dato));
		dato = fileIn.readLine();
		estado = Integer.parseInt(dato);
		dato = fileIn.readLine();
		highestscore = Integer.parseInt(dato);

		fileIn.close();
	}
	
	public void grabaArchivo() throws IOException {
//		Grabar las variables necesarias para reiniciar el juego de donde se quedo el usuario en un txt llamado Guardado
		PrintWriter fileOut = new PrintWriter(new FileWriter("Guardado"));
		fileOut.println(String.valueOf(tiempoActual));
		fileOut.println(String.valueOf(estado));
		if (score >= highestscore) {
			fileOut.println(String.valueOf(score));    
		} else {
			fileOut.println(String.valueOf(highestscore));
		}
		
		fileOut.close();
	}
	
	@Override
	public void paint(Graphics g) {
//		Inicializan el DoubleBuffer
		dbImage = createImage (this.getSize().width, this.getSize().height);
		dbg = dbImage.getGraphics ();

//		Actualiza la imagen de fondo.
		dbg.setColor(getBackground ());
		dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

//		Actualiza el Foreground.
		dbg.setColor(getForeground());
		paint1(dbg);

//		Dibuja la imagen actualizada
		g.drawImage (dbImage, 0, 0, this);
	}
	
	/**
	 * Metodo <I>paint1</I> sobrescrito de la clase <code>Applet</code>,
	 * heredado de la clase Container.<P>
	 * En este metodo se dibuja la imagen con la posicion actualizada,
	 * ademas que cuando la imagen es cargada te despliega una advertencia.
	 * @param g es el <code>objeto grafico</code> usado para dibujar.
	 */
	public void paint1(Graphics g) {
		g.setFont(new Font("Helvetica", Font.PLAIN, 20));	// plain font size 20
		g.setColor(Color.white);							// black font
		
		if (sub != null) {
//			Dibuja la imagen en la posicion actualizada
			g.drawImage(background, 0, 20, this);
			for (int i=0; i<nMines; i++) {
				Mine m = mines.get(i);
				g.drawImage(m.getTop().getImage(), m.getX(), m.getTop().getY(), this);
				g.drawImage(m.getBottom().getImage(), m.getX(), m.getBottom().getY(), this);
			}
			
			if (estado == 0) {
//			Dibuja el estado corriendo del juego
				g.drawImage(sub.getImage(), sub.getX(), sub.getY(), this);
				
//				g.drawString("Vidas: " + String.valueOf(hank.getLives()), 1000, 75);	// draw score at (1000,25)
//			} else if (estado == 1) {
//				Dibuja el estado de pausa en el jframe
				
//				g.drawImage(pausa.getImage(),pausa.getX(),pausa.getY(),this);
				
//				g.drawString("PAUSA", getWidth()/2 - 100, getHeight()/2);
			} else if (estado == 2) {
//				Dibuja el estado de informacion para el usuario en el jframe
				g.drawImage(instruc.getImage(),instruc.getX(),instruc.getY(),this);
			}
		} else {
//			Da un mensaje mientras se carga el dibujo	
			g.drawString("No se cargo la imagen..", 20, 20);
		}
	}       
		 
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		FlappySub examen2 = new FlappySub();
		examen2.setVisible(true);
		examen2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}