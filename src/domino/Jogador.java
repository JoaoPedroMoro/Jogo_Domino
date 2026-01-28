package domino;

import java.util.ArrayList;
import java.util.List;

public class Jogador {
	
	public final String nome;
	
	public final List<Peca> pecas = new ArrayList<>();
	
	public boolean passou = false;
	
	public Jogador(String nome) { this.nome = nome;}
	
	public boolean temJogadaValida(int pontaEsquerda, int pontaDireita) {
		
		for (Peca p : pecas) {
			
			if (p.combinaCom(pontaEsquerda) || p.combinaCom(pontaDireita)) return true;
			
		}
		
		return false;
		
	}


}
