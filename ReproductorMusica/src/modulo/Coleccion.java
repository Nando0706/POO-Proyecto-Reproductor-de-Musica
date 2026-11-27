package modulo;
import java.util.List;
import java.util.Comparator;

public abstract class Coleccion {
	protected String nombre;
	protected List<Cancion> canciones;
	
	public Coleccion() {
	}
	
	public int getNumeroCanciones() {
		return canciones.size();
	}
	
	public double getDuracion(String nombreCancion) {
		for(int i = 0; i < canciones.size(); i++) {
			if(nombreCancion == canciones.get(i).getNombre()) {
				return canciones.get(i).getDuracion();
			} else {
				System.out.println("No se encontraron canciones");
			}
		}
		return 0;
	}
	
	public void ocultarCancion(Cancion cancion) {
		
		
	}
	
	public List<Cancion> buscarCancion(String nombreCancion) {
		List<Cancion> correspondencia = null;
		nombreCancion.toLowerCase()
					 .replaceAll(" ", "");
		String nombreSinFormato; 
		
		for(int i = 0; i < canciones.size(); i++) {
			nombreSinFormato = canciones.get(i).getNombre();
			nombreSinFormato.toLowerCase()
							.replaceAll(" ", "");
			if(nombreCancion == nombreSinFormato) {
				correspondencia.add(canciones.get(i));
			} else {
				System.out.println("No se encontraron canciones");
				return null;
			}
		}
		return correspondencia;
	}
	
	public List<Cancion> ordenarCanciones(String criterio){
		
		switch(criterio) {
			case("Nombre"):
				canciones.sort(Comparator.comparing(Cancion::getNombre, String.CASE_INSENSITIVE_ORDER));
				break;
			case("")
		}
	}
	
	
	
	
	
	

}
