package modulo;

public class Album extends Coleccion{
	private String autor;
	private String propietario; //Quién tiene los derechos, o quién es la disquera
	private String portada; // Path de la imagen
	private int anio;

	
	public Album() {
		// TODO Auto-generated constructor stub
	}


	public String getAutor() {
		return autor;
	}


	public void setAutor(String autor) {
		this.autor = autor;
	}


	public String getPropietario() {
		return propietario;
	}


	public void setPropietario(String propietario) {
		this.propietario = propietario;
	}


	public String getPortada() {
		return portada;
	}


	public void setPortada(String portada) {
		this.portada = portada;
	}


	public int getAnio() {
		return anio;
	}


	public void setAnio(int anio) {
		this.anio = anio;
	}

}
