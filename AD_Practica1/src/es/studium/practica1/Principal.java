package es.studium.practica1;

public class Principal
{
	public static void main(String[] args)
	{
		Modelo modelo = new Modelo();
		Vista vista = new Vista();
		Controlador controlador = new Controlador(modelo, vista);
		controlador.ejecutar();
	}
}
