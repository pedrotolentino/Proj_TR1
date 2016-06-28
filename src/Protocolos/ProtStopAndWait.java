package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import Simulacao.Protocolo;

public class ProtStopAndWait implements Protocolo{
	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int flagRetorno = -1;
		
		while(flagRetorno != ACK){
			out.writeObject(TRANSMISSAO);
			out.writeObject(pacote);
			System.out.print("Emi -> ");
			int[] ret = (int[]) in.readObject();
			flagRetorno = ret[0];
			if(flagRetorno == NACK){
				System.out.println("Pacote com erro... Realizando reenvio");
			}
			System.out.println(ret[0] == 1?"ACK":"NACK");
		}
	}
	
}
