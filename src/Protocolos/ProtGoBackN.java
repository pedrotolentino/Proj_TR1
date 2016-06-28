package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class ProtGoBackN {

	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		
		out.writeObject(pacote);
		System.out.print("Emi -> ");
		int[] ret = (int[]) in.readObject();
		System.out.println(ret[0] == 1?"ACK":"NACK");
	}
}
