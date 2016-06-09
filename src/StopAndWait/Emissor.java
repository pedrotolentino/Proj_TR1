package StopAndWait;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Emissor {
	public static final int QTD_PACOTES = 1000;

	public static void main(String[] args) throws UnknownHostException, IOException {

		Socket cliente = new Socket("localhost", 12345);

		System.out.println("O receptor se conectou ao emissor!");
		
		OutputStream saida = cliente.getOutputStream();
				
		for (int i = 1; i <= QTD_PACOTES; i++){
			saida.write(1);	
		}
		saida.write(0);
		saida.close();
	}
}
