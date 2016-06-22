package FontesTeste;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Canal implements Runnable{
	ServerSocket canal;
	
	public Canal(){
		try {
			this.canal = new ServerSocket(1234);
			System.out.println("Canal instanciado com sucesso!");
			
		} catch (IOException e) {
			System.out.println("ERRO ao instanciar o Canal");
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Aguardando conexão dos clientes...");
		while(true){
			try {
				Socket maquina = canal.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
