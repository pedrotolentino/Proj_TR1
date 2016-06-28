package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Vector;

import Simulacao.Constantes;
import Simulacao.Protocolo;

public class ProtStopAndWait implements Protocolo{
	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int flagRetorno = -1;
		
		while(flagRetorno != Constantes.ACK){
			out.reset();
			out.writeObject(Constantes.TRANSMISSAO);
			out.reset();
			out.writeObject(pacote);
			System.out.print("Emi -> ");

			try{
				int[] ret = (int[]) in.readObject();
				
				flagRetorno = ret[0];
				if(flagRetorno == Constantes.NACK){
					System.out.print("Pacote com erro... Realizando reenvio ");
				}
				System.out.println(ret[0] == 1?"ACK":"NACK");
			}catch(SocketTimeoutException e){
				System.out.println("Pacote não enviado por timeout... Realizando reenvio ");
			}
		}
	}
}
