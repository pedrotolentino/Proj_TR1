package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import Simulacao.Protocolo;

public class ProtGoBackN implements Protocolo{

	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int flagRetorno = -1;
		int[] ret = null;
		while(flagRetorno != ACK){
			out.writeObject(TRANSMISSAO);
			out.writeObject(pacote);
			System.out.print("Emi -> ");
			try{
				ret	 = (int[]) in.readObject();
				
			}catch(InterruptedByTimeoutException e){}
			
			for(int i = 0; i < ret.length; i++){
				if(ret[i] == NACK){
					System.out.println("NACK do pacote "+i+1+"... Realizando reenvio");
					flagRetorno = NACK;
					break;
				}else{
					System.out.println("ACK do pacote "+i+1);
					flagRetorno = ACK;
				}
			}
		}
	}
}
