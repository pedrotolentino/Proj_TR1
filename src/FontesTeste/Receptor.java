package FontesTeste;
import java.io.*;
import java.net.*;

public class Receptor {
	ServerSocket receptor;
	Socket conn = null;
	ObjectOutputStream saida;
	ObjectInputStream entrada;
	String pacote, ack, data = "";
	int i = 0, seq = 0;

	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			receptor = new ServerSocket(1234, 10);
			System.out.println("Esperando por conexão...");
			conn = receptor.accept();
			seq = 0;
			System.out.println("Conexão estabelecida!!!");
			saida = new ObjectOutputStream(conn.getOutputStream());
			saida.flush();
			entrada = new ObjectInputStream(conn.getInputStream());
			saida.writeObject("Conectado    .");
			do {
				try {
					pacote = (String) entrada.readObject();
					if (Integer.valueOf(pacote.substring(0, 1)) == seq) {
						data += pacote.substring(1);
						seq = (seq == 0) ? 1 : 0;
						System.out.println("\n\nReceptor ->" + pacote);
					} else {
						System.out.println("\n\nReceptor ->" + pacote + "   Dado repetido");
					}
					if (i < 3) {
						saida.writeObject(String.valueOf(seq));
						i++;
					} else {
						saida.writeObject(String.valueOf((seq + 1) % 2));
						i = 0;
					}
				} catch (Exception e) {
				}
			} while (!pacote.equals("end"));
			System.out.println("Dados " + data+" recebidos com sucesso");
			saida.writeObject("Conexão encerrada.");
		} catch (Exception e) {
		} finally {
			try {
				entrada.close();
				saida.close();
				receptor.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String args[]) {
		Receptor s = new Receptor();
		s.run();
	}
}