package StopAndWait;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receptor {
	public static void main(String[] args) throws IOException {
		ServerSocket servidor = new ServerSocket(12345);

		System.out.println("Porta 12345 aberta!");

		Socket cliente = servidor.accept();

		System.out.println("Nova conexão com o Emissor " + cliente.getInetAddress().getHostAddress());
		
		InputStream entrada = cliente.getInputStream();
		
		OutputStream envioACK = cliente.getOutputStream();

		int i = 0;
		while(entrada.read() != 0){
			System.out.println(i++);
		}
		
		System.out.println("Fim da conexão com o Emissor");
		entrada.close();
		System.out.println("Fechando conexão do Receptor");
		servidor.close();

	}

}
