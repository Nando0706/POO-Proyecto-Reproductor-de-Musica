package modulo;
import java.util.List;

public class Cancion {
	private String nombre;
	private List<String> autores;
	private double duracion;
	private Album album;
	private int anio;
	private String lyrics;
	private String path; //Path del archivo de la canción.

	
	public Cancion() {
		// TODO Auto-generated constructor stub
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public List<String> getAutores() {
		return autores;
	}


	public void setAutores(List<String> autores) {
		this.autores = autores;
	}


	public double getDuracion() {
		return duracion;
	}


	public void setDuracion(double duracion) {
		this.duracion = duracion;
	}


	public Album getAlbum() {
		return album;
	}


	public void setAlbum(Album album) {
		this.album = album;
	}


	public int getAnio() {
		return anio;
	}


	public void setAnio(int anio) {
		this.anio = anio;
	}


	public String getLyrics() {
		return lyrics;
	}


	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	
	

}
