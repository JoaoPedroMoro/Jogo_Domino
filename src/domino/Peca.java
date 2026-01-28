package domino;

import org.bson.Document;

public class Peca {
	
	public final int ladoEsquerdo;
	public final int ladoDireito;
	
	public Peca(int ladoEsquerdo, int ladoDireito) {
		
		this.ladoEsquerdo = ladoEsquerdo;
		this.ladoDireito = ladoDireito;
		
	}
	
	public boolean combinaCom(int numero) {
		
		boolean esquerdaCombina = (ladoEsquerdo == numero);
		boolean direitaCombina = (ladoDireito == numero);
		
		return esquerdaCombina || direitaCombina;
		
	}
	
	public Peca invertida() {
		return new Peca(this.ladoDireito, this.ladoEsquerdo);
	}
	
	@Override
	public String toString() {
		
		String inicio = "[";
		
		String ladoE = String.valueOf(ladoEsquerdo);
		
		String separa = "|";
		
		String ladoD = String.valueOf(ladoDireito);
		
		String fim = "]";
		
		return inicio + ladoE + separa + ladoD + fim;
		
	}
	
	// Serialização para o MongoDB
	public Document toDocument() {
		
		Document doc = new Document("e", ladoEsquerdo);
		
		doc.append("d", ladoDireito);
		
		return doc;
		
	}
	
	public static Peca fromDocument(Document doc) {
		
		int valorEsquerdo = doc.getInteger("e");
		
		int valorDireito = doc.getInteger("d");
		
		return new Peca(valorEsquerdo, valorDireito);
		
	}
	
	public boolean duplo() {return ladoEsquerdo == ladoDireito;}
	
	public int soma() {return ladoEsquerdo + ladoDireito;}
			

}
