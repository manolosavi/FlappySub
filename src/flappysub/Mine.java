//
//  Created by manolo on Mar 11, 2014.
//  Copyright (c) 2014 manolo. All rights reserved.
//

package flappysub;

/**
 *
 * @author manolo
 */

public class Mine {
	int posX, posY, gap;
	Base top, bottom;
	
	/**
	 * Metodo constructor usado para crear el objeto
	 * @param x es la <code>posicion en x</code> del objeto.
	 * @param y es la <code>posicion en y</code> del objeto.
	 * @param g es el <code>espacio</code> entre objetos.
	 * @param a es el <code>objeto</code> de arriba.
	 * @param b es la <code>objeto</code> de abajo.
	 */
	public Mine(int x, int y, int g, Base a, Base b) {
		posX = x;
		posY = y;
		gap = g;
		top = a;
		bottom = b;
	}
	
	/**
	 *	Actualiza la imagen (cuadro) actual de la animación, si es necesario.
	 * @param t tiempo transcurrido
	*/
	public synchronized void actualiza(long t) {
		top.actualiza(t);
		bottom.actualiza(t);
	}
	
	/**
	 * Metodo de acceso que regresa el objeto de arriba
	 * @return el <code>objeto</code> de arriba.
	 */
	public Base getTop() {
		return top;
	}
	
	/**
	 * Metodo de acceso que regresa el objeto de abajo
	 * @return el <code>objeto</code> de abajo.
	 */
	public Base getBottom() {
		return bottom;
	}
	
	/**
	 * Metodo modificador usado para cambiar la posicion en x del objeto 
	 * @param x es la <code>posicion en x</code> del objeto.
	 */
	public void setX(int x) {
		posX = x;
		top.setX(posX);
		bottom.setX(posX);
	}
	
	/**
	 * Metodo modificador usado para cambiar la posicion en x del objeto 
	 * @param x es la <code>cantidad en x</code> a aumentar.
	 */
	public void addX(int x) {
		posX += x;
		top.setX(posX);
		bottom.setX(posX);
	}
	
	/**
	 * Metodo modificador usado para cambiar la posicion en y del objeto 
	 * @param y es la <code>posicion en y</code> del objeto.
	 */
	public void setY(int y) {
		posY = y;
		top.setY(posY-700);
		bottom.setY(posY+gap);
	}
	
	/**
	 * Metodo de acceso que regresa la posicion en x del objeto 
	 * @return posX es la <code>posicion en x</code> del objeto.
	 */
	public int getX() {
		return posX;
	}
	
	/**
	 * Metodo de acceso que regresa la posicion en y del objeto 
	 * @return posY es la <code>posicion en y</code> del objeto.
	 */
	public int getY() {
		return posY;
	}
	
	/**
	 * Checa si el objeto <code>Base</code> intersecta a otro <code>Base</code>
	 * @param obj objecto con el que se checa si se intersecta
	 * @return un valor boleano <code>true</code> si lo intersecta <code>false</code>
	 * en caso contrario
	 */
	public boolean intersecta(Base obj) {
		return (top.intersecta(obj) || bottom.intersecta(obj));
	}
}