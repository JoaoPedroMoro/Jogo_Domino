package domino;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Partida {
	
	public ObjectId id;
	public List<Peca> mesa = new ArrayList<>();
	public List<Peca> estoque = new ArrayList<>();
	public Jogador jogador1;
	public Jogador jogador2;
	public int turnoAtual = 1;
	public String status = "em andamento";
	public String vencedor = null;
	
	public Partida(String nomeJogador1, String nomeJogador2) {
		
		jogador1 = new Jogador(nomeJogador1);
		
		jogador2 = new Jogador(nomeJogador2);
		
		iniciarJogo();
		
	}
	
	private Partida() {}
	
	private void iniciarJogo() {
		
		List<Peca> todas = new ArrayList<>();
		
		for (int i = 0; i <= 6 ; i++) {
			
			for (int j = 0; j <= 6; j++) {
				
				todas.add(new Peca(i, j));
				
			}
			
		}
		
		Collections.shuffle(todas);
		
		for (int i = 0; i < 7; i++) {
			
			Peca pecaDistribuida = todas.remove(0);
			
			jogador1.pecas.add(pecaDistribuida);
			
		}
		
		for (int i = 0; i < 7; i++) {
			
			Peca pecaDistribuidaIA = todas.remove(0);
			
			jogador2.pecas.add(pecaDistribuidaIA);
			
		}
		
		mesa.add(todas.remove(0));
		
		estoque.addAll(todas);
		
	}
	
	public int pontaEsquerda() {return mesa.get(0).ladoEsquerdo;}
	
	public int pontaDireita() {
	// mesa.size() retorna o número total de peças na lista 'mesa'.
    // mesa.size() - 1 calcula o índice da última peça (direita).
    // .ladoDireito acessa o valor inteiro do lado direito dessa peça.
		return mesa.get(mesa.size() - 1).ladoDireito;
	}
	
	private boolean jogadorTemJogadaValida(Jogador j) {
		
		int esq = pontaEsquerda();
		
		int dir = pontaDireita();
		
		return j.pecas.stream().anyMatch(p -> p.combinaCom(esq) || p.combinaCom(dir));
		
	}
	
	public boolean comprarPecaHumano() {
		
		if (estoque.isEmpty()) return false;
		
		jogador1.pecas.add(estoque.remove(0));
		
		return true;
		
	}
	
	public boolean jogar(int jogador, int indice, boolean naEsquerda) {
		
		Jogador j = (jogador == 1) ? jogador1 : jogador2;
		
		if (indice < 0 || indice >= j.pecas.size()) return false;
		
		Peca p = j.pecas.get(indice);
		
		int ponta = naEsquerda ? pontaEsquerda() : pontaDireita();
		
		if (!p.combinaCom(ponta)) return false;
		
		boolean inverter =
				(naEsquerda && p.ladoDireito != ponta) ||
				(!naEsquerda && p.ladoEsquerdo != ponta);
		
		if (inverter) p = p.invertida();
		
		if (naEsquerda) mesa.add(0, p);
		else mesa.add(p);
		
		j.pecas.remove(indice);
		
		j.passou = false;
		
		turnoAtual = (turnoAtual == 1 ? 2 : 1);
		
		checarFimDeJogo();
		
		return true;
		
	}
	
	public void jogadaIA() {
		
		if (!status.equals("em andamento") || turnoAtual != 2) return;
		
		Jogador ia = jogador2;
		
		while (!jogadorTemJogadaValida(ia) && !estoque.isEmpty()) ia.pecas.add(estoque.remove(0));
		
		if (!jogadorTemJogadaValida(ia)) {
			
			passarVez(2);
			return;
			
		}
		
		int esq = pontaEsquerda();
		int dir = pontaDireita();
		
		for (int i = 0; i < ia.pecas.size(); i++) {
			
			Peca p = ia.pecas.get(i);
			
			if (p.combinaCom(esq)) {
				
				jogar(2, i, true);
				
				return;
				
			}
			
			if (p.combinaCom(dir)) {
				
				jogar(2, i, false);
				
				return;
				
			}
			
		}
		
	}
	
	public void passarVez(int jogador) {
		
		if (!estoque.isEmpty()) return;
		
		(jogador == 1 ? jogador1 : jogador2).passou = true;
		
		turnoAtual = (turnoAtual == 1 ? 2 : 1);
		
		checarFimDeJogo();
		
	}
	
	private void checarFimDeJogo() {
		
		if (jogador1.pecas.isEmpty()) {
			
			status = "finalizada";
			
			vencedor = jogador1.nome;
			
			RankingDAO.registrarResultado(jogador1.nome, true);
			
			RankingDAO.registrarResultado(jogador2.nome, false);
			
			return;
			
		}
		
		if (jogador2.pecas.isEmpty()) {
			
			status = "finalizada";
			
			vencedor = jogador2.nome;
			
			RankingDAO.registrarResultado(jogador2.nome, true);
			
			RankingDAO.registrarResultado(jogador1.nome, false);
			
			return;
			
		}
		
		if (jogador1.passou && jogador2.passou) {
			
			int soma1 = jogador1.pecas.stream()
									  .mapToInt(Peca::soma)
									  .sum();
			
			int soma2 = jogador2.pecas.stream()
									  .mapToInt(Peca::soma)
									  .sum();
			
			status = "finalizada";
			
			if (soma1 == soma2) vencedor = "Empate";
			else if (soma1 < soma2) {
				
				vencedor = jogador1.nome;
				RankingDAO.registrarResultado(jogador1.nome, true);
				RankingDAO.registrarResultado(jogador2.nome, false);
				
			} else {
				
				vencedor =  jogador2.nome;
				RankingDAO.registrarResultado(jogador2.nome, true);
				RankingDAO.registrarResultado(jogador1.nome, false);
				
			}
			
		}
		
	}
	
	public Document toDocument() {
		
		return new Document("_id", id == null ? new ObjectId() : id)
				.append("mesa", mesa.stream()
									.map(Peca::toDocument)
									.collect(Collectors.toList()))
				.append("estoque", estoque.stream()
										  .map(Peca::toDocument)
										  .collect(Collectors.toList()))
				.append("jogador1", jogadorToDoc(jogador1))
				.append("jogador2", jogadorToDoc(jogador2))
				.append("turno", turnoAtual)
				.append("status", status)
				.append("vencedor", vencedor);
		
	}
	
	private Document jogadorToDoc(Jogador j) {
		
		Document doc = new Document("nome", j.nome);
		
		List<Document> pecasDoc = j.pecas.stream().map(Peca::toDocument).collect(Collectors.toList());
		
		doc.append("pecas", pecasDoc);
		
		doc.append("passou", j.passou);
		
		return doc;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Partida fromDocument(Document d) {
		
		Partida p = new Partida();
		
		p.id = d.getObjectId("_id");
		
		p.mesa = ((List<Document>) d.get("mesa")).stream()
                .map(Peca::fromDocument)
                .collect(Collectors.toList());
		
		List<Document> estoqueDocs = (List<Document>) d.get("estoque");
		
		
		p.estoque = estoqueDocs.stream()
                .map(Peca::fromDocument)
                .collect(Collectors.toList());
		
		Document jogador1Doc = (Document) d.get("jogador1");
		
		p.jogador1 = docToJogador(jogador1Doc);
		
		Document jogador2Doc = (Document) d.get("jogador2");
		
		p.jogador2 = docToJogador(jogador2Doc);
		
		p.turnoAtual = d.getInteger("turno");
		
		p.status = d.getString("status");
		
		p.vencedor = d.getString("vencedor");
		
		return p;
		
	}
	
	@SuppressWarnings("unchecked")
    private static Jogador docToJogador(Document d) {

        Jogador j = new Jogador(d.getString("nome"));

        List<Document> pecasDocs = (List<Document>) d.get("pecas");

        List<Peca> pecas = pecasDocs.stream()
                                    .map(Peca::fromDocument)
                                    .collect(Collectors.toList());

        j.pecas.addAll(pecas);

        j.passou = d.getBoolean("passou");

        return j;
        
    }

}
