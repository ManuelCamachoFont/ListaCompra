package es.studium.practica1;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Principal
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (Exception e)
				{
					System.err.println(e.getMessage());
					
				}
				Modelo modelo = new Modelo();
				Vista vista = new Vista();
				new Controlador(modelo, vista);
				vista.setVisible(true);
			}
		});
	}
}
